package learn.leetcode;

/**
 * 240. 搜索二维矩阵 II
 *
 * @author Zephyr
 * @date 2022/4/14.
 */
public class Medium240 {

    public static void main(String[] args) {
        int[][] matrix = new int[][]{{1, 1}};
        boolean have = new Solution().searchMatrix(matrix, 2);
        System.out.println(have);
    }

    static class Solution {
        public boolean searchMatrix(int[][] matrix, int target) {
            int left = 0, right = matrix[0].length - 1;
            int up = 0, down = matrix.length - 1;

            // 二分法找找横向的折中值
            while (left < right) {
                int mid = (left + right) >> 1;
                if (matrix[0][mid] == target) {
                    return true;
                } else if (matrix[0][mid] > target) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            // 二分法找竖向的折中值
            while (up < down) {
                int mid = (up + down) >> 1;
                if (matrix[mid][0] == target) {
                    return true;
                } else if (matrix[mid][0] > target) {
                    down = mid - 1;
                } else {
                    up = mid + 1;
                }
            }

            left = 0;
            up = 0;
            while (left < right) {
                int mid = (left + right) >> 1;
                if (matrix[down][mid] == target) {
                    return true;
                } else if (matrix[down][mid] < target) {
                    left = mid + 1;
                } else {
                    break;
                }
            }

            while (up < down) {
                int mid = (up + down) >> 1;
                if (matrix[mid][right] == target) {
                    return true;
                } else if (matrix[down][mid] < target){
                    up = mid + 1;
                } else {
                    break;
                }
            }

            // 在被缩小的矩阵中找目标值
            for (int i = right; i >= 0; i--) {
                for (int j = down; j >= 0 ; j--) {
                    if (matrix[j][i] == target) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
