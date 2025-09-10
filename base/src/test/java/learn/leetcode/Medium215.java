package learn.leetcode;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 215. 数组中的第K个最大元素
 * https://leetcode.cn/problems/kth-largest-element-in-an-array/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium215 {
    public static void main(String[] args) {
        int[] intArray = LeetcodeHelper.toIntArray("[3,2,1,5,6,4]");
        int kthLargest = new Medium215().findKthLargest(intArray, 2);
        System.out.println(kthLargest);
    }

    /**
     * 最小堆：维护一个前 k 大的最小堆，堆中的最后一个元素就是满足题意的答案
     */
    // 链接：https://leetcode.cn/problems/kth-largest-element-in-an-array/solutions/19607/partitionfen-er-zhi-zhi-you-xian-dui-lie-java-dai/
    public int findKthLargest(int[] nums, int k) {
        // 使用一个含有 k 个元素的最小堆，PriorityQueue 底层是动态数组，为了防止数组扩容产生消耗，可以先指定数组的长度
        PriorityQueue<Integer> queue = new PriorityQueue<>(k, Comparator.naturalOrder());
        for (int i = 0; i < k; i++) {
            queue.add(nums[i]);
        }

        for (int i = k; i < nums.length; i++) {
            //  只要当前遍历的元素比堆顶元素大，堆顶弹出，遍历的元素进去
            if (nums[i] > queue.peek()) {
                queue.poll();
                queue.add(nums[i]);
            }
        }
        return queue.peek();
    }
}
