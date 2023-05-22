package learn.leetcode;

import java.util.Arrays;

/**
 * 581. 最短无序连续子数组
 *
 * 给你一个整数数组 nums ，你需要找出一个 连续子数组 ，如果对这个子数组进行升序排序，那么整个数组都会变为升序排序。
 *
 * 请你找出符合题意的 最短 子数组，并输出它的长度。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：nums = [2,6,4,8,10,9,15]
 * 输出：5
 * 解释：你只需要对 [6, 4, 8, 10, 9] 进行升序排序，那么整个表都会变为升序排序。
 * 示例 2：
 *
 * 输入：nums = [1,2,3,4]
 * 输出：0
 * 示例 3：
 *
 * 输入：nums = [1]
 * 输出：0
 *  
 *
 * 提示：
 *
 * 1 <= nums.length <= 104
 * -105 <= nums[i] <= 105
 *  
 *
 * 进阶：你可以设计一个时间复杂度为 O(n) 的解决方案吗？
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/shortest-unsorted-continuous-subarray
 *
 * @author Zephyr
 * @date 2021/8/3.
 */
public class Medium07 {

    public static void main(String[] args) {
        int[] nums = new int[]{1,3,2,2,2};

        int unsortedSubarrayCount = findUnsortedSubarray(nums);
        System.out.println(unsortedSubarrayCount);
    }

    /**
     * 解题思路：
     * 1.找到左右两侧的有序子数组
     * 2.找到中间无序子数组中的最大值和最小值
     * 3.根据"无序子数组中的最大值和最小值"回退两侧有序子数组的指针（无序子数组中的值可能小于左侧有序子数组或大于右侧有序子数组）
     */
    public static int findUnsortedSubarray(int[] nums) {
        // 边界控制：数组中元素个数小于2个，按有序处理
        if (nums.length < 2) {
            return 0;
        }
        // 边界控制：如果第一个值比最后一个值还大，则整个数组都是未排序数组
        if (nums[0] > nums[nums.length -1]) {
            return nums.length;
        }

        int left = 0, right = nums.length - 1;
        while (left + 1 < nums.length) {
            // 移动左边的指针：当前位置元素值小于右侧相邻元素值 并且 小于右侧指针对应的元素值
            if (nums[left] <= nums[left + 1] && nums[left + 1] <= nums[right]) {
                left++;
            } else {
                break;
            }
        }
        while (right - 1 > 0) {
            // 移动右边的指针：当前位置元素值大于左侧相邻元素值 并且 大于左侧指针对应的元素值
            if (nums[right - 1] <= nums[right] && nums[right - 1] >= nums[left]) {
                right--;
            } else {
                break;
            }
        }
        // 如果指针有交叉 说明整体有序
        if (left >= right) {
            return 0;
        }

        // 找到无序子数组中的最大值和最小值
        Integer min = null, max = null;
        for (int subI = left; subI <= right; subI++) {
            min = Math.min(nums[subI], (min == null ? nums[subI] : min) );
            max = Math.max(nums[subI], (max == null ? nums[subI] : max) );
        }

        // 如果左侧指针对应的元素值大于无序数组中的最小值，则左移左侧指针，右侧同理
        while (left >= 0 && nums[left] > min) {
            left--;
        }
        while (right < nums.length && nums[right] < max) {
            right++;
        }

        return right - left - 1;
    }


    /**
     * 官方结题思路：先排序，再从左侧和右侧开始分别找到第一个不同的元素，这中间的元素即为最短的无序数组
     */
    public static int officialFindUnsortedSubarray(int[] nums) {
        if (isSorted(nums)) {
            return 0;
        }
        int[] numsSorted = new int[nums.length];
        System.arraycopy(nums, 0, numsSorted, 0, nums.length);
        Arrays.sort(numsSorted);
        int left = 0;
        while (nums[left] == numsSorted[left]) {
            left++;
        }
        int right = nums.length - 1;
        while (nums[right] == numsSorted[right]) {
            right--;
        }
        return right - left + 1;
    }

    public static boolean isSorted(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < nums[i - 1]) {
                return false;
            }
        }
        return true;
    }

}
