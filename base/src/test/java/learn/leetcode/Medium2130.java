package learn.leetcode;

/**
 * 2130. 链表最大孪生和
 *
 * https://leetcode.cn/problems/maximum-twin-sum-of-a-linked-list/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium2130 {
    public static void main(String[] args) {
        int pairSum = new Medium2130().pairSum(LeetcodeHelper.toListNode(4, 2, 2, 3));
        System.out.println(pairSum);

         pairSum = new Medium2130().pairSum(LeetcodeHelper.toListNode(4, 200000));
        System.out.println(pairSum);
    }

    public int pairSum(ListNode head) {
        if (head == null || head.next == null) {
            return 0;
        }
        // 首先使用快慢指针，找到链表的中间，快指针每次移动两个节点，慢指针一次移动一个节点（长度一定为偶数）
        ListNode slow = new ListNode(0, head);
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // 记录链表右半部分的头，并把左右两部分分开（slow.next = null）
        ListNode slowHead = slow.next;
        slow.next = null;
        // 链表翻转，然后遍历记录孪生和
        ListNode rightHead = reverse(slowHead);
        int max = 0;
        while (rightHead != null) {
            max = Math.max(max, rightHead.val + head.val);
            rightHead = rightHead.next;
            head = head.next;
        }
        return max;
    }

    private ListNode reverse(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        // eg. 1 -> 2 -> 3
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next; //2  //3
            cur.next = pre;  //1  2 -> 3   //2 -> 1   3
            pre = cur;       //1           //2
            cur = next;      //2           //3
        }
        return pre;
    }

}
