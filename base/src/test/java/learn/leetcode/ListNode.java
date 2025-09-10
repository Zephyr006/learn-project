package learn.leetcode;

/**
 * 用于LeetCode中链表相关的题目，标识一个链表节点
 *
 * @author Zephyr
 * @since 2020-07-13.
 */
class ListNode {

    public int val;
    public ListNode next;

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
