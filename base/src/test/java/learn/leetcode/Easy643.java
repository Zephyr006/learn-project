package learn.leetcode;

/**
 * 643. 子数组最大平均数 I
 * https://leetcode-cn.com/problems/maximum-average-subarray-i/
 *
 * @author Zephyr
 * @date 2022/3/30.
 */
public class Easy643 {

    public static void main(String[] args) {
        System.out.println(Math.pow(2,3));
        System.out.println(new Solution().findMaxAverage(new int[]{0,4,0,3,2}, 1));
    }

    static class Solution {
        public double findMaxAverage(int[] nums, int k) {
            int sum = 0;
            //计算出第一个滑动窗口的sum值
            for (int i = 0; i < k; i++) {
                sum += nums[i];
            }
            // 计算其他滑动窗口的sum值，取最大值，注意边界控制
            int maxSum = sum;
            for (int i = k; i < nums.length; i++) {
                sum = sum + nums[i] - nums[i - k];
                maxSum = Math.max(sum, maxSum);
            }
            // 最后一步求平均值，不需要每一步都计算出平均值
            return maxSum * 1.0 / k;

        }
    }
}
