package f06.medipal.reminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import f06.medipal.medicine.MedicineDetailsActivity;
import f06.medipal.R;
import f06.medipal.constants.Dosage;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Consumption;
import f06.medipal.model.Medicine;
import f06.medipal.model.Reminder;

import static f06.medipal.reminder.MedicineNotificationPublisher.MEDICINE_ID;
import static f06.medipal.reminder.MedicineNotificationPublisher.REMINDER_ID;
import static f06.medipal.reminder.MedicineNotificationPublisher.REQUEST_CODE;
import static f06.medipal.reminder.MedicineNotificationPublisher.getNotificationContent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 */
public class OnAppStartCheckMedicineService extends IntentService {


    private static final String ACTION_CHECK_MEDICINE = "f06.medipal.reminder.action.CHECK_MEDICINE";
    private static final String TAG = OnAppStartCheckMedicineService.class.getSimpleName();
    private DBDAO<Medicine> mMedicineDAO;
    private DBDAO<Consumption> mConsumptionDAO;
    private DBDAO<Reminder> mReminderDAO;

    public OnAppStartCheckMedicineService() {
        super("OnAppStartCheckMedicineService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startCheckMedicine(Context context) {
        Intent intent = new Intent(context, OnAppStartCheckMedicineService.class);
        intent.setAction(ACTION_CHECK_MEDICINE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_MEDICINE.equals(action)) {
                handleCheckMedicine(getBaseContext());
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleCheckMedicine(Context context) {
        mMedicineDAO = new DBDAO<>(context, Medicine.class);
        ArrayList<Medicine> allMedicines = mMedicineDAO.getRecords();

        checkThreshold(context, allMedicines);
        checkExpire(context, allMedicines);

        mMedicineDAO.close();
        mConsumptionDAO.close();
    }

    private void checkThreshold(Context context, ArrayList<Medicine> medicines) {
        mConsumptionDAO = new DBDAO<>(context, Consumption.class);
        for (Medicine medicine : medicines) {
            ArrayList<Consumption> consumptions = mConsumptionDAO.getRecords();
            int consumedQuantity = 0;
            for (Consumption consumption : consumptions) {
                consumedQuantity += consumption.Quantity;
            }
            int leftQuantity = medicine.quantity - consumedQuantity;
            if (leftQuantity <= medicine.threshold) {
                String title = "It's time to replenish " + medicine.medicine;
                publishReplenishNotification(context, medicine, title, leftQuantity);
            }
            // Cancel the alarm manager and stop the notification
            if (medicine.remind.equals("Y") && leftQuantity <= 0) {
                cancelMedicineAlarmManager(context, medicine);
            }
        }
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

    private void checkExpire(Context context, ArrayList<Medicine> medicines) {
        for (Medicine medicine : medicines) {
            Calendar dateIssued = Calendar.getInstance();
            dateIssued.setTime(medicine.dateIssued);
            Calendar dateOneMonthBeforeExpired = (Calendar) dateIssued.clone();
            dateOneMonthBeforeExpired.add(Calendar.MONTH, medicine.expireFactor - 1);
            if (Calendar.getInstance().after(dateOneMonthBeforeExpired)) {
                publishExpiredNotification(context, medicine,  medicine.medicine + " Expire Warning");
            }
        }
    }

    private void publishReplenishNotification(Context context, Medicine medicine, String title, int leftQuantity) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = getNotification(context, title, getReplenishNotificationContent(medicine, leftQuantity));
        int notificationId = (int) System.currentTimeMillis();
        Notification notification = MedicineNotificationPublisher.getNotification(context, getNotificationContent(medicine), medicine, notificationId);
        notificationManager.notify(notificationId, notification);

    }

    private void publishExpiredNotification(Context context, Medicine medicine, String title) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = getNotification(context, title, getExpiredNotificationContent(medicine), medicine);
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notification);

    }

    private String getReplenishNotificationContent(Medicine medicine, int leftQuantity) {
        return medicine.medicine + " only left " + leftQuantity + " " + Dosage.Dosages[medicine.dosage];

    }

    private String getExpiredNotificationContent(Medicine medicine) {
        return medicine.medicine + " is going to expire in less than a month.";
    }

    private Notification getNotification(Context context, String title, String content, Medicine medicine) {
        Intent intent = new Intent(context, MedicineDetailsActivity.class);
        intent.putExtra("ID", medicine.ID);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MedicineDetailsActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_ONE_SHOT);
        Notification n  = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.medipal_icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true).build();
        return n;
    }

}
