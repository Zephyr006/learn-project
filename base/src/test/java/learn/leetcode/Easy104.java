package learn.leetcode;


/**
 * 104. 二叉树的最大深度
 *
 * 给定一个二叉树，找出其最大深度。
 *
 * 二叉树的深度为根节点到最远叶子节点的最长路径上的节点数。
 *
 * 说明: 叶子节点是指没有子节点的节点。
 *
 * 示例：
 * 给定二叉树 [3,9,20,null,null,15,7]，
 *
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 * 返回它的最大深度 3 。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/maximum-depth-of-binary-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2021/8/5.
 */
public class Easy104 {

    public static void main(String[] args) {

        int maxDepth = new Solution().maxDepth(LeetcodeHelper.toTree(3,9,20,null,null,15,7));
        System.out.println(maxDepth);
    }


    /**
     * 深度优先遍历二叉树：先递归计算出其左子树和右子树的最大深度，然后在 O(1)O(1) 时间内计算出当前二叉树的最大深度（ max(left,right)+1 ）
     */
    static class Solution {
        public int maxDepth(TreeNode root) {
            // 1 截止条件
            if (root == null) {
                return 0;
            }

            // 2 dfs：分别遍历左右子节点，
            int leftDepth = maxDepth(root.left);
            int rightDepth = maxDepth(root.right);

            // 这里的 1 指的是当前层的深度
            return Math.max(leftDepth, rightDepth) + 1;
        }
    }
}
