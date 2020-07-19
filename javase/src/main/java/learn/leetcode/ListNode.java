package learn.leetcode;

/**
 * 用于LeetCode中链表相关的题目，标识一个链表节点
 *
 * @author Zephyr
 * @date 2020/7/13.
 */
class ListNode {

    int val;
    ListNode next;

    public ListNode(int x) {
        val = x;
    }
    public ListNode(int x, ListNode next) {
        val = x;
        this.next = next;
    }

    @Override
    public String toString() {
        return "ListNode{" +
                "val=" + val +
                '}';
    }
}
