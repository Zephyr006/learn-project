package learn.leetcode;

/**
 * 假设有一个很长的花坛，一部分地块种植了花，另一部分却没有。可是，花不能种植在相邻的地块上，它们会争夺水源，两者都会死去。
 *
 * 给你一个整数数组 flowerbed 表示花坛，由若干 0 和 1 组成，其中 0 表示没种植花，1 表示种植了花。另有一个数 n ，能否在不打破种植规则的情况下种入 n 朵花？能则返回 true ，不能则返回 false 。
 *
 *
 *
 * 示例 1：
 *
 * 输入：flowerbed = [1,0,0,0,1], n = 1
 * 输出：true
 * 示例 2：
 *
 * 输入：flowerbed = [1,0,0,0,1], n = 2
 * 输出：false
 *
 * https://leetcode.cn/problems/can-place-flowers/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Easy605 {

    public static void main(String[] args) {
        int[] intArray = LeetcodeHelper.toIntArray("[1,0,0,0,1]");
        // assert new Easy605().canPlaceFlowers(intArray, 1) == true;
        // assert new Easy605().canPlaceFlowers(intArray, 2) == false;
        //
        // intArray = LeetcodeHelper.toIntArray("[0,1,0,0,1]");
        // assert new Easy605().canPlaceFlowers(intArray, 1) == false;
        // assert new Easy605().canPlaceFlowers(intArray, 2) == false;

        // intArray = LeetcodeHelper.toIntArray("[1,0,0,0,1,0,0]");
        // assert new Easy605().canPlaceFlowers(intArray, 1) == true;
        // assert new Easy605().canPlaceFlowers(intArray, 2) == true;

        intArray = LeetcodeHelper.toIntArray("[0,0]");
        assert new Easy605().canPlaceFlowers(intArray, 1) == true;
        assert new Easy605().canPlaceFlowers(intArray, 2) == false;
    }

    /**
     * 结题思路：如果可以的话，直接种花，记录能种花的最大数量
     */
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        // 处理只有一个参数的情况
        if (flowerbed.length == 1) {
            return flowerbed[0] == 0 ? n <= 1 : n == 0;
        }
        int i = 0, count = 0;
        while (i + 1 < flowerbed.length) {
            // 如果是连续的两个 0，可以种花，在左侧种花
            if (flowerbed[i] == 0 && flowerbed[i + 1] == 0) {
                flowerbed[i] = 1;
                count++;
                i += 2;
            //     如果左侧有花，直接跳过两格
            } else if (flowerbed[i] == 1 && flowerbed[i + 1] == 0) {
                i += 2;
            //     如果右侧有花，需要跳过 3 格，因为右侧的 1 旁边也不能种花
            } else if (flowerbed[i] == 0 && flowerbed[i + 1] == 1) {
                i += 3;
            } else {
                return false;
            }
        }

        // 由于是 2 个一组处理的，需要处理下最终以一个 0 结尾的情况
        if (flowerbed.length>=2 && flowerbed[flowerbed.length-2] == 0 && flowerbed[flowerbed.length-1] == 0) {
            count++;
        }
        return count >= n;
    }
}
