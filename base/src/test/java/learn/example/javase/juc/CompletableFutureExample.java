package learn.example.javase.juc;

import learn.base.BaseTest;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Zephyr
 * @date 2021/4/6.
 */
public class CompletableFutureExample extends BaseTest {

    @Test
    public void testApi() throws InterruptedException, ExecutionException {
        if (!checkContext()) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // ForkJoinPool.getCommonPoolParallelism() == Runtime.getRuntime().availableProcessors() - 1
        Executor defaultExecutor = ForkJoinPool.getCommonPoolParallelism() > 1 ?
                ForkJoinPool.commonPool() : command -> new Thread(command).start();

        //返回一个新的CompletableFuture，它由在给定Executor中运行的runnable任务异步完成，有指定类型的返回值
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync");
            countDownLatch.countDown();
            return 1;
        }, defaultExecutor);

        //返回一个新的CompletableFuture，它由给定Executor中运行的任务在运行给定runnable后异步完成，没有返回值
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            System.out.println("runAsync");
            countDownLatch.countDown();
        }, defaultExecutor);

        // 当 CompletableFuture 的计算完成，或者抛出异常的时候，我们可以执行特定的 Action
        completableFuture.whenCompleteAsync(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer value, Throwable throwable) {
                if (throwable == null) {
                    System.out.println("whenComplete " + value);
                } else {
                    System.out.println(throwable.getMessage());
                }
            }
        }, defaultExecutor);

        // 同时进行计算和转换
        Double aDouble = completableFuture.handleAsync(new BiFunction<Integer, Throwable, Double>() {
            @Override
            public Double apply(Integer value, Throwable throwable) {
                return value * 2.0;
            }
        }, defaultExecutor).get();

        // 结果转换, 相当于stream.#map()
        completableFuture.thenApply(new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return integer * 3.0;
            }
        });

        // 消耗型 - Consumer
        completableFuture.thenAcceptAsync(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });

        // 不使用上一步的计算结果，直接执行其他操作
        completableFuture.thenRunAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("new runnable task");
            }
        });

        // 结果转换, 相当于stream.#flatMap()
        completableFuture.thenComposeAsync(new Function<Integer, CompletionStage<Integer>>() {
            @Override
            public CompletionStage<Integer> apply(Integer integer) {
                return CompletableFuture.completedFuture(integer).thenApply(integer2 -> integer2 + integer);
            }
        });

        // 合并两个阶段的结果并且返回值，将Bifunction同时作用于两个阶段的结果
        String s3 = completableFuture.thenCombine(CompletableFuture.completedFuture(2).thenApply(integer -> integer * 2), new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) {
                return String.valueOf(integer) + integer2;
            }
        }).get();

        // applyToEither: 将Function作用于两个已完成Stage的结果之一
        String s4 = completableFuture.applyToEither(CompletableFuture.completedFuture(99), new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                return String.valueOf(integer);
            }
        }).getNow("if not completed, return me");
        // acceptEither: 消费两个阶段的任意一个结果
        completableFuture.acceptEither(CompletableFuture.completedFuture(99), new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println("acceptEither result = " + integer);
            }
        });

        // allOf、anyOf : 使用任意一个或全部CompletableFuture
        boolean allDone = CompletableFuture.allOf(completableFuture, completableFuture2).isDone();
        // 返回一个新的CompletableFuture，它在任何给定的CompletableFutures完成时完成，并具有相同的结果。
        // 否则，如果异常完成，则返回的CompletableFuture也会这样做，并且CompletionException将此异常作为其原因
        CompletableFuture<Object> anyOfCompletableFuture = CompletableFuture.anyOf(completableFuture, completableFuture2);

        countDownLatch.await();
    }
}
