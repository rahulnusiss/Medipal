package f06.medipal.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Reminders;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

import java.util.TimeZone;

/**
 * Created by namco on 18/3/17.
 */
public class CalendarUtils {

    public static long PRIMARY_CAL_ID;
    public static boolean isCalendarIdSet = false;
    public static final String[] EVENT_PROJECTION = new String[] {
            Calendars._ID,                           // 0
    };
    private static final int PROJECTION_ID_INDEX = 0;
    private static final String TAG = CalendarUtils.class.getSimpleName();


    public static void setPrimaryCalId(Activity currentActivity) {
        if (isCalendarIdSet) return;
        String selection = "((" + Calendars.IS_PRIMARY + " = ?))";
        String[] selectionArgs = new String[] {"1"};
        ContentResolver contentResolver = currentActivity.getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(Calendars.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, Calendars._ID + " ASC");
        if(cursor.getCount() <= 0){
            cursor = contentResolver.query(Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, Calendars._ID + " ASC");
        }
        if (cursor.moveToNext()) {
            // Get the field values
            PRIMARY_CAL_ID = cursor.getLong(PROJECTION_ID_INDEX);
            isCalendarIdSet = true;
            Log.i(TAG, "setPrimaryCalId: PRIMARY_CAL_ID: " + PRIMARY_CAL_ID);
        }
        cursor.close();

    }

    public static long addEventToCalender(Activity currentActivity, String title, String description, String location,
                                          long startDate, long endDate) {
        ContentValues eventValues = new ContentValues();
        ContentResolver contentResolver = currentActivity.getApplicationContext().getContentResolver();

        eventValues.put(Events.CALENDAR_ID, PRIMARY_CAL_ID);
        eventValues.put(Events.TITLE, title);
        eventValues.put(Events.DESCRIPTION, description);
        eventValues.put(Events.EVENT_LOCATION, location);
        eventValues.put(Events.DTSTART, startDate);
        eventValues.put(Events.DTEND, endDate);
        eventValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri eventUri = contentResolver.insert(Events.CONTENT_URI, eventValues);

        long eventId = Long.parseLong(eventUri.getLastPathSegment());
        return eventId;
    }

    public static long addEventToCalender(Activity currentActivity, String title, String description, String location) {
        ContentValues eventValues = new ContentValues();
        ContentResolver contentResolver = currentActivity.getApplicationContext().getContentResolver();

        eventValues.put(Events.CALENDAR_ID, PRIMARY_CAL_ID);
        eventValues.put(Events.TITLE, title);
        eventValues.put(Events.DESCRIPTION, description);
        eventValues.put(Events.EVENT_LOCATION, location);
        eventValues.put(Events.ALL_DAY, 1);
        eventValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri eventUri = contentResolver.insert(Events.CONTENT_URI, eventValues);

        long eventId = Long.parseLong(eventUri.getLastPathSegment());
        return eventId;
    }

    public static long addEventToCalender(Activity currentActivity, String title, String description,
                                          String location, int reminderTime) {
        long eventId = addEventToCalender(currentActivity, title, description, location);
        addReminderToEvent(currentActivity, eventId, reminderTime);
        return eventId;
    }

    public static long addEventToCalender(Activity currentActivity, String title, String description, String location,
                                           long startDate, long endDate, int reminderTime) {
        long eventId = addEventToCalender(currentActivity, title, description, location, startDate, endDate);
        addReminderToEvent(currentActivity, eventId, reminderTime);
        return eventId;
    }

    public static void addReminderToEvent(Activity currentActivity, long eventId, int reminderTime) {
        ContentResolver contentResolver = currentActivity.getApplicationContext().getContentResolver();
        ContentValues reminderValues = new ContentValues();

        reminderValues.put(Reminders.EVENT_ID, eventId);
        reminderValues.put(Reminders.MINUTES, reminderTime);
        reminderValues.put(Reminders.METHOD, Reminders.METHOD_ALERT);

        Uri reminderUri = contentResolver.insert(Reminders.CONTENT_URI, reminderValues);
        Log.i("URI", reminderUri.toString());
    }

    public static int deleteEvent(Activity currentActivity, long eventId) {
        ContentResolver contentResolver = currentActivity.getApplicationContext().getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        return contentResolver.delete(deleteUri, null, null);
    }

    public static Cursor queryEvents(Activity currentActivity, long startDate, long endDate) {
        ContentResolver contentResolver = currentActivity.getApplicationContext().getContentResolver();
        String[] projection = new String[] { Events.CALENDAR_ID, Events._ID, Events.TITLE, Events.DESCRIPTION,
                Events.DTSTART, Events.DTEND, Events.EVENT_LOCATION };
        String selection = "((dtstart >= "+startDate+") AND (dtend <= "+endDate+"))";

        Cursor cursor = contentResolver.query(Events.CONTENT_URI, projection, selection, null, null);
        return cursor;
    }
}
