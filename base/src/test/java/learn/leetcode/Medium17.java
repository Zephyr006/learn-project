package learn.leetcode;


/**
 * 209. 长度最小的子数组
 * { @link https://leetcode.cn/problems/minimum-size-subarray-sum/}
 *
 * @author Zephyr
 * @since 2023-5-25.
 */
public class Medium17 {

    public static void main(String[] args) {
        System.out.println(new Solution().minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3}));
    }
    static class Solution {
        /**
         * 滑动窗口解法:每次右移right指针,如果满足条件,则记录满足条件的结果值,并且将left和right指针右移
         */
        public int minSubArrayLen(int target, int[] nums) {
            int minLen = Integer.MAX_VALUE,sum = 0;

            int left = 0;
            int right = 0;
            /* // 自己写的解法
            for (int left = 0; left < nums.length; left++) {
                while (right < nums.length) {
                    sum += nums[right];
                    // 如果找到了满足条件的子数组,则记录结果值,并且使left指针右移1位,right指针不动
                    if (sum >= target) {
                        minLen = Math.min(minLen, right - left + 1);
                        // if ( minLen == 1) {
                        // return minLen;
                        // }
                        sum -= nums[left];
                        // 由于这层for循环每次都要先执行 sum += nums[right];  所以要先相应的减去nums[right]的值
                        sum -= nums[right];
                        break;
                    } else {
                        right++;
                    }
                }
            }*/
            while (right < nums.length) {
                sum += nums[right];
                // 官方写法:满足 sum >= target 的要求,则记录最小长度,并且使left指针右移1位,right指针不动
                while (sum >= target) {
                    minLen = Math.min(minLen, right - left + 1);
                    sum -= nums[left];
                    left++;
                }
                right++;
            }
            // 如果没找到满足条件的结果值,返回 0
            return minLen >= Integer.MAX_VALUE ? 0 : minLen;
        }
    }
}
