package learn.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zephyr
 * @date 2022/1/1.
 */
public class StringUtils {
    protected static final Pattern UNDERLINE_PATTERN = Pattern.compile("_(.)");
    protected static final Pattern UPPERCASE_PATTERN = Pattern.compile("([A-Z])");

    /**
     * 下划线转驼峰
     */
    public static String underlineToCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        Matcher matcher = UNDERLINE_PATTERN.matcher(str);
        if (matcher.find()) {
            StringBuilder sb = new StringBuilder();
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '_') {
                    sb.append(Character.toUpperCase(chars[++i]));
                } else {
                    sb.append(chars[i]);
                }
            }
            return sb.toString();
        }
        return str;
    }

    /**
     * 驼峰转下划线
     */
    public static String camelCaseToUnderline(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        Matcher matcher = UPPERCASE_PATTERN.matcher(str);
        if (matcher.find()) {
            StringBuilder sb = new StringBuilder();
            char[] chars = str.toCharArray();
            for (char aChar : chars) {
                if (Character.isUpperCase(aChar)) {
                    sb.append('_').append(Character.toLowerCase(aChar));
                } else {
                    sb.append(aChar);
                }
            }
            return sb.toString();
        }
        return str;
    }
}
