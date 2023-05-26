package learn.leetcode;

/**
 * 53. 最大子序和   (可以不是连续子序，是子序即可)
 *
 * 给定一个整数数组 nums ，找到一个具有最大和的**连续**子数组（子数组最少包含一个元素），返回其最大和。
 *
 * 示例:
 *
 * 输入: [-2,1,-3,4,-1,2,1,-5,4],
 * 输出: 6
 * 解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
 * 进阶:
 *
 * 如果你已经实现复杂度为 O(n) 的解法，尝试使用更为精妙的分治法求解。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/maximum-subarray
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-7/8.
 */
public class Easy014 {

    /**
     * 解法1：动态规划
     *
     * @param nums
     * @return
     */
    public int maxSubArray(int[] nums) {
        if (nums == null || nums.length < 1)
            return 0;

        int max = nums[0];    // 全局最大值
        int subMax = nums[0];  // 前一个子组合的最大值,状态压缩
        for (int i = 1; i < nums.length; i++) {
            if (subMax <= 0) {
                // 前一个子组合最大值小于0，抛弃前面的结果
                subMax = nums[i];
            } else {
                // 前一个子组合最大值大于0，正增益
                subMax = subMax + nums[i];
            }
            // 计算全局最大值
            max = Math.max(max, subMax);
        }
        return max;
    }

    /**
     * 解法2：分治
     * @param nums
     * @return
     */
    public int maxSubArrayOfficial(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        if (nums.length == 1){
            return nums[0];
        }

        int[] subNums = new int[nums.length];
        subNums[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (subNums[i-1] <= 0) {
                subNums[i] = nums[i];
            } else {
                subNums[i] = subNums[i-1] + nums[i];
            }
        }
        int max = subNums[0];
        for (int i = 1; i < subNums.length; i++) {
            max = Math.max(max, subNums[i]);
        }
        return max;
    }

    public static void main(String[] args) {
        new Easy014().maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4});
    }
}
