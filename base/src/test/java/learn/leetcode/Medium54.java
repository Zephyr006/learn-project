package learn.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 54. 螺旋矩阵
 * https://leetcode.cn/problems/spiral-matrix/description/?envType=study-plan-v2&envId=top-interview-150
 */
public class Medium54 {
    public static void main(String[] args) {
        int[][] matrix = LeetcodeHelper.to2DIntArray("[[1,2,3],[4,5,6],[7,8,9]]");
        List<Integer> res = new Medium54().spiralOrder(matrix);
        System.out.println(res);
    }

    /**
     * 矩阵遍历
     */
    public List<Integer> spiralOrder(int[][] matrix) {
        // 先求出矩阵中的元素总数
        int total = matrix[0].length * matrix.length;
        int topLimit = 0, bottomLimit = matrix.length - 1;
        int leftLimit = 0, rightLimit = matrix[0].length - 1;

        List<Integer> result = new ArrayList<>();
        // 设定上下左右边界，每一轮遍历都按照 向右-向下-向左-向上 的路径遍历，直到遍历完所以元素为止
        while (true) {
            // 向右遍历
            for (int i = leftLimit; i <= rightLimit; i++) {
                result.add(matrix[topLimit][i]);
            }
            if (result.size() == total)
                return result;
            topLimit++;

            // 向下遍历
            for (int i = topLimit; i <= bottomLimit; i++) {
                result.add(matrix[i][rightLimit]);
            }
            if (result.size() == total)
                return result;
            rightLimit--;

            // 向左遍历
            for (int i = rightLimit; i>= leftLimit; i--) {
                result.add(matrix[bottomLimit][i]);
            }
            if (result.size() == total)
                return result;
            bottomLimit--;

            // 向上遍历
            for (int i = bottomLimit; i >= topLimit; i--) {
                result.add(matrix[i][leftLimit]);
            }
            if (result.size() == total)
                return result;
            leftLimit++;
        }
    }
}
