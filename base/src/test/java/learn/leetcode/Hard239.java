package learn.leetcode;

import java.util.Deque;
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
        public int[] maxSlidingWindow(int[] nums, int k) {
            if (nums == null || nums.length < k - 1) {
                return nums;
            }
            int[] result = new int[nums.length - k + 1];
            Deque<Integer> deque = new LinkedList<>();

            for (int i = 0; i < nums.length; i++) {
                // 如果queue里面的数比当前对应的数字小则需要依次弹出，保证从大到小，直至满足要求
                while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                    deque.pollLast();
                }
                // 添加当前值对应的数组下标到queue
                deque.addLast(i);
                // 判断当前队列中队首的值是否在窗口可视范围内，如果超出滑动窗口范围，则需要弹出队首下标
                if (deque.peek() <= i - k) {
                    deque.poll();
                }
                // 当窗口长度为k时 保存当前窗口中最大值，这个if值为false时，表明遍历的数字还不够k个
                if (i + 1 >= k) {
                    result[i + 1 - k] = nums[deque.peek()];
                }
            }
            return result;
        }
    }
}
