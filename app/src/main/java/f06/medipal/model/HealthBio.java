package f06.medipal.model;

import f06.medipal.dao.Column;

public class HealthBio extends Model {
    @Column(type = Column.VARCHAR, length = 255)
    public String Condition;

    @Column(type = Column.VARCHAR)
    public String StartDate;

    @Column(type = Column.VARCHAR, length = 1)
    public String ConditionType;

    public HealthBio(String condition, String startDate, String conditionType) {
        Condition = condition;
        StartDate = startDate;
        ConditionType = conditionType;
    }

    public HealthBio() {

    }

}
