package learn.leetcode;

/**
 * 206. 反转链表
 *
 * @author Zephyr
 * @date 2022/4/13.
 */
public class Easy206 {

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        ListNode listNode = new Solution().reverseList(head);
        System.out.println(listNode);
    }

    static class Solution {

        /**
         * 官方解
         */
        public ListNode reverseList(ListNode head) {
            ListNode prev = null;
            ListNode curr = head;
            while (curr != null) {
                ListNode nextTemp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = nextTemp;
            }
            return prev;
        }
    }
}
