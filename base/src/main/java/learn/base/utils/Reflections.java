package learn.base.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Zephyr
 * @date 2021/4/17.
 */
public class Reflections {

    public static void main(String[] args) {
        try {
            System.out.println("接口实现类：");
            for (Class<JsonUtil> c : getAllAssignedClass(JsonUtil.class)) {
                System.out.println(c.getName());
            }
            System.out.println("子类：");
            for (Class<JsonUtil> c : getAllAssignedClass(JsonUtil.class)) {
                System.out.println(c.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取同一路径下所有子类或接口实现类
     * @apiNote 不能获取jar包中的类
     */
    public static <T> List<Class<T>> getAllAssignedClass(Class<T> clazz) throws ClassNotFoundException {
        List<Class<T>> classes = new ArrayList<>();
        for (Class<?> c : getClasses(clazz)) {
            if (clazz.isAssignableFrom(c) && !clazz.equals(c)) {
                classes.add((Class<T>)c);
            }
        }
        return classes;
    }


    /**
     * 取得当前类路径下的所有类
     */
    public static List<Class<?>> getClasses(Class<?> cls) throws ClassNotFoundException {
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(path);
        return getClasses(new File(Objects.requireNonNull(url).getFile()), pk);
    }


    /**
     * 迭代查找类
     */
    private static List<Class<?>> getClasses(File dir, String pk) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!dir.exists()) {
            return classes;
        }
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) {
                classes.addAll(getClasses(f, pk + "." + f.getName()));
            }
            String name = f.getName();
            if (name.endsWith(".class")) {
                classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
            }
        }
        return classes;
    }
}
