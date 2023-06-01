package learn.leetcode;

/**
 * 剑指 Offer 42. 连续子数组的最大和
 * @author Zephyr
 * @date 2022/4/23.
 */
public class Offer42 {
    public static void main(String[] args) {
        int maxSubArray = new Solution().maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4});
        System.out.println(maxSubArray);
    }

    static class Solution {
        public int maxSubArray(int[] nums) {
            int[] dp = new int[nums.length];

            for (int i = 0 ; i < nums.length; i++) {
                if (i - 1 >= 0 && dp[i - 1] > 0) {
                    dp[i] = dp[i - 1] + nums[i];
                } else {
                    dp[i] = nums[i];
                }
            }

            int res = dp[0];
            for (int i = 1; i < dp.length; i++) {
                res = Math.max(res, dp[i]);
            }
            return res;
        }
    }
}
