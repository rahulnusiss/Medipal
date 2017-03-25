package f06.medipal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import f06.medipal.model.Category;
import f06.medipal.model.Consumption;
import f06.medipal.model.HealthBio;
import f06.medipal.model.ICE;
import f06.medipal.model.Appointment;
import f06.medipal.model.AppointmentReminder;
import f06.medipal.model.Measurement;
import f06.medipal.model.Medicine;
import f06.medipal.model.PersonalBio;
import f06.medipal.model.Reminder;

import static f06.medipal.dao.DBDAO.formatter;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medipal.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DROP_TEMPLATE = "DROP TABLE IF EXISTS %s";
    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DataBaseHelper(context);
        return instance;
    }

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    private String createTableSQL(Class type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE " + type.getSimpleName() + "(");
        for (Field field : type.getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(Column.class);
            if (annotation instanceof Column) {
                Column column = (Column) annotation;
                buffer.append(String.format("%s %s%s,", field.getName(), column.type(),
                        column.length() == 0 ? "" : "(" + column.length() + ")"));
            }
        }
        buffer.append("ID INTEGER PRIMARY KEY AUTOINCREMENT)");
        String sql = buffer.toString();
        Log.d("createTableSQL", sql);
        return sql;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSQL(Measurement.class));
        db.execSQL(createTableSQL(ICE.class));
        db.execSQL(createTableSQL(PersonalBio.class));
        db.execSQL(createTableSQL(HealthBio.class));
        db.execSQL(createTableSQL(Appointment.class));
        db.execSQL(createTableSQL(AppointmentReminder.class));
        db.execSQL(createTableSQL(Category.class));
        db.execSQL(createTableSQL(Medicine.class));
        db.execSQL(createTableSQL(Reminder.class));
        db.execSQL(createTableSQL(Consumption.class));
//        db.execSQL(createTableSQL(Reminder.class));

        FakeMeasurements(db);
        FakeICE(db);
        //FakePersonalBio(db);
        FakeHealthBio(db);
        FakeCategory(db);
        FakeMedicine(db);
        FakeAppointment(db);
        //FakeConsumption(db);
        FakeMedicine2(db);
        FakeReminders(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DataBaseHelper.class.getName(),
                String.format("Upgrading database from version %s to %s", oldVersion, newVersion));
        db.execSQL(String.format(DROP_TEMPLATE, Measurement.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, ICE.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, PersonalBio.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, HealthBio.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, Appointment.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, AppointmentReminder.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, Medicine.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, Category.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, Reminder.class.getSimpleName()));
        db.execSQL(String.format(DROP_TEMPLATE, Consumption.class.getSimpleName()));
        onCreate(db);
    }

    @Override
    public void close() {
        super.close();
    }

    private void FakeMeasurements(SQLiteDatabase db) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        Date today = new Date();
        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            int diastolic = rand.nextInt(80, 100);
            int systolic = diastolic + rand.nextInt(10, 50);
            Date date = new Date(today.getTime() - (10 - i) * (1000 * 60 * 60 * 24));
            values.put("Systolic", systolic);
            values.put("Diastolic", diastolic);
            values.put("Pulse", rand.nextInt(80, 100));
            values.put("Temperature", rand.nextDouble(36.5, 37.5));
            values.put("Weight", rand.nextInt(70, 75));
            values.put("MeasuredOn", formatter.format(date));
            db.insert(Measurement.class.getSimpleName(), null, values);
        }
    }

    private void FakeICE(SQLiteDatabase db) {
        //Next of Kin
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Son");
            values.put("ContactNo", "111111");
            values.put("ContactType", 0);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Daughter");
            values.put("ContactNo", "111222");
            values.put("ContactType", 0);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Nephew");
            values.put("ContactNo", "111333");
            values.put("ContactType", 0);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Niece");
            values.put("ContactNo", "111444");
            values.put("ContactType", 0);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        //Doctors
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Doctor Jack");
            values.put("ContactNo", "222111");
            values.put("ContactType", 1);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Doctor John");
            values.put("ContactNo", "222222");
            values.put("ContactType", 1);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Name", "Doctor Tim");
            values.put("ContactNo", "222333");
            values.put("ContactType", 1);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
        //Emergency Numbers
        {
            ContentValues values = new ContentValues();
            values.put("Name", "911");
            values.put("ContactNo", "911");
            values.put("ContactType", 2);
            values.put("Description", "");
            db.insert(ICE.class.getSimpleName(), null, values);
        }
    }

    private void FakePersonalBio(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("Name", "Trung");
        values.put("Dob", formatter.format(new Date()));
        values.put("Idno", "1234123");
        values.put("Address", "21 Lower Kent Ridge Rd, Singapore");
        values.put("Postal", "119077");
        values.put("Height", "168");
        values.put("BloodType", "A");
        db.insert(PersonalBio.class.getSimpleName(), null, values);
    }


    private void FakeCategory(SQLiteDatabase db) {
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Category", "Supplement");
            values.put("Code", "SUP");
            values.put("Description", "User may opt to set reminder for consumption of supplement.");
            values.put("Remind", "O");
            db.insert(Category.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Category", "Chronic");
            values.put("Code", "CHR");
            values.put("Description", "This is to categorise medication for long- term/life-time consumption for diseases, i.e. diabetes, hypertension, heart regulation, etc.");
            values.put("Remind", "Y");
            db.insert(Category.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Category", "Incidental");
            values.put("Code", "INC");
            values.put("Description", "For common cold, flu or symptoms happen to be unplanned or subordinate conjunction with something and prescription from general practitioners.");
            values.put("Remind", "Y");
            db.insert(Category.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Category", "Complete Course");
            values.put("Code", "COM");
            values.put("Description", "This may applies to medication like antibiotics for sinus infection, pneumonia, bronchitis, acne, strep throat, cellulitis, etc.");
            values.put("Remind", "O");
            db.insert(Category.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Category", "Self Apply");
            values.put("Code", "SEL");
            values.put("Description", "To note down any self-prescribed or consume medication, i.e. applying band aids, balms, etc.");
            values.put("Remind", "O");
            db.insert(Category.class.getSimpleName(), null, values);
        }
    }

    private void FakeMedicine(SQLiteDatabase db) {
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Medicine", "Paracetamol");
            values.put("Description", "Fever cold cough");
            values.put("Remind", "N");
            values.put("reminderID", 0);
            values.put("catID", 2);
            values.put("Quantity", 50);
            values.put("Dosage", 4);
            values.put("DateIssued", formatter.format(date));
            values.put("ConsumeQuantity", 2);
            values.put("Threshold", 10);
            values.put("ExpireFactor", 2);
            db.insert(Medicine.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Medicine", "D cold Total");
            values.put("Description", "Cough Syrup");
            values.put("Remind", "N");
            values.put("reminderID", 0);
            values.put("catID", 3);
            values.put("Quantity", 100);
            values.put("Dosage", 2);
            values.put("DateIssued", formatter.format(date));
            values.put("ConsumeQuantity", 2);
            values.put("Threshold", 20);
            values.put("ExpireFactor", 3);
            db.insert(Medicine.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Medicine", "Ibrupifin");
            values.put("Description", "Sprain ");
            values.put("Remind", "N");
            values.put("reminderID", 0);
            values.put("catID", 4);
            values.put("Quantity", 30);
            values.put("Dosage", 1);
            values.put("DateIssued", formatter.format(date));
            values.put("ConsumeQuantity", 2);
            values.put("Threshold", 10);
            values.put("ExpireFactor", 1);
            db.insert(Medicine.class.getSimpleName(), null, values);
        }
    }

    private void FakeAppointment(SQLiteDatabase db) {
        {
            ContentValues values = new ContentValues();
            values.put("location", "NUH");
            values.put("appointment", "2017-05-01 00:00:00");
            values.put("description", "Ankle sprain");
            db.insert(Appointment.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("location", "UHC");
            values.put("appointment", "2017-06-02 00:00:00");
            values.put("description", "Stomach");
            db.insert(Appointment.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("location", "UHC");
            values.put("appointment", "2017-02-02 00:00:00");
            values.put("description", "Physical examination");
            db.insert(Appointment.class.getSimpleName(), null, values);
        }
    }


    private void FakeHealthBio(SQLiteDatabase db) {
        {
            ContentValues values = new ContentValues();
            values.put("Condition", "Pollen");
            values.put("StartDate", "2011-05-01 00:00:00");
            values.put("ConditionType", "A");
            db.insert(HealthBio.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Condition", "Peanut");
            values.put("StartDate", "2012-05-01 00:00:00");
            values.put("ConditionType", "A");
            db.insert(HealthBio.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Condition", "Pharyngitis");
            values.put("StartDate", "2013-05-01 00:00:00");
            values.put("ConditionType", "C");
            db.insert(HealthBio.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            values.put("Condition", "Rash");
            values.put("StartDate", "2014-05-01 00:00:00");
            values.put("ConditionType", "C");
            db.insert(HealthBio.class.getSimpleName(), null, values);
        }

    }

    private void FakeConsumption(SQLiteDatabase db) {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ContentValues values = new ContentValues();
                values.put("MedicineID", i);
                values.put("Quantity", i * 2);
                calendar.set(2017, 1, i * 2 + j, 10, 0);
                values.put("ConsumedOn", formatter.format(calendar.getTime()));
                db.insert(Consumption.class.getSimpleName(), null, values);
            }
        }


    }

    private void FakeMedicine2(SQLiteDatabase db) {
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Medicine", "Paracetamol");
            values.put("Description", "Fever cold cough");
            values.put("Remind", "N");
            values.put("reminderID", 1);
            values.put("catID", 2);
            values.put("Quantity", 50);
            values.put("Dosage", 4);
            values.put("DateIssued", formatter.format(date));
            values.put("ConsumeQuantity", 2);
            values.put("Threshold", 10);
            values.put("ExpireFactor", 2);
            db.insert(Medicine.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Medicine", "D cold Total");
            values.put("Description", "Cough Syrup");
            values.put("Remind", "N");
            values.put("reminderID", 1);
            values.put("catID", 3);
            values.put("Quantity", 100);
            values.put("Dosage", 2);
            values.put("DateIssued", formatter.format(date));
            values.put("ConsumeQuantity", 2);
            values.put("Threshold", 20);
            values.put("ExpireFactor", 3);
            db.insert(Medicine.class.getSimpleName(), null, values);
        }
        {
            ContentValues values = new ContentValues();
            Date date = new Date();
            values.put("Medicine", "Ibrupifin");
            values.put("Description", "Sprain ");
            values.put("Remind", "N");
            values.put("reminderID", 2);
            values.put("catID", 4);
            values.put("Quantity", 30);
            values.put("Dosage", 1);
            values.put("DateIssued", formatter.format(date));
            values.put("ConsumeQuantity", 2);
            values.put("Threshold", 10);
            values.put("ExpireFactor", 1);
            db.insert(Medicine.class.getSimpleName(), null, values);
        }
    }

    public void FakeReminders(SQLiteDatabase db) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 8);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, 02);
        cal.set(Calendar.DAY_OF_MONTH, 23);
        Date reminderTime = cal.getTime();

        {
            // Reminder 1
            ContentValues values = new ContentValues();
            values.put("Frequency", 4);
            values.put("StartTime", formatter.format(reminderTime));
            values.put("Interval", 4);
            db.insert(Reminder.class.getSimpleName(), null, values);
        }

        cal.set(Calendar.HOUR_OF_DAY, 06);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        reminderTime = cal.getTime();

        {
            //Reminder reminder2 = new Reminder(3, reminderTime, 5);
            ContentValues values = new ContentValues();
            values.put("Frequency", 3);
            values.put("StartTime", formatter.format(reminderTime));
            values.put("Interval", 5);
            db.insert(Reminder.class.getSimpleName(), null, values);
        }
    }
}
