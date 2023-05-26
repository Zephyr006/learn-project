package learn.example.javase.stream;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 流式操作使用示例
 *
 * 惰性求值 - 中间操作与终止操作：
 * 多个中间操作可以连接起来形成一个流水线，除非流水线上触发终止操作，否则中间操作不会执行任何处理！
 * 而在终止操作时一次性执行全部操作，称作“惰性求值”
 *
 * 中间操作：
 *    过滤（filter）、截断流（limit）、跳过元素（skip）、去重(筛选)（distinct）、
 *    映射（map、flatMap）、排序（sorted、unordered）、操作（peek、forEach）
 *
 * 终止操作：
 *    匹配与查找（allMatch、anyMatch、noneMatch、findFirst、findAny、count、max、min）、
 *    规约（reduce）、收集（collect）
 *
 *
 * @author Zephyr
 * @since 2020-11-19.
 */
public class StreamDemo {

    public static void main(String[] args) {

        // Stream 的创建
        // 1. 从集合或数组创建
        List<Integer> list = Arrays.asList(1,3,5);
        Stream<Integer> stream1 = list.stream();
        Stream<Integer> stream2 = list.parallelStream();

        Stream<String> stream5 = Stream.of("a", "b", "c");


        // 2. 数字 Stream：不需要自动装箱
        IntStream stream3 = IntStream.of(1, 2);
        IntStream stream4 = Arrays.stream(new int[]{1,2,3});


        // 3. 自己创建
        IntStream unlimitedStream = new Random().ints(); // 无限流
        IntStream stream6 = unlimitedStream.limit(10);   // 返回指定数量个随机数

        Random random = new Random();
        Stream<IntStream> stream7 = Stream.generate(random::ints);


        // 一些 api 的使用

        // peek 与 forEach：作用相同，peek 是中间操作，forEach 是终止操作
        Stream<Integer> peekStream = stream1.peek(System.out::println);
        peekStream.forEach(System.out::println);

        // reduce：按照指定的函数操作（参数函数）处理流中的每个元素
        Optional<String> reduceResult = stream5.reduce((e1, e2) -> e1 + e2);
        reduceResult.ifPresent(System.out::println);
        // count：统计流中元素的个数
        long count = stream3.count();

        // 并行计算

        //默认的线程数是当前机器 cpu 核心数，下面的可以修改默认线程数
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");
        long count1 = IntStream.range(1, 10).parallel().peek(System.out::println).count();

        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                long count2 = IntStream.range(1, 10).parallel().peek(System.out::println).count();
            }
        });
        forkJoinPool.shutdown();

        // 收集器
        // 统计数据：Collectors.sum...
        IntSummaryStatistics summaryStatistics = Stream.of(1, 10).collect(Collectors.summarizingInt(e -> (int) e));
        Integer sum = Stream.of(1, 10).collect(Collectors.summingInt(e -> e));
        Integer sum2 = IntStream.of(1, 10).sum();
        // 分块：Collectors.partitioningBy
        Map<Boolean, List<Integer>> partitionMap = Stream.of(1, 10).collect(Collectors.partitioningBy(e -> e > 5));
        // 分组：Collectors.groupingBy
        List<Dog> dogList = Arrays.asList(new Dog("zhangergou"), new Dog("恶狗"));
        Map<String, Long> groupByMap = dogList.stream().collect(Collectors.groupingBy(dog -> dog.name, Collectors.counting()));
        System.out.println(groupByMap);

    }

    static class Dog {
        private Integer food = 10;
        private String name;

        public Dog() {
        }

        public Dog(String name) {
            this.name = name;
        }
    }
}
