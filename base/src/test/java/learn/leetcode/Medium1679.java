package learn.leetcode;

import java.util.Arrays;

/**
 * 1679. K 和数对的最大数目
 *
 * 给你一个整数数组 nums 和一个整数 k 。
 *
 * 每一步操作中，你需要从数组中选出和为 k 的两个整数，并将它们移出数组。
 *
 * 返回你可以对数组执行的最大操作数。
 *
 *
 *
 * 示例 1：
 *
 * 输入：nums = [1,2,3,4], k = 5
 * 输出：2
 * 解释：开始时 nums = [1,2,3,4]：
 * - 移出 1 和 4 ，之后 nums = [2,3]
 * - 移出 2 和 3 ，之后 nums = []
 * 不再有和为 5 的数对，因此最多执行 2 次操作。
 * 示例 2：
 *
 * 输入：nums = [3,1,3,4,3], k = 6
 * 输出：1
 * 解释：开始时 nums = [3,1,3,4,3]：
 * - 移出前两个 3 ，之后nums = [1,4,3]
 * 不再有和为 6 的数对，因此最多执行 1 次操作。
 *
 * https://leetcode.cn/problems/max-number-of-k-sum-pairs/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium1679 {

    public static void main(String[] args) {

    }

    /**
     * 先对数组排序，再双指针：
     * 如果两个整数和大于 k，说明需要减小 和 的值，则右指针左移
     * 如果两个整数和小于 k，说明需要增大 和 的值，则左指针右移
     * 恰巧相等的话，满足题目要求，记录
     */
    public int maxOperations(int[] nums, int k) {
        Arrays.sort(nums);
        int count = 0, left = 0, right = nums.length - 1;
        while (left < right) {
            if (nums[left] + nums[right] > k) {
                right--;
            } else if (nums[left] + nums[right] < k) {
                left++;
            } else {
                count++;
                left++;
                right--;
            }
        }
        return count;
    }

}
