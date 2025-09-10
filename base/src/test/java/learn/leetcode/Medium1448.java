package learn.leetcode;

/**
 * 1448. 统计二叉树中好节点的数目
 * https://leetcode.cn/problems/count-good-nodes-in-binary-tree/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium1448 {
    public static void main(String[] args) {
        TreeNode root = LeetcodeHelper.toTree();
        int i = new Medium1448().goodNodes(root);

    }

    int count = 0;
    public int goodNodes(TreeNode root) {
        if (root == null) {
            return 0;
        }
        dfs(root, root.val);
        return count;
    }

    void dfs(TreeNode node, int maxVal) {
        // 1 截止条件
        if (node == null) {
            return;
        }

        // 2 dfs：
        if (maxVal <= node.val) {
            count++;
        }
        if (node.left != null) {
            dfs(node.left, Math.max(maxVal, node.val));
        }
        if (node.right != null) {
            dfs(node.right, Math.max(maxVal, node.val));
        }
        // 3 回溯
    }
}
