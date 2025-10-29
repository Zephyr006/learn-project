package learn.leetcode;

import java.util.LinkedList;

/**
 * 230. 二叉搜索树中第 K 小的元素
 * https://leetcode.cn/problems/kth-smallest-element-in-a-bst/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium230 {

    /**
     * 二叉树的中序遍历：左 - 根 - 右
     * 前序遍历见 {@link Medium114}
     */
    public int kthSmallest(TreeNode root, int k) {
        LinkedList<TreeNode> stack = new LinkedList();
        // 循环处理，直到处理完所有元素
        while (root != null || stack.size() > 0) {
            // 中序遍历：把父节点入栈，优先处理左子结点
            while (root != null) {
                stack.push(root);
                root = root.left;
            }

            // 从栈中取值，并对 k 计数，直到找到第 k 小的目标值为止
            root = stack.pop();
            k--;
            if (k == 0) {
                break;
            }
            // 处理右子节点
            root = root.right;
        }
        return root.val;
    }
}
