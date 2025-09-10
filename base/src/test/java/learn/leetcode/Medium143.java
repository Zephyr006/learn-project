package learn.leetcode;

/**
 * 143. 重排链表
 * https://leetcode.cn/problems/reorder-list/description/?envType=company&envId=meituan&favoriteSlug=meituan-thirty-days
 */
public class Medium143 {
    public static void main(String[] args) {
        new Medium143().reorderList(LeetcodeHelper.toListNode(new int[]{1, 2, 3, 4, 5}));
    }

    public void reorderList(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) {
            return;
        }

        // 找到链表的中间位置，将其断开，把右半部分链表反转链表顺序
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode rightPart = reverse(slow.next);

        // 按顺序交叉放置两个链表的每个节点
        slow.next = null;
        slow = head;
        while (slow != null && rightPart != null) {
            ListNode temp = slow.next;
            slow.next = rightPart;
            rightPart = rightPart.next;
            slow = slow.next;
            slow.next = temp;
            slow = slow.next;
        }
        LeetcodeHelper.print(head);
    }

    public long maxProduct(int[] nums) {
        long max = Long.MIN_VALUE;
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if ((nums[i] & nums[j]) == 0 && nums[i] * (long)nums[j] > max) {
                    max = nums[i] * (long)nums[j];
                }
            }
        }

        if (max == Long.MIN_VALUE) {
            return 0;
        }
        return max;
    }

    private ListNode reverse(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }
}
