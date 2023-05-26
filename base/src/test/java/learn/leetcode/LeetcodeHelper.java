package learn.leetcode;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Method;

/**
 * @author Zephyr
 * @since 2023-5-24.
 */
public class LeetcodeHelper {

    public static void invokeSolutions(Class<?> solution, Object... params) {
        try {
            Object instance = solution.newInstance();
            for (Method method : solution.getMethods()) {
                // 判断这个method是不是由'Solution'类声明的(还是从父类继承来的),只执行Solution类中的方法
                if (!solution.equals(method.getDeclaringClass())) {
                    continue;
                }
                // Class<?> declaringClass = method.getDeclaringClass();
                // System.out.println(declaringClass);
                Object invokeResult = method.invoke(instance, params);
                if (invokeResult != null) {
                    System.out.printf("%n方法[%s]返回结果为:%s %n", method.getName(), JSON.toJSONString(invokeResult));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
