package learn.leetcode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * 1161. 最大层内元素和
 * https://leetcode.cn/problems/maximum-level-sum-of-a-binary-tree/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium1161 {
    public static void main(String[] args) {
        TreeNode tree = LeetcodeHelper.toTree(1,7,0,7,-8,null,null);
        System.out.println(new Medium1161().maxLevelSum2(tree));

        tree = LeetcodeHelper.toTree(1, 1, 0, 7, -8, -7, 9);
        System.out.println(new Medium1161().maxLevelSum2(tree));

        tree = LeetcodeHelper.toTree(-1,-1,1,0,0);
        System.out.println(new Medium1161().maxLevelSum2(tree));
    }

    /**
     * 广度优先遍历：
     */
    public int maxLevelSum(TreeNode root) {
        LinkedList<TreeNode> queue1 = new LinkedList<>();
        queue1.add(root);
        int level = 1;
        TreeMap<Integer, List<Integer>> map = new TreeMap<>(Comparator.naturalOrder());

        while (!queue1.isEmpty()) {
            int sum = 0;
            LinkedList<TreeNode> queue2 = new LinkedList<>();
            while (!queue1.isEmpty()) {
                TreeNode node = queue1.removeFirst();
                if (node.left != null) {
                    queue2.add(node.left);
                }
                if (node.right != null) {
                    queue2.add(node.right);
                }
                sum += node.val;
            }
            // max = Math.max(max, sum);
            map.compute(sum, (k, v) -> v == null ? new LinkedList<>() : v).add(level);
            level++;
            queue1 = queue2;
        }

        return map.get(map.lastKey()).get(0);
    }

    public int maxLevelSum2(TreeNode root) {
        LinkedList<TreeNode> queue1 = new LinkedList<>();
        queue1.add(root);
        ArrayList<Integer> sumList = new ArrayList<>();
        int sum;
        while (!queue1.isEmpty()) {
            sum = 0;
            LinkedList<TreeNode> queue2 = new LinkedList<>();
            while (!queue1.isEmpty()) {
                TreeNode node = queue1.removeFirst();
                if (node.left != null) {
                    queue2.add(node.left);
                }
                if (node.right != null) {
                    queue2.add(node.right);
                }
                sum += node.val;
            }
            // max = Math.max(max, sum);
            sumList.add(sum);
            queue1 = queue2;
        }

        int level = 1;
        sum = sumList.get(0);
        for (int i = sumList.size() - 1; i >= 0; i--) {
            if (sumList.get(i) > sum || (sumList.get(i) == sum && level > i + 1)) {
                sum = sumList.get(i);
                level = i + 1;
            }
        }

        return level;
    }



}
