package learn.base.utils;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/4/30.
 */
public class ResultSetUtils {

    public static <T> Collection<T> parseCollection(final ResultSet resultSet, final Collection<T> collection, Class<T> targetClass) {
        return parseCollection(resultSet, collection, targetClass, true);
    }

    /**
     * @param collection      存放结果的集合
     * @param underline_style 数据库字段是否为下划线命名风格
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> parseCollection(final ResultSet resultSet, final Collection<T> collection, Class<T> targetClass, boolean underline_style) {
        try {
            //Class<?> targetClass = target.getClass();
            Map<String, Method> methodMap = Arrays.stream(targetClass.getMethods())
                    .filter(method -> method.getName().startsWith("set"))
                    .collect(Collectors.toMap(Method::getName, Function.identity()));
            while (resultSet.next()) {
                collection.add((T) parseObject(resultSet, underline_style, targetClass, methodMap));
            }
            return collection;
        } catch (InstantiationException | IllegalAccessException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static <T> Object parseObject(final ResultSet resultSet, final boolean underline_style, final Class<T> targetClass, final Map<String, Method> methodMap) throws InstantiationException, IllegalAccessException, InvocationTargetException, SQLException {
        Object newInstance = targetClass.newInstance();
        for (Field field : targetClass.getDeclaredFields()) {
            String fieldName = field.getName();
            String setterMethodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method writeMethod = methodMap.get(setterMethodName);

            if (writeMethod != null) {
                Class<?> paramClass = writeMethod.getParameterTypes()[0];
                if (paramClass == int.class || paramClass == Integer.class) {
                    writeMethod.invoke(newInstance, underline_style ? resultSet.getInt(toUnderline(fieldName)) : resultSet.getInt(fieldName));
                }
                else if (paramClass == long.class || paramClass == Long.class) {
                    writeMethod.invoke(newInstance, underline_style ? resultSet.getLong(toUnderline(fieldName)) : resultSet.getLong(fieldName));
                }
                else if (paramClass == boolean.class || paramClass == Boolean.class) {
                    writeMethod.invoke(newInstance, underline_style ? resultSet.getBoolean(toUnderline(fieldName)) : resultSet.getBoolean(fieldName));
                }
                else if (paramClass == String.class) {
                    writeMethod.invoke(newInstance, underline_style ? resultSet.getString(toUnderline(fieldName)) : resultSet.getString(fieldName));
                }
            }
        }
        return newInstance;
    }

    public static Collection<Long> parseLongCollection(final ResultSet resultSet, final Collection<Long> collection, int index) {
        try {
            while (resultSet.next()) {
                collection.add(resultSet.getLong(index));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collection;
    }

    private static String toCamelCase(String str) {
        int indexOfUnderscore = str.indexOf("_");
        if (StringUtils.isBlank(str) || indexOfUnderscore == -1) {
            return str;
        }

        StringBuilder stringBuilder = new StringBuilder(str.substring(0, indexOfUnderscore));
        char[] charArray = str.toCharArray();
        for (int i = indexOfUnderscore; i < charArray.length; i++) {
            if (charArray[i] == '_') {
                stringBuilder.append(Character.toUpperCase(charArray[++i]));
            } else {
                stringBuilder.append(charArray[i]);
            }
        }
        return stringBuilder.toString();
    }
    private static String toUnderline(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        StringBuilder stringBuilder = new StringBuilder();
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                stringBuilder.append('_');
                stringBuilder.append(Character.toLowerCase(c));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

}
