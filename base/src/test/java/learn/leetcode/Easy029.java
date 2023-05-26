package learn.leetcode;

/**
 * 747. 至少是其他数字两倍的最大数
 *
 * https://leetcode-cn.com/problems/largest-number-at-least-twice-of-others/
 *
 * @author Zephyr
 * @since 2022-3-13.
 */
public class Easy029 {

    public static void main(String[] args) {
        int i = new Easy029().dominantIndex(new int[]{1, 0});
        System.out.println(i);
    }

    /**
     * 统计最大和次大：设置两个变量，遍历数组，分别维护最大值和次大值
     */
    public int dominantIndex(int[] nums) {
        // 特殊情况：数组中只有一个元素，直接返回0
        if (nums.length == 1) {
            return 0;
        }

        // 由于 【0 <= nums[i] <= 100】，所以定义 max=second=-1 ，并且遍历从0开始
        int index = 0, max = -1, second = -1;
        // 找出数组中的最大值和他的下标index
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > max) {
                second = max;
                max = nums[i];
                index = i;
            } else if (nums[i] > second) {
                second = nums[i];
            }
        }

        return max >= second * 2 ? index : -1;
    }

}
