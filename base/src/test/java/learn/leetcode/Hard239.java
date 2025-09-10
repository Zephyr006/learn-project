package learn.leetcode;

import java.util.LinkedList;

/**
 * 239. 滑动窗口最大值
 * https://leetcode-cn.com/problems/sliding-window-maximum/
 *
 * @author Zephyr
 * @date 2022/3/27.
 */
public class Hard239 {

    public static void main(String[] args) {
        int[] ints = new Solution().maxSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3);

    }
    static class Solution {
        /**
         * 队列、最小堆：队尾比不过同龄人的删掉，队头超出时代区间的删掉
         * 每次遍历到一个新值时，要做一个事情，删除队列中所有比这个值小的值，因为这个值入队之后，所以比这个值小的，并且在这个值之前的，都不可能是答案。
         * 再 判断当前队列中队首的值是否在窗口范围内，如果超出滑动窗口范围，则需要弹出队首下标
         */
        public int[] maxSlidingWindow(int[] nums, int k) {
            if (nums == null || nums.length < k - 1) {
                return nums;
            }
            int[] result = new int[nums.length - k + 1];
            // queue 中保存的是数组的下标
            LinkedList<Integer> queue = new LinkedList<>();

            for (int i = 0; i < nums.length; i++) {
                // 弹出队列中小于当前值的元素：因为按照滑动窗口的规则，如果当前值还在窗口中，那么小于当前值的左侧数据永远不会是窗口内的最大值
                // 如果queue里面的数比当前对应的数字小则需要依次弹出，保证从大到小，直至满足要求
                while (!queue.isEmpty() && nums[queue.peekLast()] <= nums[i]) {
                    queue.pollLast();
                }
                // 添加当前值对应的数组下标到queue，最新的数据在队尾
                queue.addLast(i);
                // 判断当前队列中队首的值是否在窗口范围内，如果超出滑动窗口范围，则需要弹出队首下标
                if (queue.peekFirst() <= i - k) {
                    queue.pollFirst();
                }
                // 当窗口长度为k时 保存当前窗口中最大值，这个if值为false时，表明遍历的数字还不够k个
                if (i + 1 >= k) {
                    result[i + 1 - k] = nums[queue.peekFirst()];
                }
            }
            return result;
        }
    }
}
