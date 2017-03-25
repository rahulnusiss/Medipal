package f06.medipal.reminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Medicine;
import f06.medipal.model.Reminder;

import java.util.Calendar;

import static f06.medipal.reminder.MedicineNotificationPublisher.MEDICINE_ID;
import static f06.medipal.reminder.MedicineNotificationPublisher.REMINDER_ID;
import static f06.medipal.reminder.MedicineNotificationPublisher.REQUEST_CODE;

/**
 * An {@link IntentService} subclass for handling the alarm manager for notification.
 *
 */
public class MedicineNotificationService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_INIT_NOTIFICATION = "f06.medipal.reminder.action.INIT_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION = "f06.medipal.reminder.action.CANCEL_NOTIFICATION";

    private static final String TAG = MedicineNotificationService.class.getSimpleName();
    private DBDAO<Reminder> mReminderDAO;
    private DBDAO<Medicine> mMedicineDAO;


    public MedicineNotificationService() {
        super("MedicineNotificationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startInitNotificationService(Context context, int medicineId) {
        Intent intent = new Intent(context, MedicineNotificationService.class);
        intent.setAction(ACTION_INIT_NOTIFICATION);
        intent.putExtra(MEDICINE_ID, medicineId);
        context.startService(intent);
    }

    public static void startCancelNotificationService(Context context, int medicineId) {
        Intent intent = new Intent(context, MedicineNotificationService.class);
        intent.setAction(ACTION_CANCEL_NOTIFICATION);
        intent.putExtra(MEDICINE_ID, medicineId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            int medicineId = intent.getIntExtra(MEDICINE_ID, -1);
            if (medicineId == -1) return;
            Context context = getBaseContext();
            mMedicineDAO = new DBDAO<>(context, Medicine.class);
            Medicine medicine = mMedicineDAO.getRecord(medicineId);
            if (ACTION_INIT_NOTIFICATION.equals(action)) {
                handleInitNotification(context, medicine);
            } else if (ACTION_CANCEL_NOTIFICATION.equals(action)) {
                handleCancelNotification(context, medicine);
            }
        }
    }

    private void handleCancelNotification(Context context, Medicine medicine) {
        cancelMedicineAlarmManager(context, medicine);
    }

    private void cancelMedicineAlarmManager(Context context, Medicine medicine) {
        mReminderDAO = new DBDAO<>(context, Reminder.class);
        Reminder reminder = mReminderDAO.getRecord(medicine.reminderID);
        if (reminder == null) return;
        Context applicationContext = getApplicationContext();
        AlarmManager alarmMgr = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < reminder.Frequency; i++) {
            Intent intent = new Intent(applicationContext, MedicineNotificationPublisher.class);
            intent.putExtra(MEDICINE_ID, medicine.ID);
            intent.putExtra(REMINDER_ID, medicine.reminderID);
            int requestCode = MedicineNotificationService.getPendingIntentRequestCode(medicine, i);
            intent.putExtra(REQUEST_CODE, requestCode);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(applicationContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                alarmMgr.cancel(alarmIntent);
            } catch (Exception e) {
                Log.w(TAG, "scheduleDailyAlarmManager: ", e);
            }
        }
    }


    /**
     * Handle action InitNotification in the provided background thread with the provided
     * parameters.
     */
    private void handleInitNotification(Context context, Medicine medicine) {
        mReminderDAO = new DBDAO<>(context, Reminder.class);

        Reminder reminder = mReminderDAO.getRecord(medicine.reminderID);
//        Reminder reminder = new Reminder(5, new Date(System.currentTimeMillis() + 60 * 1000), 1);
        if (reminder == null) return;
        int notifyFrequency = reminder.Frequency;
        Calendar[] calendars = new Calendar[reminder.Frequency];
        Calendar startDatetime = Calendar.getInstance();
        startDatetime.setTime(reminder.StartTime);
        calendars[0] = startDatetime;
        for (int i = 1; i < notifyFrequency; i++) {
            Calendar tmpCal = (Calendar) calendars[0].clone();
            tmpCal.add(Calendar.HOUR_OF_DAY, reminder.Interval);
            calendars[i] = tmpCal;
        }
        for (int i = 0; i < notifyFrequency; i++) {
            scheduleMedicineAlarmManager(medicine, calendars[i], getPendingIntentRequestCode(medicine, i));
        }
    }

    public static int getPendingIntentRequestCode(Medicine medicine, int serialNo) {
        return Integer.valueOf(String.valueOf(medicine.ID) + String.valueOf(serialNo));
    }

    private void scheduleMedicineAlarmManager(Medicine medicine, Calendar startTime, int requestCode) {
        Context applicationContext = getApplicationContext();
        Intent intent = new Intent(applicationContext, MedicineNotificationPublisher.class);
        intent.putExtra(MEDICINE_ID, medicine.ID);
        intent.putExtra(REMINDER_ID, medicine.reminderID);
        intent.putExtra(REQUEST_CODE, requestCode);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(applicationContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmMgr.cancel(alarmIntent);
        } catch (Exception e) {
            Log.w(TAG, "scheduleMedicineAlarmManager: ", e);
        }
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                alarmIntent);
        Log.i(TAG, "scheduleMedicineAlarmManager: Daily alarm at " + startTime.toString());
    }



}
