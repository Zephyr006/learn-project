package learn.leetcode;

/**
 * 206. 反转链表
 *
 * @author Zephyr
 * @date 2022/4/13.
 */
public class Easy206 {

    public static void main(String[] args) {
        LeetCodeHelper.ListNode head = new LeetCodeHelper.ListNode(1);
        head.next = new LeetCodeHelper.ListNode(2);
        LeetCodeHelper.ListNode listNode = new Solution().reverseList(head);
        System.out.println(listNode);
    }

    static class Solution {

        /**
         * 官方解
         */
        public LeetCodeHelper.ListNode reverseList(LeetCodeHelper.ListNode head) {
            LeetCodeHelper.ListNode prev = null;
            LeetCodeHelper.ListNode curr = head;
            while (curr != null) {
                LeetCodeHelper.ListNode nextTemp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = nextTemp;
            }
            return prev;
        }
    }
}
