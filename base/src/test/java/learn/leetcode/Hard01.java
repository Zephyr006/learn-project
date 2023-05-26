package learn.leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 432. 全 O(1) 的数据结构
 * https://leetcode-cn.com/problems/all-oone-data-structure/
 *
 * @author Zephyr
 * @since 2022-03-20.
 */
public class Hard01 {

    public static void main(String[] args) {
        //["AllOne","inc",    "inc",    "inc",    "dec",    "inc",    "inc", "getMaxKey","dec",    "dec",   "dec","getMaxKey"]
        //[[],    ["hello"],["world"],["hello"],["world"],["hello"],["leet"],    [],   ["hello"],["hello"],["hello"],[]]
        AllOne one = new AllOne();
        one.inc("hello");
        one.inc("world");
        one.inc("hello");
        one.dec("world");
        one.inc("hello");
        one.inc("leet");
        String maxKey = one.getMaxKey();
        one.dec("hello");
        one.dec("hello");
        one.dec("hello");
        String maxKey2 = one.getMaxKey();
        System.out.println(maxKey2);
    }

    /**
     * 实现思路：类似于lru算法，与普通map不同的是value要按顺序串连在一起，并增加head和tail节点方便getMaxKey
     * @link https//leetcode-cn.com/problems/all-oone-data-structure/solution/by-ac_oier-t26d/
     */
    static class AllOne {
        class Node {
            int count;
            Set<String> set = new HashSet<>();
            Node left, right;
            Node(int _cnt) {
                count = _cnt;
            }
        }

        Node head, tail;
        Map<String, Node> map = new HashMap<>();

        public AllOne() {
            // 初始化空的头结点和尾节点，避免null检查 !!
            // 注意维护map和set中的引用关系
            head = new Node(-1000); tail = new Node(-1000);
            head.right = tail; tail.left = head;
        }

        void tryClear(Node node) {
            if (node.set.size() == 0) {
                node.left.right = node.right;
                node.right.left = node.left;
                // help to gc
                node.left = null;
                node.right = null;
            }
        }

        void insertToRight(Node node, Node newNode) {
            node.right.left = newNode;
            newNode.right = node.right;
            newNode.left = node;
            node.right = newNode;
        }

        public void inc(String key) {
            Node node = map.get(key);
            //之前已经存在这个字符串
            if (node != null) {
                node.set.remove(key);
                Node next;
                // 计数为下一个值得节点存在
                if (node.right.count == node.count + 1) {
                    next = node.right;
                } else {
                    // 不存在count+1，需要insert
                    next = new Node(node.count + 1);
                    insertToRight(node, next);
                }
                map.put(key, next);
                next.set.add(key);
                //!! 当前key移动到下一个节点了，当前节点可能已经为空，需要clear
                tryClear(node);
            } else {
                Node node_1;
                // 左侧的第一个有效节点的计数值是否为1
                if (head.right.count == 1) {
                    node_1 = head.right;
                } else {
                    node_1 = new Node(1);
                    insertToRight(head, node_1);
                }
                node_1.set.add(key);
                map.put(key, node_1);
            }
        }

        public void dec(String key) {
            Node node = map.get(key);
            // 题目保证此处一定不为空
            if (node != null) {
                node.set.remove(key);
                int count = node.count;
                // key的计数值大于0，需要将key值重新插入到count-1的位置
                if (count - 1 > 0) {
                    Node prev;
                    // 如果存在count-1的位置
                    if (node.left.count == count - 1) {
                        prev = node.left;
                    // 如果不存在count-1的位置，新创建一个Node
                    } else {
                        prev = new Node(count - 1);
                        insertToRight(node.left, prev);
                    }
                    prev.set.add(key);
                    map.put(key, prev);
                } else {
                    map.remove(key);
                }
                tryClear(node);
            }
        }

        public String getMaxKey() {
            Node node = tail.left;
            for (String str : node.set)
                return str;
            return "";
        }

        public String getMinKey() {
            Node node = head.right;
            for (String str : node.set)
                return str;
            return "";
        }
    }

/**
 * Your AllOne object will be instantiated and called as such:
 * AllOne obj = new AllOne();
 * obj.inc(key);
 * obj.dec(key);
 * String param_3 = obj.getMaxKey();
 * String param_4 = obj.getMinKey();
 */
}
