package f06.medipal.reminder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Consumption;
import f06.medipal.model.Medicine;

import java.util.Date;

public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String TAKEN_ACTION = "f06.medipal.reminder.action.TAKEN_ACTION";
    public static final String SKIP_ACTION = "f06.medipal.reminder.action.SKIP_ACTION";
    private DBDAO<Medicine> mMedicineDAO;
    private DBDAO<Consumption> mConsumptionDAO;

    public NotificationActionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(MedicineNotificationPublisher.NOTIFICATION_ID, -1);
        if (notificationId != -1) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
        String action = intent.getAction();
        int medicineId = intent.getIntExtra(MedicineNotificationPublisher.MEDICINE_ID, -1);
        if (medicineId == -1) return;
        mMedicineDAO = new DBDAO<>(context, Medicine.class);
        Medicine medicine = mMedicineDAO.getRecord(medicineId);
        if(TAKEN_ACTION.equals(action)) {
            handleTaken(context, medicine);
        } else if(SKIP_ACTION.equals(action)) {
            handleSkip(context, medicine);
        }
    }

    private void handleTaken(Context context, Medicine medicine) {
        mConsumptionDAO = new DBDAO<>(context, Consumption.class);
        Consumption consumption = new Consumption(
                medicine.ID,
                medicine.consumeQuantity,
                new Date(System.currentTimeMillis())
        );
        mConsumptionDAO.save(consumption);
    }

    private void handleSkip(Context context, Medicine medicine) {}
}
