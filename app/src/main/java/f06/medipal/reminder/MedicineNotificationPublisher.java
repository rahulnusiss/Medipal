package f06.medipal.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import f06.medipal.R;
import f06.medipal.constants.Dosage;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Medicine;

/**
 * Created by GONG MENGNAN on 23/3/17.
 */
public class MedicineNotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "f06.medipal.reminder.extra.NOTIFICATION_ID";
    public static final String MEDICINE_ID = "f06.medipal.reminder.extra.MEDICINE_ID";
    public static final String REMINDER_ID = "f06.medipal.reminder.extra.REMINDER_ID";
    public static final String REQUEST_CODE = "f06.medipal.reminder.extra.REQUEST_CODE";


    private DBDAO<Medicine> mMedicineDAO;
    private static final String TAG = MedicineNotificationPublisher.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int medicineId = intent.getIntExtra(MEDICINE_ID, -1);
        int reminderId = intent.getIntExtra(REMINDER_ID, -1);
        int id = intent.getIntExtra(NOTIFICATION_ID, (int) System.currentTimeMillis());

        if (medicineId == -1 || reminderId == -1) {
            Log.w(TAG, "onReceive: Cannot get intent extras.");
        }
        mMedicineDAO = new DBDAO<>(context, Medicine.class);
        Medicine medicine = mMedicineDAO.getRecord(medicineId);
        if (medicine.reminderID != reminderId) {
            Log.w(TAG, "onReceive: Reminder ID in database: " + medicine.reminderID + " Reminder ID in intent: " + reminderId);
            return;
        }
        Notification notification = getNotification(context, getNotificationContent(medicine), medicine, id);
        notificationManager.notify(id, notification);

    }

    public static String getNotificationContent(Medicine medicine) {
        return "It's time to take " + medicine.consumeQuantity + " " + Dosage.Dosages[medicine.dosage] + " of " + medicine.medicine;
    }

    public static Notification getNotification(Context context, String content, Medicine medicine, int notificationId) {

        Intent takenIntent = new Intent(context, NotificationActionReceiver.class);
        takenIntent.putExtra(MEDICINE_ID, medicine.ID);
        takenIntent.putExtra(NOTIFICATION_ID, notificationId);
        takenIntent.setAction(NotificationActionReceiver.TAKEN_ACTION);
        PendingIntent pendingTakenIntent = PendingIntent.getBroadcast(
                context, (int) System.currentTimeMillis(), takenIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent skipIntent = new Intent(context, NotificationActionReceiver.class);
        skipIntent.putExtra(MEDICINE_ID, medicine.ID);
        skipIntent.putExtra(NOTIFICATION_ID, notificationId);
        skipIntent.setAction(NotificationActionReceiver.SKIP_ACTION);
        PendingIntent pendingSkipIntent = PendingIntent.getBroadcast(
                context, (int) System.currentTimeMillis(), skipIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification n  = new NotificationCompat.Builder(context)
                .setContentTitle("Take your medicine")
                .setContentText(content)
                .setSmallIcon(R.drawable.medipal_icon)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .addAction(android.R.drawable.ic_menu_add, "TAKEN", pendingTakenIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "SKIP", pendingSkipIntent).build();
        return n;
    }
}
