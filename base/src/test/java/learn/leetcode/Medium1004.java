package learn.leetcode;

/**
 * 1004. 最大连续1的个数 III
 *
 * 给定一个二进制数组 nums 和一个整数 k，假设最多可以翻转 k 个 0 ，则返回执行操作后 数组中连续 1 的最大个数 。
 *
 * https://leetcode.cn/problems/max-consecutive-ones-iii/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium1004 {
    public static void main(String[] args) {
        int longestOnes = new Medium1004().longestOnes(LeetcodeHelper.toIntArray("[1,1,1,0,0,0,1,1,1,1,0]"), 2);
        System.out.println(longestOnes);
        longestOnes = new Medium1004().longestOnes(LeetcodeHelper.toIntArray("[0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1]"), 3);
        System.out.println(longestOnes);
    }

    /**
     * 滑动窗口：窗口右侧一直向右，直到窗口中 0 的个数超过 k，记录最大值 max。 再移动窗口左侧指针 left，直到窗口中的 0 个数不超过 k
     */
    public int longestOnes(int[] nums, int k) {
        int max = 0;
        int left = 0, right = 0;
        int count0 = 0;
        while (right < nums.length) {

            // 滑动窗口中的 0 个数超过了 k，记录最大长度max
            if (nums[right] == 0 && ++count0 > k) {
                max = Math.max(max, right - left);

                //     窗口左指针右移，直到窗口中 0 的个数不超过 k
                while (nums[left] == 1) {
                    left++;
                }
                left++;
                count0--;
            }
            right++;
        }
        if (count0 <= k) {
            max = Math.max(max, right - left);
        }
        return max;
    }
}
