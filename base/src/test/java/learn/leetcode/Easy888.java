package learn.leetcode;

import java.util.Arrays;

/**
 * 888. 公平的糖果交换
 * { @link https://leetcode.cn/problems/fair-candy-swap/description/}
 *
 * @author Zephyr
 * @date 2023-08-15
 */
public class Easy888 {

    class Solution {
        public int[] fairCandySwap(int[] aliceSizes, int[] bobSizes) {
            Arrays.sort(aliceSizes);
            Arrays.sort(bobSizes);
            int aliceTotal = 0 , bobTotal = 0;
            for(int s : aliceSizes) {
                aliceTotal += s;
            }
            for(int s : bobSizes) {
                bobTotal += s;
            }

            int totalDiff = (aliceTotal - bobTotal) / 2;

            int i = 0 , j = 0;
            while (i < aliceSizes.length && j < bobSizes.length) {
                if (aliceSizes[i] - bobSizes[j] == totalDiff) {
                    return new int[]{aliceSizes[i], bobSizes[j]};
                }
                if (i + 1 < aliceSizes.length && aliceSizes[i + 1] - bobSizes[j] <= totalDiff) {
                    i++;
                } else {
                    j++;
                }
            }

            return new int[]{aliceSizes[i], bobSizes[j]};
        }

        int abs(int i) {
            return i < 0 ? -i : i;
        }
    }
}
