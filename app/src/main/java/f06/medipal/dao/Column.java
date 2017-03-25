package f06.medipal.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Column {
    public String type();
    public int length() default 0;

    static public String VARCHAR = "VARCHAR";
    static public String INT = "INT";
    static public String DOUBLE = "DOUBLE";
    static public String LONG = "LONG";
    static public String DATETIME = "DATETIME";
}