package learn.base.utils;

/**
 * @author Zephyr
 * @since 2020-12-28.
 */
public class ValueParser {

    public static Long parseLong(Object value) {
        if (value == null)
            return null;
        if (value instanceof Integer) {
            return ((Integer)value).longValue();
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }


}
