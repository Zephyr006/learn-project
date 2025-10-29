package learn.leetcode;

/**
 * 236. 二叉树的最近公共祖先
 * https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium236 {
    public static void main(String[] args) {

    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        return dfs(root, p, q);
    }

    TreeNode dfs(TreeNode root, TreeNode p, TreeNode q) {
        // 1 结束条件：遍历结束，或者找到了 p q，公共父节点只会在上面的节点
        if (root == null || root == p || root == q) {
            return root;
        }

        // 2 dfs：向下遍历左子树和右子树，判断是否能返回公共节点
        TreeNode left = dfs(root.left, p, q);
        TreeNode right = dfs(root.right, p, q);

        // 当节点 p,q 在节点 root 的异侧时，节点 root 即为最近公共祖先
        if (left != null && right != null) {
            return root;
        } else if (left != null) {
            return left;
        } else if (right != null) {
            return right;
        // 当 left 和 right 同时为空 ：说明 root 的左 / 右子树中都不包含 p,q
        } else {
            return null;
        }
    }
}
