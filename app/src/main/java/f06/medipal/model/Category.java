package f06.medipal.model;

import f06.medipal.dao.Column;

/**
 * Created by Admin on 3/22/2017.
 */

public class Category extends Model {

    @Column(type = Column.VARCHAR, length = 50)
    public String Category;

    @Column(type = Column.VARCHAR, length = 5)
    public String Code;

    @Column(type = Column.VARCHAR, length = 255)
    public String Description;

    @Column(type = Column.VARCHAR, length = 1)
    public String Remind;

    public Category() {

    }

    public Category(String category, String code, String description, String remind) {
        Category = category;
        Code = code;
        Description = description;
        Remind = remind;
    }
}
