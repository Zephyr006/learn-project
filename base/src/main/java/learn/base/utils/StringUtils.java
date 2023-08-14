package learn.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zephyr
 * @since 2022-01-01.
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

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return cs != null && cs.length() > 0;
    }

    /**
     * 用"{}"占位符格式化输出,尽量只用于测试
     */
    public static String formatByBracket(String format, Object... args) {
        if (format == null || format.length() < 2 || args == null || args.length == 0) {
            return format;
        }
        int argsIndex = 0, offset = 0;
        StringBuilder stringBuffer = new StringBuilder();
        char[] chars = format.toCharArray();
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == '}' && chars[i - 1] == '{') {
                if (argsIndex >= args.length) {
                    System.err.println("StringUtils::formatByBracket 占位符与参数个数不符");
                    break;
                }
                stringBuffer.append(chars, offset, i - offset - 1).append(String.valueOf(args[argsIndex++]));
                offset = i + 1;
                i++;
            }
        }
        if (offset < chars.length - 1 && offset != 0) {
            stringBuffer.append(chars, offset, chars.length - offset);
        }
        return offset == 0 ? format : stringBuffer.toString();
    }


}
