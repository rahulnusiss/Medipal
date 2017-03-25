package f06.medipal.model;

import java.util.Date;

import f06.medipal.dao.Column;

public class Measurement extends Model {
    @Column(type = Column.INT)
    public int Systolic;

    @Column(type = Column.INT)
    public int Diastolic;

    @Column(type = Column.INT)
    public int Pulse;

    @Column(type = Column.DOUBLE)
    public double Temperature;

    @Column(type = Column.INT)
    public int Weight;

    @Column(type = Column.DATETIME)
    public Date MeasuredOn;

    public Measurement() {

    }

    //Generated constructor
    public Measurement(int systolic, int diastolic, int pulse, double temperature, int weight, Date measuredOn) {
        Systolic = systolic;
        Diastolic = diastolic;
        Pulse = pulse;
        Temperature = temperature;
        Weight = weight;
        MeasuredOn = measuredOn;
    }
}
