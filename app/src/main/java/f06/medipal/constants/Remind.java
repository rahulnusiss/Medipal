package f06.medipal.constants;

/**
 * Created by huazhihao on 24/3/17.
 */

public abstract class Remind {
    static public final String Yes = "Y";
    static public final String No = "N";
    static public final String Optional = "O";

    public static String translate(String flag) {
        switch (flag) {
            case Remind.Yes:
                return "Yes";
            case Remind.No:
                return "No";
            case Remind.Optional:
                return "Optional";
        }
        return null;
    }
}
