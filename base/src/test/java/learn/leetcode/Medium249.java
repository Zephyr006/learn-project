package learn.leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 429. N 叉树的层序遍历
 *
 * @author Zephyr
 * @date 2022/4/10.
 */
public class Medium249 {


    class Solution {
        /**
         * 用两个队列实现的版本
         */
        public List<List<Integer>> levelOrder(Node root) {
            if (root == null) {
                return Collections.emptyList();
            }
            Queue<Node> thisLevel = new LinkedList<>();
            Queue<Node> nextLevel = new LinkedList<>();
            thisLevel.add(root);

            List<List<Integer>> result = new ArrayList<>();
            while (!thisLevel.isEmpty()) {
                List<Integer> currentRes = new ArrayList<>(thisLevel.size());
                // list current level
                Node node;
                while ((node = thisLevel.poll()) != null) {
                    currentRes.add(node.val);
                    if (node.children != null && !node.children.isEmpty()) {
                        nextLevel.addAll(node.children);
                    }
                }
                result.add(currentRes);
                // swap current level and next level
                Queue<Node> temp = thisLevel;
                thisLevel = nextLevel;
                nextLevel = temp;
            }
            return result;
        }


        /**
         * 优化：用一个队列保存中间结果的版本，
         */
        public List<List<Integer>> levelOrder2(Node root) {
            if (root == null) {
                return Collections.emptyList();
            }
            Queue<Node> queue = new LinkedList<>();
            queue.add(root);
            List<List<Integer>> result = new ArrayList<>();

            while (!queue.isEmpty()) {
                // 注意这里：当前层的节点个数！！
                int currentLevelNodeSize = queue.size();

                List<Integer> currentRes = new ArrayList<>(currentLevelNodeSize);
                while (currentLevelNodeSize-- > 0) {
                    Node node = queue.poll();
                    currentRes.add(node.val);
                    if (node.children != null && !node.children.isEmpty()) {
                        queue.addAll(node.children);
                    }
                }
                result.add(currentRes);
            }

            return result;
        }
    }

    private static class Node {
        public int val;
        public List<Node> children;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    };
}
