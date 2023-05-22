package learn.leetcode;

import java.util.Arrays;

/**
 * 645. 错误的集合
 *
 * 集合 s 包含从 1 到 n 的整数。不幸的是，因为数据错误，导致集合里面某一个数字复制了成了集合里面的另外一个数字的值，导致集合 丢失了一个数字 并且 有一个数字重复 。
 *
 * 给定一个数组 nums 代表了集合 S 发生错误后的结果。
 *
 * 请你找出重复出现的整数，再找到丢失的整数，将它们以数组的形式返回。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：nums = [1,2,2,4]
 * 输出：[2,3]
 * 示例 2：
 *
 * 输入：nums = [1,1]
 * 输出：[1,2]
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/set-mismatch
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2022/3/13.
 */
public class Easy028 {

    public static void main(String[] args) {

    }

    /**
     * 将数组排序之后，比较每对相邻的元素，即可找到错误的集合。
     *
     * 寻找重复的数字较为简单，如果相邻的两个元素相等，则该元素为重复的数字。
     *
     * 寻找丢失的数字相对复杂，可能有以下两种情况：
     *
     * 如果丢失的数字大于 1 且小于 n，则一定存在相邻的两个元素的差等于 22，这两个元素之间的值即为丢失的数字；
     *
     * 如果丢失的数字是 1 或 n，则需要另外判断。
     *
     * 为了寻找丢失的数字，需要在遍历已排序数组的同时记录上一个元素，然后计算当前元素与上一个元素的差。考虑到丢失的数字可能是 1，因此需要将上一个元素初始化为 0。
     *
     * 当丢失的数字小于 n 时，通过计算当前元素与上一个元素的差，即可得到丢失的数字；
     *
     * 如果 nums[n-1] != n , 则丢失的数字是 n
     *
     * 作者：LeetCode-Solution
     * 链接：https://leetcode-cn.com/problems/set-mismatch/solution/cuo-wu-de-ji-he-by-leetcode-solution-1ea4/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public int[] findErrorNums(int[] nums) {
        int[] errorNums = new int[2];
        int n = nums.length;
        Arrays.sort(nums);
        //考虑到丢失的数字可能是 1，因此需要将上一个元素初始化为 0
        int prev = 0;
        for (int i = 0; i < n; i++) {
            int curr = nums[i];
            // 当前值等于上一个值，当前值就是出错的值
            if (curr == prev) {
                errorNums[0] = prev;
            // 当前值减去上一个值大于1，当前值就是出错值得下一个值
            } else if (curr - prev > 1) {
                errorNums[1] = prev + 1;
            }
            prev = curr;
        }
        if (nums[n - 1] != n) {
            errorNums[1] = n;
        }
        return errorNums;
    }

}
