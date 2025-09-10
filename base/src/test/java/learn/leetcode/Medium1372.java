package learn.leetcode;

/**
 * 1372. 二叉树中的最长交错路径
 * https://leetcode.cn/problems/longest-zigzag-path-in-a-binary-tree/description/
 */
public class Medium1372 {
    int maxAns = 0;
    public int longestZigZag(TreeNode root) {
        if (root == null) {
            return 0;
        }
        dfs(root, false, 0);
        dfs(root, true, 0);
        return maxAns;
    }

    void dfs(TreeNode node, boolean toLeft, int depth) {
        // 结束条件：如果在这里判断 null，则 dfs 调用时不需要判断了
        // 如果在这里不判断 null，则在 dfs 调用时要先判断 null，优点是少了一次方法调用（入栈）
        if (node == null) {
            maxAns = Math.max(maxAns, depth - 1);
            return;
        }

        if (toLeft) {
            // if (node.right != null) {
                dfs(node.right, false, depth + 1);
            // }
            // if (node.left != null) {
                dfs(node.left, true, 1);
            // }
        } else {
            // if (node.right != null) {
                dfs(node.right, false, 1);
            // }
            // if (node.left != null) {
                dfs(node.left, true, depth + 1);
            // }
        }
    }

}
