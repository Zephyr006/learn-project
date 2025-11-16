package learn.leetcode;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 295. 数据流的中位数
 * https://leetcode.cn/problems/find-median-from-data-stream/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Hard295 {

    private static class MedianFinder {
        // 存储较大的数
        private PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.naturalOrder());
        // 存储较小的数
        private PriorityQueue<Integer> minHeap = new PriorityQueue<>(Comparator.reverseOrder());

        public MedianFinder() {

        }

        public void addNum(int num) {
            // 优先把数字放在较小的堆中，如果较小的堆中数量超过一半，则将较小的堆顶元素放入较大的堆
            if (minHeap.isEmpty() || num <= minHeap.peek()) {
                minHeap.offer(num);
                if (maxHeap.size() + 1 < minHeap.size()) {
                    maxHeap.offer(minHeap.poll());
                }
            // 如果数字大于较小的堆顶元素，则放入较大的堆，如果较大的堆中数量超过一半，则将较大的堆顶元素放入较小的堆
            } else {
                maxHeap.offer(num);
                if (maxHeap.size() > minHeap.size()) {
                    minHeap.offer(maxHeap.poll());
                }
            }
        }

        public double findMedian() {
            // 如果两个堆的数量不相等，则说明中位数在有序整数列表的中间（总数据量是奇数），中位数为较小的堆顶元素
            if (maxHeap.size() != minHeap.size()) {
                return minHeap.peek();
            }
            // 如果两个堆的数量相等，则说明中位数在有序整数列表的中间（总数据量是偶数），中位数是两个中间值的平均值。
            return (minHeap.peek() + maxHeap.peek()) / 2.0;
        }
    }

}
