package f06.medipal.utils;
import java.util.ArrayList;
import java.util.Date;

import f06.medipal.model.AppointmentReminder;
import f06.medipal.model.Model;

/**
 * Created by namco on 19/3/17.
 */
public abstract class BaseReminderManager<T extends Model> {
    public abstract long createReminder(long eventId);
    public abstract int deleteReminder(int reminderId);
    public abstract ArrayList<T> getReminder(Date startDate, Date endDate);
    public abstract T getReminder(int reminderId);
    public abstract int updateReminder(AppointmentReminder appointmentReminder);

}
