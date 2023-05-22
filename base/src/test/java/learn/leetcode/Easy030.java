package learn.leetcode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * 606. 根据二叉树创建字符串
 * https://leetcode-cn.com/problems/construct-string-from-binary-tree/
 *
 * @author Zephyr
 * @date 2022/3/20.
 */
public class Easy030 {

    class Solution {
        StringBuilder sb = new StringBuilder();
        public String tree2str(TreeNode root) {
            dfs(root);
            // 根节点不需要括号
            return sb.substring(1, sb.length() - 1);
        }

        /**
         * 递归解法：深度优先，遍历树
         */
        private void dfs(TreeNode root) {
            sb.append('(').append(root.val);
            if (root.left != null) {
                dfs(root.left);
            }
            if (root.right != null) {
                if (root.left == null) {
                    sb.append("()");
                }
                dfs(root.right);
            }
            sb.append(')');
        }

        /**
         * 使用栈来迭代遍历，代替递归
         *
         * 由于当以某个节点 xx 为根节点时，其需要在 开始 前序遍历当前子树时添加 (，在 结束 前序遍历时添加 )，因此某个节点需要出入队两次。
         * 同时区分是首次出队（开始前序遍历）还是二次出队（结束前序遍历），这需要使用一个 set 来做记录，其余逻辑与「递归」做法类似。
         */
        public String tree2strWithoutRecursion(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            // 用于记录是否已经处理过当前节点，如果已经处理过则需要加 ")" 作为节点的结束
            Set<TreeNode> vis = new HashSet<>();
            // 用于模拟栈，last作为栈的头
            Deque<TreeNode> d = new ArrayDeque<>();
            d.addLast(root);
            while (!d.isEmpty()) {
                TreeNode t = d.pollLast();
                // 判断是否为二次出栈
                if (vis.contains(t)) {
                    sb.append(")");
                } else {
                    d.addLast(t);
                    sb.append("(");
                    sb.append(t.val);
                    // 记录当前节点已处理
                    vis.add(t);
                    // 由于栈先入先出，所以先将右节点入栈
                    if (t.right != null)
                        d.addLast(t.right);
                    // 如果当前节点的左孩子不为空，则入栈，否则要 append("()")
                    if (t.left != null)
                        d.addLast(t.left);
                    else if (t.right != null)
                        sb.append("()");
                }
            }
            // 去掉根节点的左右括号
            return sb.substring(1, sb.length() - 1);
        }

    }
}
