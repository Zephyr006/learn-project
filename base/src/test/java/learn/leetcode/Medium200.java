package learn.leetcode;

/**
 * 200. 岛屿数量
 * { @link https://leetcode.cn/problems/number-of-islands/}
 * 岛屿类问题解析见 { @link https://leetcode.cn/problems/number-of-islands/solution/dao-yu-lei-wen-ti-de-tong-yong-jie-fa-dfs-bian-li-/}
 *
 * @author Zephyr
 * @since 2023-05-29
 */
public class Medium200 {
    public static void main(String[] args) {
        char[][] array = LeetcodeHelper.parse2DCharArray("[[\"1\",\"1\",\"0\",\"0\",\"0\"],[\"1\",\"1\",\"0\",\"0\",\"0\"],[\"0\",\"0\",\"1\",\"0\",\"0\"],[\"0\",\"0\",\"0\",\"1\",\"1\"]]");
        System.out.println(new Solution().numIslands(array));
    }

    static class Solution {
        public int numIslands(char[][] grid) {
            int count = 0;
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    // 遍历每一个单位,面积 >0 的说明是一个岛屿,
                    int area = area(grid, i, j);
                    if (area > 0) {
                        count++;
                    }
                }
            }
            return count;
        }

        /**
         * 返回一整个岛屿的面积
         */
        int area(char[][] grid, int level, int deep) {
            // 必须先判断当前对应的位置是否在岛屿(二维数组)内,避免数组下标越界
            boolean inArea = level < grid.length && deep < grid[0].length
                && deep >= 0 && level >= 0;
            // 如果越界了,或者已经遍历过,则返回 0
            if (!inArea || grid[level][deep] != '1') {
                return 0;
            }
            // 修改对此位置的元素值,表示已经遍历过此位置
            grid[level][deep] = '2';
            return
                1 + area(grid, level - 1, deep)
                    + area(grid, level + 1, deep)
                    + area(grid, level, deep - 1)
                    + area(grid, level, deep + 1);
        }
    }

}
