package learn.leetcode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zephyr
 * @since 2022-04-23.
 */
public class LeetcodeHelper {

    /**
     * 字符串转二维数组，字符串示例如下
     * <pre> {@code
     *     [
     *   [1,3,1],
     *   [1,5,1],
     *   [4,2,1]
     * ]
     * }</pre>
     */
    public static int[][] parse2DIntArray(String str) {
        List<Integer[]> arrayList = JSONArray
            .parseArray(str.replace("\n", ""), Integer[].class);
        int[][] result = new int[arrayList.size()][arrayList.get(0).length];
        for (int i = 0; i < arrayList.size(); i++) {
            // Integer数组转为int数组
            result[i] = Arrays.stream(arrayList.get(i)).mapToInt(Integer::valueOf).toArray();
        }
        return result;
    }

    public static char[][] parse2DCharArray(String str) {
        if (str == null || str.isEmpty()) {
            return new char[0][0];
        }
        if (!str.startsWith("[") && !str.startsWith("]")) {
            str = "[" + str + "]";
        }
        JSONArray jsonArray = JSON.parseArray(str);

        char[][] charArray = new char[jsonArray.size()][];
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray innerArray = jsonArray.getJSONArray(i);
            char[] innerCharArray = new char[innerArray.size()];
            for (int j = 0; j < innerArray.size(); j++) {
                innerCharArray[j] = innerArray.getString(j).charAt(0);
            }
            charArray[i] = innerCharArray;
        }
        return charArray;
    }

    public static int[] toIntArray(String s) {
        if (s == null || s.isEmpty()) {
            return new int[0];
        }
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
        }
        try {
            return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
        } catch (Exception e) {
            return Arrays.stream(s.split(", ")).mapToInt(Integer::parseInt).toArray();
        }
    }

    // Java获取堆栈信息 https://blog.csdn.net/devcloud/article/details/136685119
    @SuppressWarnings("unchecked")
    public static void invokePublicMethods(Object... params) {
        // R result = null;
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            // 获取调用本方法的堆栈信息
            StackTraceElement stackTraceElement = stackTraceElements[stackTraceElements.length - 1];
            Class<?> prevClass = Class.forName(stackTraceElement.getClassName());
            Object invokeClassInstance = prevClass.newInstance();
            List<Method> methodNameMatch = new ArrayList<>(2);
            for (Method method : prevClass.getDeclaredMethods()) {
                // 寻找public非静态方法
                if (!Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                    methodNameMatch.add(method);
                }
            }

            for (Method method : methodNameMatch) {
                try {
                    method.setAccessible(true);
                    Object result = method.invoke(invokeClassInstance, params);
                    System.out.println("--> 调用方法【 " + method.getName() + " 】的执行结果为：" + JSON.toJSONString(result));
                } catch (Exception e) {
                    System.err.println("--> 调用方法【 " + method + " 】执行异常: "
                        + e.getClass().getName() + ":" + e.getMessage());
                }
            }

            // Method method = methodNameMatch.isEmpty() ? null : methodNameMatch.get(0);
            // if (methodNameMatch.size() > 1) {
            //     method = methodNameMatch.stream().filter(m -> m.getReturnType() != Void.class).findAny().get();
            // }
            // if (method != null) {
            //     System.out.printf("--> 实际调用了方法 %s\n", method);
            //     result = (R) method.invoke(invokeClassInstance, params);
            // }
            // System.out.println("--> 方法调用结果为：" + JSON.toJSONString(result));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // return result;
    }
}
