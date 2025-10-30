package learn.leetcode;

/**
 * 437. 路径总和 III
 * https://leetcode.cn/problems/path-sum-iii/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium437 {
    public static void main(String[] args) {
        TreeNode tree = LeetcodeHelper.toTree(1, null, 2, null, 3, null, 4, null, 5);
        int sum = new Medium437().pathSum(tree, 3);
        System.out.println(sum);
    }

    // 主方法：计算以所有节点为起点的有效路径总和
    public int pathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return 0;
        }
        // 1. 以当前节点为起点的有效路径数总和
        int current = dfs(root, targetSum);
        // 2. 以左右子树作为路径的起点！！！搜索左子树中所有可能的路径数（递归）
        int left = pathSum(root.left, targetSum);
        int right = pathSum(root.right, targetSum);
        return current + left + right;
    }

    // 辅助方法：计算以当前节点为起点，路径和等于targetSum的路径数
    private int dfs(TreeNode root, int targetSum) { // 用long避免int溢出
        if (root == null) {
            return 0;
        }

        int count = 0;
        // 当前节点值等于剩余目标和，计数+1
        if (root.val == targetSum) {
            count++;
        }

        // 递归左右子树：目标和减去当前节点值
        count += dfs(root.left, targetSum - root.val);
        count += dfs(root.right, targetSum - root.val);
        return count;
    }
}
