package learn.leetcode;

/**
 * { @link https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array/}
 * @author Zephyr
 * @date 2023/5/28.
 */
public class Medium18 {

    public static void main(String[] args) {
        System.out.println(new Solution().findMin(new int[]{4,5,6,7,0,1,2}));
    }

    static class Solution {
        // 2,3,4,5,1
        public int findMin(int[] nums) {
            int left = 0, right = nums.length - 1;
            // 旋转前与旋转后的数字顺序一致，都是升序排列
            // if(nums[left] < nums[right]) {
            //     return nums[left];
            // }

            while (left < right) {
                int mid = left + (right - left) / 2;
                if (nums[mid] < nums[right]) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
                // if (nums[left] <= nums[mid]) {
                //     left = mid + 1;
                // } else {
                //     right = mid;
                // }
            }
            return nums[left];
        }
    }
}
