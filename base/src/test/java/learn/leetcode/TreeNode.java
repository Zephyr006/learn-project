package learn.leetcode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Zephyr
 * @date 2022/3/20.
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

    public static TreeNode buildTree(Integer[] array) {
        TreeNode root = new TreeNode(array[0]);
        Queue<TreeNode> nextLevelNodes = new LinkedList<>();
        Queue<TreeNode> thisLevelNodes = new LinkedList<>();
        TreeNode node = root;
        for (int i = 1; i < array.length; i++) {
            if (array[i] != null) {
                node.left = new TreeNode(array[i]);
                nextLevelNodes.add(node.left);
            }
            // 处理下一个数字并检查是否下标越界
            if (++i >= array.length) {
                break;
            }
            if (array[i] != null) {
                node.right = new TreeNode(array[i]);
                nextLevelNodes.add(node.right);
            }

            // 当前层元素已全部处理完,继续处理下一层节点
            if (thisLevelNodes.isEmpty()) {
                Queue<TreeNode> temp = thisLevelNodes;
                thisLevelNodes = nextLevelNodes;
                nextLevelNodes = temp;
            }
            node = thisLevelNodes.poll();
        }
        return root;
    }
}
