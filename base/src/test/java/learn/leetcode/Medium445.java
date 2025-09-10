package learn.leetcode;

import java.util.LinkedList;

/**
 *445. 两数相加 II
 * https://leetcode.cn/problems/add-two-numbers-ii/description/
 */
public class Medium445 {
    public static void main(String[] args) {
        ListNode listNode1 = LeetcodeHelper.toListNode(7, 2, 4, 3);
        ListNode listNode2 = LeetcodeHelper.toListNode(5, 6, 4);
        ListNode res = new Medium445().addTwoNumbers(listNode1, listNode2);
        LeetcodeHelper.print(res);

        int[] arrays = new Medium445().addArrays(LeetcodeHelper.toIntArray("7,8,9"), LeetcodeHelper.toIntArray("[7,8]"));
        LeetcodeHelper.print(arrays);
        arrays = new Medium445().addArrays(LeetcodeHelper.toIntArray("9,8,9"), LeetcodeHelper.toIntArray("[7,8]"));
        LeetcodeHelper.print(arrays);
    }

    /**
     * 两个数组相加，数组中的每一个元素都是“大数”中的一位
     */
    public int[] addArrays(int[] num1, int[] num2) {
        int maxLen = Math.max(num1.length, num2.length);
        int[] res = new int[maxLen + 1];
        int carry = 0;
        for (int i = maxLen -1; i >= 0; i--) {
            int idx1 = i - (maxLen - num1.length);
            int idx2 = i - (maxLen - num2.length);
            int sum = (idx1 < 0 ? 0 : num1[idx1]) + (idx2 < 0 ? 0 : num2[idx2]) + carry;
            res[i + 1] = sum % 10;
            carry = sum / 10;
        }
        if (carry > 0) {
            res[0] = carry;
            return res;
        }
        int[] newRes = new int[maxLen];
        System.arraycopy(res, 1, newRes, 0, maxLen);
        return newRes;
    }

    /**
     * 栈：借助栈翻转遍历顺序，注意 判断链表长度不一致的情况，和最终进位值的处理
     */
    public ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
        LinkedList<Integer> list1 = new LinkedList<>();
        LinkedList<Integer> list2 = new LinkedList<>();

        while (l1 != null) {
            list1.add(l1.val);
            l1 = l1.next;
        }
        while (l2 != null) {
            list2.add(l2.val);
            l2 = l2.next;
        }
        int carry = 0;
        ListNode head = new ListNode(0);
        ListNode pre;

        while (list1.size() > 0 || list2.size() > 0) {
            Integer i = list1.pollLast();
            Integer j = list2.pollLast();
            int sum = (i == null ? 0 : i) + (j == null ? 0 : j) + carry;
            carry = sum / 10;
            pre = head.next;
            head.next = new ListNode(sum % 10);
            head.next.next = pre;
        }
        if (carry > 0) {
            head.val = carry;
            return head;
        }
        return head.next;
    }


    /**
     * 翻转链表：先反转链表，再计算累加
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        l1 = reverse(l1);
        l2 = reverse(l2);
        int carry = 0;
        ListNode head = new ListNode(0);

        while (l1 != null || l2 != null) {
            int sum = (l1 == null ? 0 : l1.val) + (l2 == null ? 0 : l2.val) + carry;
            carry = sum / 10;
            ListNode node = new ListNode(sum % 10);
            node.next = head.next;
            head.next = node;

            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }
        if (carry > 0) {
            head.val = carry;
            return head;
        }
        return head.next;
    }

    private ListNode reverse(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode pre = null;
        ListNode current = head;
        while (current != null) {
            // 暂时持有，避免翻转后丢失
            ListNode next = current.next;
            // 翻转后的指针赋值
            current.next = pre;
            // 指针整体向后移动一个 node
            pre = current;
            current = next;
        }
        return pre;
    }
}
