package learn.example.javase.leetcode;

import java.util.Arrays;

/**
 * 56. 合并区间
 * https://leetcode-cn.com/problems/merge-intervals/
 *
 * @author Zephyr
 * @date 2022/3/20.
 */
public class Medium14 {

    public static void main(String[] args) {
        int[][] merge = new Solution().merge(new int[][]{{1, 4}, {4, 8}});
        System.out.println(merge);
    }

    static class Solution {
        /**
         * 抄袭自：https://leetcode-cn.com/problems/merge-intervals/solution/chi-jing-ran-yi-yan-miao-dong-by-sweetiee/
         */
        public int[][] merge(int[][] intervals) {
            int[][] result = new int[intervals.length][2];
            // 注意初始变量值，必须是-1
            int idx = -1;
            // 以子数组中的第一个数组元素排序
            Arrays.sort(intervals, (a1, a2) -> a1[0] - a2[0]);

            for (int[] arr : intervals) {
                // 如果结果集中还没有任何结果，或者两个子数组不交叉，则直接将当前子数组放入结果集
                if (idx == -1 || result[idx][1] < arr[0]) {
                    result[++idx] = arr;
                // 如果子数组结果区间相交，则子数组的后一个值取较大值
                } else {
                    result[idx][1] = Math.max(arr[1], result[idx][1]);
                }
            }
            // 只取有结果的部分
            return Arrays.copyOfRange(result, 0, idx + 1);
        }
    }
}
