package f06.medipal.model;

import f06.medipal.dao.Column;

import java.util.Date;

/**
 * Created by namco on 11/3/17.
 */
public class Appointment extends Model {

    @Column(type = Column.VARCHAR, length = 100)
    public String location;

    @Column(type = Column.DATETIME)
    public Date appointment;

    @Column(type = Column.VARCHAR, length = 255)
    public String description;

    @Column(type = Column.INT)
    public int reminderId;

    public Appointment() {}

    public Appointment(String description, Date appointment, String location) {
        this.location = location;
        this.appointment = appointment;
        this.description = description;
    }

    public Appointment(String description, Date appointment, String location, int reminderId) {
        this.location = location;
        this.appointment = appointment;
        this.description = description;
        this.reminderId = reminderId;
    }

    public Appointment(String description, Date appointment, String location, int reminderId, int id) {
        this.location = location;
        this.appointment = appointment;
        this.description = description;
        this.reminderId = reminderId;
        this.ID = id;
    }
}
