package f06.medipal.model;

import f06.medipal.dao.Column;

/**
 * Created by Vu Le on 3/10/2017.
 */

public class ICE extends Model {
    @Column(type = Column.VARCHAR, length = 100)
    public String Name;

    @Column(type = Column.VARCHAR, length = 20)
    public String ContactNo;

    @Column(type = Column.INT)
    public int ContactType;

    @Column(type = Column.VARCHAR, length = 255)
    public String Description;

    public ICE() {

    }

    //Generated constructor
    public ICE(String name, String contactNo, int contactType, String description) {
        Name = name;
        ContactNo = contactNo;
        ContactType = contactType;
        Description = description;
    }
}
