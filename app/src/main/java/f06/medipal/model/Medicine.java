package f06.medipal.model;

import java.util.Date;

import f06.medipal.dao.Column;

/**
 * Created by Prasanna on 3/11/2017.
 */

public class Medicine extends Model {

    @Column(type = Column.VARCHAR,length = 50)
    public String medicine;

    @Column(type = Column.VARCHAR,length = 255)
    public String description;

    @Column(type = Column.VARCHAR,length=1)
    public String remind;

    @Column(type = Column.INT)
    public int catID;

    @Column(type = Column.INT)
    public int reminderID;

    @Column(type = Column.INT)
    public int quantity;

    @Column(type = Column.INT)
    public int dosage;

    @Column(type = Column.DATETIME)
    public Date dateIssued;

    @Column(type = Column.INT)
    public int consumeQuantity;

    @Column(type = Column.INT)
    public int threshold;

    @Column(type = Column.INT)
    public int expireFactor;

    //default constructor
    public Medicine(){

    }

    public Medicine(String medicine, String description, String remind, int catID, int reminderID, int quantity, int dosage, Date dateIssued, int consumeQuantity, int threshold, int expireFactor) {
        this.medicine = medicine;
        this.description = description;
        this.remind = remind;
        this.catID = catID;
        this.reminderID = reminderID;
        this.quantity = quantity;
        this.dosage = dosage;
        this.dateIssued = dateIssued;
        this.consumeQuantity = consumeQuantity;
        this.threshold = threshold;
        this.expireFactor = expireFactor;
    }
}//class Medicine
