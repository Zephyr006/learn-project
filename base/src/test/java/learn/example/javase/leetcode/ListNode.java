package learn.example.javase.leetcode;

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

    public static ListNode build(int... nums) {
        ListNode head = new ListNode(nums[0]);
        ListNode node = head;

        for (int i = 1; i < nums.length; i++) {
            node.next = new ListNode(nums[i]);
            node = node.next;
        }
        return head;
    }

    @Override
    public String toString() {
        return "ListNode{" +
                "val=" + val +
                '}';
    }
}
