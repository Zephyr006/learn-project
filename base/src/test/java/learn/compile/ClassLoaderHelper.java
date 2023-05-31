package learn.compile;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * @author Zephyr
 * @since 2023-05-30
 */
public class ClassLoaderHelper {
    // eg. 7 / 8 / 9 / 11
    private static final String latestVersion = SourceVersion.latest().name().substring(SourceVersion.latest().name().lastIndexOf('_') + 1);

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException,
        InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        String sourceCode = "public class TestLoadClass {\n" +
            "    public String testMethod() {\n" +
            "        String s = \"load outer class success !\";\n" +
            "        System.out.println(s);\n" +
            "        return s;\n" +
            "    }\n" +
            "}";

        // 编译源代码
        ClassLoaderHelper.compileSourceCode(sourceCode);
        // ClassLoaderHelper.compileJavaFile("/Users/wangshidong/Desktop/TestLoadClass.java");

        // 加载类到内存
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
        Class<?> aClass = Class.forName("TestLoadClass", true, classLoader);

        // 反射调用类中的方法
        assert aClass != null;
        Object instance = aClass.newInstance();
        Method method = aClass.getMethod("testMethod");
        Object result = method.invoke(instance);
        assert "load outer class success !".equals(result);
    }


    /**
     * 编译java源文件,不支持编译源码
     */
    public static void compileJavaFile(String... javaFileAbsolutePath) {
        //获取系统的java编译器
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        // 这里可以指定javac命令中的可选配置,参照下面的格式
        String[] ops = {"-source", latestVersion};
        String[] arguments = new String[javaFileAbsolutePath.length + ops.length];
        System.arraycopy(ops, 0, arguments, 0, ops.length);
        System.arraycopy(javaFileAbsolutePath, 0, arguments, ops.length, javaFileAbsolutePath.length);
        int result = javaCompiler.run(System.in, System.out, System.err, arguments);
        if (result != 0) {
            throw new RuntimeException("Compilation failed, errorCode = " + result);
        }
    }

    /**
     * 使用Java Compiler API编译源码文本内容,适用于编译从数据库中读取到的源码内容
     * javac命令默认情况下只能编译源代码*文件*，而不能直接编译源代码*文本*,需要借助Java Compiler API中的JavaFileManager类和MemoryFileManager类
     */
    public static void compileSourceCode(String sourceCodeWithComments) {
        String sourceCode = removeComments(sourceCodeWithComments);
        //java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        // try (MemoryJavaFileManager memoryJavaFileManager = new MemoryJavaFileManager(fileManager)) {
        JavaFileObject javaFileObject = new SourceCodeJavaFileObject("", sourceCode);
        JavaCompiler.CompilationTask compilerTask = compiler.getTask(
            null, null, null, null, null, Arrays.asList(javaFileObject));
        if (!compilerTask.call()) {
            throw new RuntimeException("Compilation failed ");
        }
    }

    /**
     * 从源码中去除单行注释"//"和多行注释 (javadoc注释也会被去除)
     */
    private static String removeComments(String sourceCode) {
        StringBuilder sb = new StringBuilder();
        boolean inComment = false;
        for (int i = 0; i < sourceCode.length(); i++) {
            char c = sourceCode.charAt(i);
            if (inComment) {
                // '*/'
                if (c == '*' && i < sourceCode.length() - 1 && sourceCode.charAt(i + 1) == '/') {
                    inComment = false;
                    i++;
                }
            } else {
                if (c == '/') {
                    // Single-line comment, skip to end of line
                    if (i < sourceCode.length() - 1 && sourceCode.charAt(i + 1) == '/') {
                        i = sourceCode.indexOf('\n', i);
                        if (i == -1) {
                            break;
                        }
                        sb.append('\n');

                    // Multi-line comment, skip to end of comment
                    } else if (i < sourceCode.length() - 1 && sourceCode.charAt(i + 1) == '*') {
                        inComment = true;
                        i++;
                    } else {
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

}
