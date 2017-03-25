package f06.medipal.appointment;

import android.content.Context;

import f06.medipal.utils.BaseReminderManager;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.AppointmentReminder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by namco on 19/3/17.
 */
public class AppointmentReminderManager extends BaseReminderManager {

    private DBDAO<AppointmentReminder> dao;

    public AppointmentReminderManager(Context context) {
        dao = new DBDAO<>(context, AppointmentReminder.class);
    }

    @Override
    public long createReminder(long eventId) {
        AppointmentReminder appointmentReminder = new AppointmentReminder(eventId);
        return dao.save(appointmentReminder);
    }

    @Override
    public int deleteReminder(int reminderId) {
        return dao.delete(reminderId);
    }

    @Override
    public ArrayList getReminder(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public AppointmentReminder getReminder(int reminderId) {
        return dao.getRecord(reminderId);
    }

    @Override
    public int updateReminder(AppointmentReminder appointmentReminder) {
        return dao.update(appointmentReminder);
    }

}
