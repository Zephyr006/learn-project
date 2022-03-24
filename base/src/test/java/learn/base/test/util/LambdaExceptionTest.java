package learn.base.test.util;

import learn.base.utils.LambdaExceptionUtil;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zephyr
 * @date 2021/4/11.
 */
public class LambdaExceptionTest {

    @Test
    public void test_Consumer_with_checked_exceptions() throws IllegalAccessException, ClassNotFoundException {
        Stream.of("java.lang.Object", "java.lang.Integer", "java.lang.String")
                .forEach(LambdaExceptionUtil.rethrowConsumer(className -> System.out.println(Class.forName(className))));

        Stream.of("java.lang.Object", "java.lang.Integer", "java.lang.String")
                .forEach(LambdaExceptionUtil.rethrowConsumer(System.out::println));
    }

    @Test(expected = ClassNotFoundException.class)
    public void test_Function_with_checked_exceptions() throws ClassNotFoundException {
        List<Class> classes1
                = Stream.of("Object", "Integer2", "String")
                .map(LambdaExceptionUtil.rethrowFunction(className -> Class.forName("java.lang." + className)))
                .collect(Collectors.toList());

        List<Class> classes2
                = Stream.of("java.lang.Object", "java.lang.Integer", "java.lang.String")
                .map(LambdaExceptionUtil.rethrowFunction(Class::forName))
                .collect(Collectors.toList());
    }

    @Test
    public void test_Supplier_with_checked_exceptions() throws ClassNotFoundException {
        Collector.of(
                LambdaExceptionUtil.rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, StandardCharsets.UTF_8))),
                StringJoiner::add, StringJoiner::merge, StringJoiner::toString);
    }

    @Test
    public void test_uncheck_exception_thrown_by_method() {
        Class clazz1 = LambdaExceptionUtil.uncheck(() -> Class.forName("java.lang.String"));

        Class clazz2 = LambdaExceptionUtil.uncheck(Class::forName, "java.lang.String");
    }

    @Test (expected = ClassNotFoundException.class)
    public void test_if_correct_exception_is_still_thrown_by_method() {
        Class clazz3 = LambdaExceptionUtil.uncheck(Class::forName, "INVALID");
    }

}
