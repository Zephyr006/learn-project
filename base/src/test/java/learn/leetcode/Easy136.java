package learn.leetcode;

import java.util.BitSet;

/**
 * 136. 只出现一次的数字
 * https://leetcode.cn/problems/single-number/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Easy136 {

    /**
     * 位运算：使用 bit 数组存储每个数字出现的次数，
     */
    public int singleNumber(int[] nums) {
        BitSet set = new BitSet(6 * 10000);
        // 遍历 nums 数组，对nums 数组中对应的 bit 执行翻转
        for (int num : nums) {
            set.flip(num + 3 * 10000);
        }
        // 返回数组中值为 1 的位，就是结果 num
        return set.nextSetBit(0) - 3 * 10000;
    }

    /**
     * 位运算：使用 bit 数组存储每个数字出现的次数，
     * 除了某个元素只出现一次以外，其余每个元素均出现两次。所以遍历数组结束，bit 数组应该只会有一个位是 1，其他位都是 0
     */
    public int singleNumber2(int[] nums) {
        // 优化：先找到数组中的最大值和最小值，以减小bit 数组的大小
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int num : nums) {
            max = Math.max(max, num);
            min = Math.min(min, num);
        }
        BitSet set = new BitSet(max - min + 1);

        // 遍历 nums 数组，对nums 数组中对应的 bit 执行翻转
        for (int num : nums) {
            set.flip(num - min);
        }
        // 返回数组中值为 1 的位，就是结果 num
        return set.nextSetBit(0) + min;
    }
}
