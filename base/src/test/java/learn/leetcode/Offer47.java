package learn.leetcode;

/**
 * 剑指 Offer 47. 礼物的最大价值
 * 动态规划
 *
 * @author Zephyr
 * @date 2022/4/23.
 */
public class Offer47 {

    public static void main(String[] args) {
        int[][] grid = LeetcodeHelper.parse2DIntArray("[[1,2],[1,1]]");
        int maxValue = new Solution().maxValue(grid);
        System.out.println(maxValue);

    }

    static class Solution {
        public int maxValue(int[][] grid) {
            int m = grid.length, n = grid[0].length;
            for(int i = 0; i < m; i++) {
                for(int j = 0; j < n; j++) {
                    if(i == 0 && j == 0) continue;
                    if(i == 0) grid[i][j] += grid[i][j - 1] ;
                    else if(j == 0) grid[i][j] += grid[i - 1][j];
                    else grid[i][j] += Math.max(grid[i][j - 1], grid[i - 1][j]);
                }
            }
            return grid[m - 1][n - 1];
        }
    }

}
