package learn.leetcode;

/**
 * 437. 路径总和 III
 * https://leetcode.cn/problems/path-sum-iii/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium437 {
    public static void main(String[] args) {

    }

    int count = 0;
    public int pathSum(TreeNode root, int targetSum) {
        dfs(root, targetSum, 0);
        return count;
    }

    /**
     * todo 未完成
     */
    int dfs(TreeNode root, int targetSum, int currentSum) {
        // 1 截止条件：
        if (root == null) {
            return 0;
        }

        // dfs
        currentSum += root.val;
        if (currentSum == targetSum) {
            count++;
        }
        dfs(root.left, targetSum, currentSum);
        dfs(root.right, targetSum, currentSum);
        return currentSum -= root.val;
    }
}
