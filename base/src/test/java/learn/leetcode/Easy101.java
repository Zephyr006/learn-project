package learn.leetcode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 101. 对称二叉树
 *
 * @author Zephyr
 * @date 2022/4/19.
 */
public class Easy101 {

    class Solution {
        /**
         * 递归的实现
         */
        public boolean isSymmetric(TreeNode root) {
            if (root == null) {
                return false;
            }
            return recursion(root.left, root.right);
        }

        boolean recursion(TreeNode left, TreeNode right) {
            if ((left == null && right != null) || (left != null && right == null)) {
                return false;
            }
            if (left == null && right == null) {
                return true;
            }
            return left.val == right.val && recursion(left.left, right.right) && recursion(left.right, right.left);
        }
    }

    class Solution2 {
        /**
         * 递归的实现
         */
        public boolean isSymmetric(TreeNode root) {
            if (root == null || (root.left==null && root.right==null)) {
                return true;
            }
            Queue<TreeNode> queue = new LinkedList<>();
            queue.add(root.left);
            queue.add(root.right);

            while (!queue.isEmpty()) {
                TreeNode left = queue.poll();
                TreeNode right = queue.poll();
                if (left == null && right == null) {
                    continue;
                }
                if (left != null && right == null) {
                    return false;
                }
                if (left == null && right != null) {
                    return false;
                }
                if (left.val != right.val) {
                    return false;
                }
                queue.add(left.left);
                queue.add(right.right);
                queue.add(left.right);
                queue.add(right.left);
            }

            return true;
        }

    }
}
