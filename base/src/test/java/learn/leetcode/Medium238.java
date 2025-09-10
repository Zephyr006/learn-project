package learn.leetcode;

/**
 * 238. 除自身以外数组的乘积
 * https://leetcode.cn/problems/product-of-array-except-self/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium238 {
    public static void main(String[] args) {
        int[] intArray = LeetcodeHelper.toIntArray("1, 2, 3, 4");
        int[] node = new Medium238().productExceptSelf(intArray);
        LeetcodeHelper.print(node);
    }

    /**
     * 数组
     */
    public int[] productExceptSelf(int[] nums) {
        int[] left = new int[nums.length];
        int[] right = new int[nums.length];
        left[0] = nums[0];
        right[nums.length - 1] = nums[nums.length - 1];
        for (int i = 1; i < nums.length; i++) {
            left[i] = left[i - 1] * nums[i];
        }
        for (int i = nums.length - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i];
        }

        int[] ans = new int[nums.length];
        ans[0] = right[1];
        ans[nums.length - 1] = left[nums.length - 2];
        for (int i = nums.length - 2; i > 0; i--) {
            ans[i] = right[i + 1] * left[i - 1];
        }
        return ans;
    }
}
