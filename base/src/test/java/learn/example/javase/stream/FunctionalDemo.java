package learn.example.javase.stream;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * java8 新增的函数接口使用示例
 *
 * Function<T,R>
 * 接受一个输入参数，返回一个结果。
 *
 * Consumer<T>
 * 代表了接受一个输入参数并且无返回的操作
 *
 * Supplier<T>
 * 无参数，返回一个结果。
 *
 * Predicate<T>
 * 接受一个输入参数，返回一个布尔值结果。
 *
 * BiConsumer<T,U>
 * 代表了一个接受两个输入参数的操作，并且不返回任何结果
 *
 * BiFunction<T,U,R>
 * 代表了一个接受两个输入参数的方法，并且返回一个结果
 *
 * BiPredicate<T,U>
 * 代表了一个两个参数的boolean值方法
 *
 * BinaryOperator<T>
 * 代表了一个作用于于两个同类型操作符的操作，并且返回了操作符同类型的结果
 *
 * UnaryOperator<T>
 * 接受一个参数为类型T,返回值类型也为T。
 *
 * 其他：带有前缀 Boolean/Int/Long/Double
 * 完整表单参见 @link https://www.runoob.com/java/java8-functional-interfaces.html
 *
 * @author Zephyr
 * @since 2020-11-19.
 */
public class FunctionalDemo {

    public static void main(String[] args) {
        //System.out.println("===== Function =====");
        Function<String, Dog> newDogFunction = Dog::new;
        Dog functionDog = newDogFunction.apply("狗的名字");


        //System.out.println("===== Supplier =====");
        Supplier<Dog> newDogSupplier = Dog::new;
        Dog supplierDog = newDogSupplier.get();


        System.out.println("===== Consumer =====");
        Consumer<String> consumer = System.out::println;
        consumer.accept("接收的数据被输出");


        System.out.println("===== Consumer =====");
        Dog dog = new Dog("狗h");
        Consumer<Dog> dogConsumer = Dog::dark;
        dogConsumer.accept(dog);


        System.out.println("===== Predicate =====");
        Predicate<Dog> predicate = Objects::nonNull;
        boolean nonNull = predicate.test(dog);
        System.out.println("there has a dog?  " + nonNull);


        System.out.println("===== BiFunction =====");
        BiFunction<Dog, Integer, Integer> eatFunction = Dog::eat;
        Integer left = eatFunction.apply(dog, 3);
        System.out.println("还剩下 " + left + " 斤狗粮");
    }


    static class Dog{
        private Integer food = 10;
        private String name;

        public Dog() {

        }
        public Dog(String name) {
            this.name = name;
        }

        public static void dark(Dog dog) {
            System.out.println(dog.name + " 叫了");
        }

        // 编译器默认行为：成员方法默认会把当前实例的引用传入到非静态方法，参数名为 this，位置为第一个参数
        public int eat(Dog this, int num) {
            System.out.println("狗吃了 " +num+ " 斤狗粮");
            this.food -= num;
            return this.food;
        }
        //public int eat(int num) {
        //    System.out.println("狗吃了 " +num+ " 斤狗粮");
        //    this.food -= num;
        //    return this.food;
        //}

    }
}
