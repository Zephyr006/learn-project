package learn.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2352. 相等行列对
 * https://leetcode.cn/problems/equal-row-and-column-pairs/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium2352 {
    public static void main(String[] args) {
        int[][] ints = LeetcodeHelper.to2DIntArray("[[3,2,1],[1,7,6],[2,7,7]]");
        int result = new Medium2352().equalPairs(ints);
        System.out.println(result);
    }

    /**
     * 哈希表：首先将矩阵的行放入哈希表中统计次数，哈希表的键可以是将行拼接后的字符串，也可以用各语言内置的数据结构，然后分别统计每一列相等的行有多少，求和即可。
     */
    public int equalPairs(int[][] grid) {
        Map<List<Integer>, Integer> map = new HashMap<>();
        int length = grid.length;
        for (int[] ints : grid) {
            List<Integer> list = new ArrayList<>(length);
            for (int j = 0; j < length; j++) {
                list.add(ints[j]);
            }
            Integer count = map.getOrDefault(list, 0);
            map.put(list, count + 1);
        }

        int count = 0;
        for (int rowIndex = 0; rowIndex < length; rowIndex++) {
            List<Integer> list = new ArrayList<>(length);
            for (int lineIdx = 0; lineIdx < length; lineIdx++) {
                // row[1] = grid[1][idx]
                list.add(grid[lineIdx][rowIndex]);
            }
            count += map.getOrDefault(list, 0);
        }
        return count;
    }
}
