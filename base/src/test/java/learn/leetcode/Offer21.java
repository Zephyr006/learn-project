package learn.leetcode;

/**
 * @author Zephyr
 * @date 2022/4/27.
 */
public class Offer21 {

    public static void main(String[] args) {
        int[] nums = LeetCodeHelper.toIntArray("[1,2,3,5,4]");
        int[] exchange = new Solution().exchange(nums);
        System.out.println(exchange);
    }

    static class Solution {
        public int[] exchange(int[] nums) {
            // 双指针：快慢指针,快指针找奇数，慢指针找偶数
            int slow = -1, fast = -1;
            while (slow < nums.length && fast < nums.length) {
                while (++slow < nums.length) {
                    if ((nums[slow] & 1) == 0) {
                        break;
                    }
                }
                fast = slow;
                while (++fast < nums.length) {
                    if ((nums[fast] & 1) == 1) {
                        break;
                    }
                }

                if (fast < nums.length) {
                    int temp = nums[slow];
                    nums[slow] = nums[fast];
                    nums[fast] = temp;
                } else {
                    break;
                }
            }

            return nums;
        }

    }
}
