package learn.light4j.util;

import com.networknt.config.Config;

import java.util.Map;

/**
 * 工具类：获取在"values.yaml"中定义的变量值
 *
 * @author Zephyr
 * @since 2020-12-01.
 */
public class LightConfigValues {

    // Define one of the injection value source "values.yaml" and list of exclusion config files
    private static final String CENTRALIZED_MANAGEMENT = "values";
    private static final Map<String, Object> valueMap = Config.getInstance().getDefaultJsonMapConfig(CENTRALIZED_MANAGEMENT);


    public static Object get(String key) {
        return valueMap == null ? null : valueMap.get(key);
    }

    public static String getAsString(String key) {
        return (String) get(key);
    }

    public static Long getAsLong(String key) {
        Object obj = get(key);
        assertNotNull(obj);
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        else if (obj instanceof Long) {
            return (Long) obj;
        } else {
            return Long.valueOf((String) obj);
        }
    }

    public static Integer getAsInteger(String key) {
        return (Integer) get(key);
    }

    static void assertNotNull(Object object) {
        assert object != null;
    }
}
