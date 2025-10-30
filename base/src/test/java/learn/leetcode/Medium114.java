package learn.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 114. 二叉树展开为链表
 * https://leetcode.cn/problems/flatten-binary-tree-to-linked-list/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium114 {

    /**
     * 二叉树的前序遍历，顺序： 根 - 左 - 右
     * 中序遍历见 {@link Medium230}
     */
    public void flatten(TreeNode root) {
        List<TreeNode> list = new ArrayList<>();
        LinkedList<TreeNode> stack = new LinkedList<>();

        TreeNode node = root;
        while (node != null || stack.size() > 0) {
            // 如果当前节点不为空，则记录当前节点，将当前节点入栈，然后继续遍历左子节点
            while (node != null) {
                list.add(node);
                stack.addLast(node);
                node = node.left;
            }

            // 取出上一个节点，并且尝试遍历右子节点
            node = stack.removeLast();
            node = node.right;
        }

        // 已经按前序遍历处理好了二叉树，处理每个节点的左右子节点引用即可构造链表
        for (int i = 1; i < list.size(); i++) {
            TreeNode prev = list.get(i - 1);
            TreeNode curr = list.get(i);
            prev.left = null;
            prev.right = curr;
        }
    }
}
