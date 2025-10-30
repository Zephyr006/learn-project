package learn.leetcode;

/**
 * 53. 最大子数组和
 * https://leetcode.cn/problems/maximum-subarray/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium53 {
    public static void main(String[] args) {
        int max = new Medium53().maxSubArray(LeetcodeHelper.toIntArray("[-2,1,-3,4,-1,2,1,-5,4]"));
        System.out.println(max);
    }

    /**
     * 子数组和：计算前缀和，
     * 1 如果前缀和大于 0，则和当前元素累加会让结果更大，做累加
     * 2 如果前缀和小于 0，则和当前元素累加只会让结果值更小，抛弃前面的前缀和，从当前元素重新累加
     */
    public int maxSubArray(int[] nums) {
        int left = 0, right = 0;
        int sum = 0;
        int max = Integer.MIN_VALUE;

        while (right < nums.length) {
            if (sum <= 0) {
                sum = nums[right];
            } else {
                sum += nums[right];
            }
            right++;
            max = Math.max(max, sum);
        }
        return max;
    }
}
