package f06.medipal.appointment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.utils.CalendarUtils;
import f06.medipal.utils.TimePickerFragment;
import f06.medipal.model.Appointment;

import java.util.Date;


public class NewAppointmentActivity extends BaseAppointmentActivity implements
        EditAppointmentFragment.OnAppointmentCreatedListener, TimePickerFragment.OnTimePickedListener {

    private DBDAO<Appointment> mDao;
    private AppointmentReminderManager mAppointmentReminderManager;
    private Appointment mCurrentAppointment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_new);

        mDao = new DBDAO<>(this.getBaseContext(), Appointment.class);
        mAppointmentReminderManager = new AppointmentReminderManager(this.getBaseContext());
    }

    @Override
    public void onAppointmentCreated(Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
//            intent.putExtra(EditAppointmentFragment.APPOINTMENT_KEY, bundle);
            setCurrentAppointment(bundle);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                        REQ_CALENDAR_PERMISSION);
                return;
            }
            CalendarUtils.setPrimaryCalId(this);
            saveCurrentAppointment();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (checkCalendarPermission(requestCode, permissions, grantResults)) {
            CalendarUtils.setPrimaryCalId(this);
            saveCurrentAppointment();
            setResult(RESULT_OK, null);
            finish();
        } else {
            setResult(RESULT_CANCELED, null);
            finish();
        }
    }

    @Override
    public void onTimePicked(Date pickedDatetime) {
        EditAppointmentFragment editAppointmentFragment = (EditAppointmentFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fg_appointment_new);
        editAppointmentFragment.onTimePicked(pickedDatetime);
    }

    @Override
    public void setCurrentAppointment(Bundle bundle) {
        String description = bundle.getString(EditAppointmentFragment.DESCRIPTION_KEY);
        long appointmentDate = bundle.getLong(EditAppointmentFragment.DATETIME_KEY);
        String location = bundle.getString(EditAppointmentFragment.LOCATION_KEY);
        mCurrentAppointment = new Appointment(
                description,
                new Date(appointmentDate),
                location
        );
    }

    @RequiresPermission(allOf = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR})
    public void saveCurrentAppointment() {
        if (mCurrentAppointment == null) return;
        long eventId = addAppointmentEventToCalender(
                this,
                mCurrentAppointment.description,
                mCurrentAppointment.appointment.getTime(),
                mCurrentAppointment.location);
        int reminderId = (int) mAppointmentReminderManager.createReminder(eventId);
        mCurrentAppointment.reminderId = reminderId;
        mDao.save(mCurrentAppointment);
    }

}
