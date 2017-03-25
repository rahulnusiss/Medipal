package f06.medipal.model;

import java.util.Date;

import f06.medipal.dao.Column;

/**
 * Created by huazhihao on 24/3/17.
 */
public class Reminder extends Model {
    @Column(type = Column.INT)
    public int Frequency;

    @Column(type = Column.DATETIME)
    public Date StartTime;

    @Column(type = Column.INT)
    public int Interval;

    public Reminder() {

    }

    public Reminder(int frequency, Date startTime, int interval) {
        Frequency = frequency;
        StartTime = startTime;
        Interval = interval;
    }
}
