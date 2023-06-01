package learn.leetcode;

/**
 * 剑指 Offer 26. 树的子结构
 *
 * @author Zephyr
 * @date 2022/4/19.
 */
public class Offer26 {

    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode(int x) { val = x; }
     * }
     */
    class Solution {
        /**
         * https://leetcode-cn.com/problems/shu-de-zi-jie-gou-lcof/solution/mian-shi-ti-26-shu-de-zi-jie-gou-xian-xu-bian-li-p/
         */
        public boolean isSubStructure(LeetCodeHelper.TreeNode A, LeetCodeHelper.TreeNode B) {
            if (A == null || B == null) {
                return false;
            }
            return recursion(A, B) || isSubStructure(A.left, B) || isSubStructure(A.right, B);
        }

        /**
         * 递归判断两棵树是否完全匹配
         */
        boolean recursion(LeetCodeHelper.TreeNode A, LeetCodeHelper.TreeNode B) {
            //节点 BB 为空：说明树 BB 已匹配完成（越过叶子节点），因此返回 true
            if(B == null)
                return true;
            //节点 AA 为空：说明已经越过树 AA 叶子节点，即匹配失败，返回 false
            if (A == null)
                return false;
            //节点 AA 和 BB 的值不同：说明匹配失败，返回 false
            if(A.val != B.val)
                return false;
            // 判断A和B两颗树的节点的左右子树是否匹配
            return recursion(A.left, B.left) && recursion(A.right, B.right);
        }
    }
}
