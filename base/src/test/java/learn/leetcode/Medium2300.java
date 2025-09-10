package learn.leetcode;

import java.util.Arrays;

/**
 * 2300. 咒语和药水的成功对数
 * https://leetcode.cn/problems/successful-pairs-of-spells-and-potions/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium2300 {
    public static void main(String[] args) {
        int[] spells = LeetcodeHelper.toIntArray("[5,1,3]");
        int[] potions = LeetcodeHelper.toIntArray("[1,2,3,4,5]");
        LeetcodeHelper.print(new Medium2300().successfulPairs(spells, potions, 7));

    }
    public int[] successfulPairs(int[] spells, int[] potions, long success) {
        Arrays.sort(potions);

        int[] result = new int[spells.length];
        for (int i = 0; i < spells.length; i++) {
            long target = success / spells[i];
            if (target * spells[i] < success) {
                target++;
            }
            int minIdx = find(potions, (int) target);
            if (minIdx >= potions.length) {
                result[i] = 0;
            } else if (minIdx < 0) {
                result[i] = potions.length;
            } else {
                result[i] = potions.length - minIdx;
            }
        }
        return result;
    }

    // 二分法找到有序数组中大于等于指定值 target 的最小下标 idx
    private int find(int[] potions, int target) {
        int left = 0, right = potions.length - 1;
        while (left < right) {
            int mid =  left + (right - left) / 2;
            // if (potions[mid] == target) {
            //     while (mid > 0 && potions[mid - 1] == target) {
            //        mid--;
            //     }
            //     return mid;
            // } else
            if (potions[mid] >= target) {
                //result = mid;
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return potions[left] >= target ? left : left + 1;
    }
}
