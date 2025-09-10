package learn.leetcode;

/**
 * 给你一个整数数组 nums ，判断这个数组中是否存在长度为 3 的递增子序列。
 *
 * 如果存在这样的三元组下标 (i, j, k) 且满足 i < j < k ，使得 nums[i] < nums[j] < nums[k] ，返回 true ；否则，返回 false 。
 *
 *
 *
 * 示例 1：
 *
 * 输入：nums = [1,2,3,4,5]
 * 输出：true
 * 解释：任何 i < j < k 的三元组都满足题意
 * 示例 2：
 *
 * 输入：nums = [5,4,3,2,1]
 * 输出：false
 * 解释：不存在满足题意的三元组
 * 示例 3：
 *
 * 输入：nums = [2,1,5,0,4,6]
 * 输出：true
 * 解释：三元组 (3, 4, 5) 满足题意，因为 nums[3] == 0 < nums[4] == 4 < nums[5] == 6
 *
 * https://leetcode.cn/problems/increasing-triplet-subsequence/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium334 {

    public static void main(String[] args) {
        if (!new Medium334().increasingTriplet(LeetcodeHelper.toIntArray("[20,100,10,12,5,13]"))) {
            System.err.println("fail");
        }
        if (!new Medium334().increasingTriplet(LeetcodeHelper.toIntArray("[2,1,5,0,4,6]"))) {
            System.err.println("fail");
        }
        if (!new Medium334().increasingTriplet(LeetcodeHelper.toIntArray("[1,5,0,4,1,3]"))) {
            System.err.println("fail");
        }
        System.out.println("success");
    }

    /**
     * 双向遍历：根据题目要求，需要有一个递增的三元数组，那么一定存在一个元素 i，这个元素左边存在一个小于 nums[i] 的值，右边存在一个大于 nums[i] 的值
     *        所以需要维护两个数组，一个是从左向右，左侧的最小元素值，一个是从右向左，右侧的最大元素值，然后在原始数组中找到满足条件的下标位置即可
     */
    public boolean increasingTriplet(int[] nums) {
        if (nums.length < 3) {
            return false;
        }
        int[] rightMax = new int[nums.length];
        rightMax[nums.length - 1] = nums[nums.length - 1];
        // 用一个数组记录当前位置及右侧的最大值，这样就可以直接遍历这个数组判断右侧有没有更大的值,不需要指针向后遍历了
        for (int i = nums.length - 2; i >= 0; i--) {
            rightMax[i] = Math.max(nums[i], rightMax[i + 1]);
        }

        int[] leftMin = new int[nums.length];
        leftMin[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            leftMin[i] = Math.min(leftMin[i - 1], nums[i]);
        }

        for (int i = 1; i < nums.length - 1; i++) {
            if (nums[i] < rightMax[i + 1] && nums[i] > leftMin[i -1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 暴力遍历，时间复杂度超出限制
     */
    public boolean increasingTriplet2(int[] nums) {
        if (nums.length < 3) {
            return false;
        }
        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                // 先判断左边两个元素大小，再判断第二个元素和第三个元素的大小
                // 注意：三个元素之间不一定相邻，没有这个限制
                if (nums[i] >= nums[j]) {
                    continue;
                }
                for (int k = j + 1; k < nums.length; k++) {
                    if (nums[j] >= nums[k]) {
                        continue;
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
