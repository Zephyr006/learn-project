package learn.leetcode;

/**
 * @author Zephyr
 * @date 2020/7/13.
 */
public class ListNode {

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
