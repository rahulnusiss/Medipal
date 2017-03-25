package f06.medipal.appointment;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import f06.medipal.utils.CalendarUtils;

/**
 * Created by namco on 20/3/17.
 */
public abstract class BaseAppointmentActivity extends AppCompatActivity {
    static final String APPOINTMENT_TITLE = "Medical Appointment";
    static final int REQ_CALENDAR_PERMISSION = 0;

    abstract void setCurrentAppointment(Bundle bundle);

    static long addAppointmentEventToCalender(Activity activity, String description, long appointmentDate, String location) {
        long endDate = appointmentDate + 1000*60*60;
        long eventId = CalendarUtils.addEventToCalender(activity, APPOINTMENT_TITLE, description, location, appointmentDate, endDate);
        return eventId;
    }

    boolean checkCalendarPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
