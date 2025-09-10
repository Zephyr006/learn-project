package learn.leetcode;

import java.util.LinkedList;
import java.util.Objects;

public class Easy872 {
    public static void main(String[] args) {

    }

    public boolean leafSimilar(TreeNode root1, TreeNode root2) {
        LinkedList<Integer> leafList1 = new LinkedList<>();
        LinkedList<Integer> leafList2 = new LinkedList<>();
        dfs(root1, leafList1);
        dfs(root2, leafList2);

        if (leafList1.size() != leafList2.size()) {
            return false;
        }

        while (leafList1.size() > 0 && leafList2.size() > 0) {
            if (!Objects.equals(leafList1.removeFirst(), leafList2.removeFirst())) {
                return false;
            }
        }
        return leafList1.size() == leafList2.size();
    }

    void dfs(TreeNode root, LinkedList<Integer> leafList) {
        // 1 结束条件
        if (root == null) {
            return;
        }

        // 2 dfs：
        if (root.left == null && root.right == null) {
            leafList.add(root.val);
        } else if (root.left != null && root.right != null) {
            dfs(root.left, leafList);
            dfs(root.right, leafList);
        } else if (root.left == null) {
            dfs(root.right, leafList);
        } else {
            dfs(root.left, leafList);
        }

        // 3 回溯
    }
}
