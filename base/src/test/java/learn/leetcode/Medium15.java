package learn.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 15. 三数之和
 * https://leetcode.cn/problems/3sum/description/
 */
public class Medium15 {
    public static void main(String[] args) {
        List<List<Integer>> res = new Medium15().threeSum(LeetcodeHelper.toIntArray("[-1,0,1,2,-1,-4]"));

    }

    /**
     * 双指针：先为数组排序，在固定第一个数之后，用双指针找到满足题目的解
     *   如果当前和 >0，则需要找到更小的数，右指针左移；如果当前和 <0，则需要找到更大的数，左指针右移
     *   注意：在找到满足题意的解时，如果相邻元素相等，说明都满足题意，需要跳过相同的解
     */
    public List<List<Integer>> threeSum(int[] nums) {
        // 特殊输入值判断
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length<3){
            return result;
        }

        // 先为数组排序
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2;i++) {
            // 剪枝：如果 nums[i]nums[i]大于 0，则三数之和必然无法等于 0，结束循环
            if (nums[i] > 0)
                break;
            // 去重
            // if(i > 0 && nums[i] == nums[i-1])
            //     continue;

            int left = i + 1, right = nums.length - 1;
            // 固定最左侧的下标 i，对于后面的两个值，使用双指针，由于数组是有序的，两个指针同时向中间移动，很容易找到满足题意的答案
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // 如果相邻元素值相等，说明都满足题意，需要跳过相邻元素
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    // 注意：指针需要移动，不然始终都停留在相同下标位置，会出错
                    left++;
                } else if (sum > 0) {
                    right--;
                } else{
                    left++;
                }
            }
            while (i + 1 < nums.length - 2 && nums[i] == nums[i+1]) {
                i++;
            }
        }
        return result;
    }
}
