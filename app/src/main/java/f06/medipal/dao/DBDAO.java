package f06.medipal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import f06.medipal.model.Model;

public class DBDAO<T extends Model> {
    protected SQLiteDatabase database;
    protected DataBaseHelper dbHelper;
    protected Context context;
    protected Class<T> type;

    protected static final String WHERE_ID_EQUALS = "ID=?";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat simple_formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    protected SQLiteDatabase getDB(){
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public DBDAO(Context context, Class<T> type) {
        this.context = context;
        this.type = type;
        dbHelper = DataBaseHelper.getHelper(context);
        open();
    }

    public void open() throws SQLException {
        if (dbHelper == null)
            dbHelper = DataBaseHelper.getHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database = null;
    }

    protected ContentValues toModelObject(T item) {
        ContentValues values = new ContentValues();

        try {
            for (Field field : type.getFields()) {
                Annotation annotation = field.getAnnotation(Column.class);
                if (annotation instanceof Column) {
                    Column column = (Column) annotation;
                    switch (column.type()) {
                        case Column.DATETIME:
                            values.put(field.getName(), formatter.format(field.get(item)));
                            break;
                        case Column.DOUBLE:
                            values.put(field.getName(), (double) field.get(item));
                            break;
                        case Column.LONG:
                            values.put(field.getName(), (long) field.get(item));
                            break;
                        case Column.INT:
                            if (!(field.getName() == "ID" && (int) field.get(item) == 0)) {
                                values.put(field.getName(), (int) field.get(item));
                            }
                            break;
                        case Column.VARCHAR:
                            values.put(field.getName(), String.valueOf(field.get(item)));
                            break;
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            Log.e("IllegalAccessException", ex.getMessage());
        }
        return values;
    }

    protected ArrayList<T> toDBObject(Cursor cursor) {
        ArrayList<T> items = new ArrayList<T>();
        try {
            while (cursor.moveToNext()) {
                T item = newItem();
                int i = 0;
                for (Field field : type.getFields()) {
                    Annotation annotation = field.getAnnotation(Column.class);
                    if (annotation instanceof Column) {
                        Column column = (Column) annotation;
                        switch (column.type()) {
                            case Column.DATETIME:
                                field.set(item, formatter.parse(cursor.getString(i)));
                                break;
                            case Column.DOUBLE:
                                field.set(item, cursor.getDouble(i));
                                break;
                            case Column.LONG:
                                field.set(item, cursor.getLong(i));
                                break;
                            case Column.INT:
                                field.set(item, cursor.getInt(i));
                                break;
                            case Column.VARCHAR:
                                field.set(item, cursor.getString(i));
                                break;
                        }
                    }

                    i++;
                }
                items.add(item);
            }
        } catch (IllegalAccessException ex) {
            Log.e("IllegalAccessException", ex.getMessage());
        } catch (ParseException ex) {
            Log.e("ParseException", ex.getMessage());
        }
        return items;
    }

    protected T newItem() {
        T item = null;
        try {
            item = type.newInstance();
        } catch (InstantiationException ex) {
            Log.e("InstantiationException", ex.getMessage());
        } catch (IllegalAccessException ex) {
            Log.e("IllegalAccessException", ex.getMessage());
        }
        return item;
    }

    public int save(T item) {
        return (int) getDB().insert(type.getSimpleName(), null, toModelObject(item));
    }

    public int update(T item) {
        int result = getDB().update(type.getSimpleName(), toModelObject(item),
                WHERE_ID_EQUALS, new String[]{String.valueOf(item.ID)});
        return result;
    }

    public int delete(T item) {
        return getDB().delete(type.getSimpleName(), WHERE_ID_EQUALS,
                new String[]{String.valueOf(item.ID)});
    }

    public int delete(int id) {
        return getDB().delete(type.getSimpleName(), WHERE_ID_EQUALS, new String[]{String.valueOf(id)});
    }

    public ArrayList<T> getRecords(String where, Object value, String operator, String orderby, boolean reverse) {
        StringBuffer buffer = new StringBuffer();
        for (Field field : type.getFields()) {
            buffer.append(field.getName() + ",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        String sql = String.format("SELECT %s FROM %s", buffer.toString(), type.getSimpleName()) +
                (where != null ? String.format(" WHERE %s %s '%s' ", where, operator, value) : "") +
                (orderby != null ? String.format(" ORDER BY %s %s", orderby, reverse ? "DESC" : "ASC") : "");
        Log.v("selectSQL", sql);
        Cursor cursor = getDB().rawQuery(sql, new String[]{});
        return toDBObject(cursor);
    }

    public ArrayList<T> getRecords() {
        return getRecords(null, null, null, null, false);
    }

    public ArrayList<T> getRecords(String where, Object value) {
        return getRecords(where, value, "=", null, false);
    }

    public ArrayList<T> getRecords(String orderby, boolean reverse) {
        return getRecords(null, null, null, orderby, reverse);
    }

    public T getRecord(int id) {
        StringBuffer buffer = new StringBuffer();
        for (Field field : type.getFields()) {
            buffer.append(field.getName() + ",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        String sql = "SELECT " + buffer.toString() + " FROM " + type.getSimpleName() + " WHERE ID = ?";
        Cursor cursor = getDB().rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor == null || cursor.getCount() <= 0) {
            return null;
        }

        ArrayList<T> listModel = toDBObject(cursor) ;
        if (0 < listModel.size())
        {
            return listModel.get(0);
        }
        return null;
    }

}

