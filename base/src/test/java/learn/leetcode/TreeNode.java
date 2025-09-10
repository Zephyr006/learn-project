package learn.leetcode;

/**
 * @author Zephyr
 * @since 2022-03-20.
 */
class TreeNode {

    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TreeNode{").append("val=").append(val);
        if (left != null) {
            sb.append(", left=").append(left.val);
        }
        if (right != null) {
            sb.append(", right=").append(right.val);
        }
        return sb.append('}').toString();
    }

}
