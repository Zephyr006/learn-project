package learn.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 分段查询，并最终聚合结果
 */
@Slf4j
public class AsyncExecUtils {

    private static final int MAX_THREADS = Math.min(Runtime.getRuntime().availableProcessors() * 2, 16);
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool(MAX_THREADS);
    private static final Supplier<String> TRACE_ID_SUPPLIER = () -> MDC.get("traceId"); //XXX
    private static final String TRACE_ID_KEY = "traceId";

    @PreDestroy
    public void preDestroy() {
        try {
            forkJoinPool.shutdown();
            if (!forkJoinPool.awaitTermination(5, TimeUnit.SECONDS)) {
                forkJoinPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            forkJoinPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 单线程串行执行分批任务
     * @param batchSize      单次查询最大数量
     * @param total          总数量
     * @param worker         具体执行的任务
     */
    public static <T> List<T> batchWork(int batchSize, int total, Worker<T> worker) throws ExecutionException {
        if (total == 0) {
            return Collections.emptyList();
        }
        // 如果总数小于阈值，直接执行
        if (total <= batchSize) {
            return worker.doWork(0, total);
        }

        List<T> result = new ArrayList<>();
        int start = 0, end = batchSize;
        while (start < total) {
            List<T> ts = worker.doWork(start, end);
            result.addAll(ts);
            start = end;
            end  = Math.min(total, end + batchSize);
        }
        return result;
    }

    /**
     * @param batchSize      单次查询最大数量
     * @param total          总数量
     * @param worker         具体执行的任务
     */
    public static <T> List<T> forkJoinWork(int batchSize, int total, Worker<T> worker) throws ExecutionException {
        if (total == 0) {
            return Collections.emptyList();
        }
        // 如果总数小于阈值，直接执行
        if (total <= batchSize) {
            return worker.doWork(0, total);
        }
        String traceId = TRACE_ID_SUPPLIER.get();
        Task<T> task = new Task<>(0, total, batchSize, worker, traceId);
        Future<List<T>> result = forkJoinPool.submit(task);
        List<T> ts = null;
        try {
            ts = result.get();
        } catch (InterruptedException interruptedException) {
            log.warn("ForkJoinUtils执行并发任务时失败Worker：{}", worker.getClass(), interruptedException);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("ForkJoinUtils执行并发任务时失败Worker：{}", worker.getClass(), e);
        }
        return ts;
    }


    private static class Task<T> extends RecursiveTask<List<T>> {
        /** 阈值 */
        private final int batchSize;
        /** 开始 */
        private final int start;
        /** 结束 */
        private final int end;

        private final String traceId;

        private final transient Worker<T> worker;

        Task(int start, int end, int batchSize, Worker<T> worker, String traceId) {
            this.batchSize = batchSize;
            this.start = start;
            this.end = end;
            this.worker = worker;
            this.traceId = traceId;
        }

        @Override
        protected List<T> compute() {
            MDC.put(TRACE_ID_KEY, traceId);
            // RpcContext.getContext().setAttachment("_trace_id_", traceId);
            List<T> result = null;
            if (end <= start) {
                return new ArrayList<>();
            } else if ((end - start) <= batchSize) {
                log.debug("执行ForkJoin任务,分段start:{},end:{}", start, end);
                try {
                    result = worker.doWork(start, end);
                } catch (Exception e) {
                    log.error("执行ForkJoin任务发生异常,分段start:{},end:{}", start, end, e);
                }
            } else {
                int middle = (start + end) / 2;
                Task<T> leftTask = new Task<>(start, middle, batchSize, worker, traceId);
                Task<T> rightTask = new Task<>(middle, end, batchSize, worker, traceId);
                leftTask.fork();
                rightTask.fork();
                List<T> rightResult = rightTask.join();
                List<T> leftResult = leftTask.join();
                leftResult.addAll(rightResult);
                result = leftResult;
            }
            return result;
        }

    }

    public interface Worker<T> {
        List<T> doWork(int start, int end) throws ExecutionException;
    }

    public static void main(String[] args) throws ExecutionException {
        List<Integer> collect = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        List<Integer> result = batchWork(5, collect.size(), new Worker<Integer>() {
            @Override
            public List<Integer> doWork(int start, int end) {
                return IntStream.range(start, end).boxed().collect(Collectors.toList());
            }
        });
        assert result.size() == collect.size();
        assert result.containsAll(collect);
        System.out.println(result);
    }
}
