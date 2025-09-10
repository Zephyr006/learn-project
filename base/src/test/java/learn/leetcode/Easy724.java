package learn.leetcode;

/**
 * 724. 寻找数组的中心下标
 *
 * 给你一个整数数组 nums ，请计算数组的 中心下标 。
 *
 * 数组 中心下标 是数组的一个下标，其左侧所有元素相加的和等于右侧所有元素相加的和。
 *
 * 如果中心下标位于数组最左端，那么左侧数之和视为 0 ，因为在下标的左侧不存在元素。这一点对于中心下标位于数组最右端同样适用。
 *
 * 如果数组有多个中心下标，应该返回 最靠近左边 的那一个。如果数组不存在中心下标，返回 -1 。
 *
 * https://leetcode.cn/problems/find-pivot-index/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Easy724 {
    public static void main(String[] args) {
        int[] intArray = LeetcodeHelper.toIntArray("[1,7,3,6,5,6]");
        int index = new Easy724().pivotIndex(intArray);
        System.out.println(index);
    }

    /**
     * 前缀和：先计算整个数组的和，然后移动指针，每次计算出左边和右边元素的和，判断是否相等
     */
    public int pivotIndex(int[] nums) {
        int leftSum = 0, rightSum = 0;
        for (int num : nums) {
            rightSum += num;
        }

        for (int i = 0; i < nums.length; i++) {
            rightSum -= nums[i];
            if (leftSum == rightSum) {
                return i;
            }
            // 注意：判断是否相等时，不包含当前下标的值
            leftSum += nums[i];
        }
        return -1;
    }
}
