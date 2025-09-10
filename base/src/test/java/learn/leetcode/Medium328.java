package learn.leetcode;

/**
 * 328. 奇偶链表
 *
 * https://leetcode.cn/problems/odd-even-linked-list/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium328 {
    public static void main(String[] args) {
        ListNode result = new Medium328().oddEvenList(LeetcodeHelper.toListNode(1, 2, 3, 4));
        LeetcodeHelper.print(result);
        result = new Medium328().oddEvenList(LeetcodeHelper.toListNode(1, 2, 3, 4, 5));
        LeetcodeHelper.print(result);
    }
    /**
     * 链表：先遍历链表，将整个链表分成奇数节点和偶数节点两部分，再把偶数节点链表拼接到奇数链表结尾
     * 每次处理两个节点，结束条件是 oddCurrent != null && oddCurrent.next != null
     */
    public ListNode oddEvenList(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) {
            return head;
        }
        ListNode evenHead = head.next;
        ListNode evenTail = evenHead;
        ListNode oddCurrent = head;
        ListNode oddTail = oddCurrent;
        while (oddCurrent != null && oddCurrent.next != null) {
            // 如果不是第一个偶节点，把当前偶节点放到偶链表的尾，并移动尾指针
            if (evenTail != oddCurrent.next) {
                evenTail.next = oddCurrent.next;
                evenTail = evenTail.next;
            }
            oddCurrent.next = oddCurrent.next.next;
            oddTail = oddCurrent.next == null ? oddCurrent : oddCurrent.next;
            oddCurrent = oddCurrent.next;
            evenTail.next = null;
        }

        // 把偶数链表拼接到奇数链表的后面
        oddTail.next = evenHead;
        return head;
    }
}
