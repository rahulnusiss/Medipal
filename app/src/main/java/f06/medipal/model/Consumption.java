package f06.medipal.model;

import java.util.Date;

import f06.medipal.dao.Column;

/**
 * Created by huazhihao on 25/3/17.
 */

public class Consumption extends Model {

    @Column(type = Column.INT)
    public int MedicineID;

    @Column(type = Column.INT)
    public int Quantity;

    @Column(type = Column.DATETIME)
    public Date ConsumedOn;

    public Consumption() {

    }

    public Consumption(int medicineID, int quantity, Date consumedOn) {
        MedicineID = medicineID;
        Quantity = quantity;
        ConsumedOn = consumedOn;
    }
}
