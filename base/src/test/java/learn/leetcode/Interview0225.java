package learn.leetcode;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 面试题 02.05. 链表求和
 *
 * https://leetcode.cn/problems/sum-lists-lcci/description/
 */
public class Interview0225 {

    public static void main(String[] args) {
        ListNode listNode = LeetcodeHelper.toListNode(1, 2, 3);
        ListNode listNode2 = LeetcodeHelper.toListNode(2, 3, 4);
        // ListNode reverse = new Interview0225().reverse(listNode);
        // LeetcodeHelper.printListNode(reverse);

        ListNode result = new Interview0225().addTwoNumbers(listNode, listNode2);
        // LeetcodeHelper.print(result);
        // 这些数位是正向存放的
        result = new Interview0225().addTwoNumbers2(
                LeetcodeHelper.toListNode(6, 1, 7), LeetcodeHelper.toListNode(2, 9, 5));
        LeetcodeHelper.print(result);
    }

    /**
     * 假设这些数位是正向存放的，那么就先翻转，再计算，最终再翻转结果
     */
    public ListNode addTwoNumbersReverse(ListNode l1, ListNode l2) {
        ListNode result = addTwoNumbers(reverse(l1), reverse(l2));
        return reverse(result);
    }

    /**
     * 进阶问题中，输入的两个链表都是正向存放数字的位数的，因此链表中数位的顺序与我们做加法的顺序是相反的。
     * 用栈解决正向存放顺序的问题，注意存放结果时，需要正向存放，也就是结果值在结果链表的 头
     */
    public ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
        Deque<Integer> stack1 = new ArrayDeque<Integer>();
        Deque<Integer> stack2 = new ArrayDeque<Integer>();
        while (l1 != null) {
            stack1.push(l1.val);
            l1 = l1.next;
        }
        while (l2 != null) {
            stack2.push(l2.val);
            l2 = l2.next;
        }
        int carry = 0;

        ListNode result = new ListNode(0);
        while (!stack1.isEmpty() || !stack2.isEmpty()) {
            int sum = carry;
            if (!stack1.isEmpty()) {
                sum += stack1.pop();
            }
            if (!stack2.isEmpty()) {
                sum += stack2.pop();
            }
            carry = sum / 10;
            ListNode temp = result.next;
            result.next = new ListNode(sum % 10);
            result.next.next = temp;
        }
        return result.next;
    }


    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode prev1 = l1;
        ListNode prev2 = l2;
        int carry = 0; //进位
        ListNode result = new ListNode(0);
        ListNode resultTail = result;
        while (prev1 != null || prev2 != null) {
            int sum = carry;
            if (prev1 != null) {
                sum += prev1.val;
                prev1 = prev1.next;

            }
            if (prev2 != null) {
                sum += prev2.val;
                prev2 = prev2.next;
            }
            carry = sum / 10;
            resultTail.next =  new ListNode(sum % 10);
            resultTail = resultTail.next;
        }
        if (carry > 0) {
            resultTail.next = new ListNode(carry);
        }
        return result.next;
    }

    private ListNode reverse(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode result = null;
        while (head != null) {
            ListNode temp = head;
            head = head.next;
            temp.next = result;
            result = temp;
        }
        return result;
    }

}
