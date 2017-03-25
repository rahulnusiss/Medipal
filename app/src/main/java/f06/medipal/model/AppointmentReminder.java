package f06.medipal.model;

import f06.medipal.dao.Column;

/**
 * Created by namco on 19/3/17.
 */
public class AppointmentReminder extends Model {
    @Column(type = Column.LONG)
    public long eventId;

    public AppointmentReminder() {}

    public AppointmentReminder(long eventId) {
        this.eventId = eventId;
    }

    public AppointmentReminder(long eventId, int id) {
        this.eventId = eventId;
        this.ID = id;
    }
}
