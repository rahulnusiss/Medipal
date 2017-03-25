package f06.medipal.model;

import f06.medipal.dao.Column;

public class PersonalBio extends Model {
    @Column(type = Column.VARCHAR, length = 100)
    public String Name;

    @Column(type = Column.VARCHAR)
    public String Dob;

    @Column(type = Column.VARCHAR, length = 20)
    public String Idno;

    @Column(type = Column.VARCHAR, length = 100)
    public String Address;

    @Column(type = Column.VARCHAR, length = 10)
    public String Postal;

    @Column(type = Column.INT)
    public int Height;

    @Column(type = Column.VARCHAR, length = 10)
    public String BloodType;

    public PersonalBio() {

    }

    public PersonalBio(String name, String dob, String idno, String address, String postal, int height, String bloodtype ) {
        Name = name;
        Dob = dob;
        Idno = idno;
        Address = address;
        Postal = postal;
        Height = height;
        BloodType = bloodtype;
    }


}
