package learn.leetcode;

/**
 * 485. 最大连续 1 的个数
 * { @link https://leetcode.cn/problems/max-consecutive-ones/}
 *
 * @author Zephyr
 * @since 2023-5-25.
 */
public class Easy031 {

    class Solution {
        public int findMaxConsecutiveOnes(int[] nums) {
            int maxLen = nums[0] == 1 ? 1 : 0;
            if (nums.length == 1)
                return maxLen;
            int left = nums[0] == 1 ? 0 : -1;
            int right = 1;
            for (; right < nums.length; right++) {
                if (nums[right] == 1) {
                    // 01
                    if (nums[right - 1] != 1) {
                        left = right;
                    // 11
                    } else {
                    }

                } else {
                    // 10
                    if (nums[right - 1] == 1) {
                        maxLen = Math.max(maxLen, right - left);
                        // 00
                    } else {
                    }
                }
            }
            // 数组的最后一个元素为1的时候，maxLen可能没有触发更新，因为只有最后两个数组为10的时候才会更新maxLen
            if (left >= 0 && nums[right - 1] == 1) {
                maxLen = Math.max(maxLen, right - left);
            }
            return  maxLen;
        }
    }
}
