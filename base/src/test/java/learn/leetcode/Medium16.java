package learn.leetcode;


/**
 * 167. 两数之和 II - 输入有序数组
 * { @link https://leetcode.cn/problems/two-sum-ii-input-array-is-sorted/}
 *
 * @author Zephyr
 * @since 2023-5-24.
 */
public class Medium16 {

    public static void main(String[] args) {
        new Solution().twoSum(new int[]{2, 7, 11, 15}, 9);
    }

    static class Solution {
        public int[] twoSum(int[] numbers, int target) {
            int left = 0, right = numbers.length - 1;
            while (left >= 0 && right < numbers.length) {
                int tmp = numbers[left] + numbers[right];
                if (tmp == target) {
                    return new int[]{left+1, right+1};
                } else if (tmp < target) {
                    left++;
                }else {
                    right--;
                }
            }
            return null;
        }
    }
}
