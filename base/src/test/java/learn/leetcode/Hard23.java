package learn.leetcode;

/**
 * 23. 合并 K 个升序链表
 * https://leetcode.cn/problems/merge-k-sorted-lists/description/?envType=study-plan-v2&envId=top-interview-150
 */
public class Hard23 {

    /**
     * 分治：先细化合并的粒度，一直细化到链表两两合并，再执行最终的合并动作
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        return merge(lists, 0, lists.length - 1);
    }

    private ListNode merge(ListNode[] lists, int start, int end) {
        if (start == end) {
            return lists[start];
        // } else if (start > end) {//在测试用例里面不会出现这种情况，删掉也能通过
        //     return null;
        } else {
            int mid = start + (end - start) / 2;
            ListNode left = merge(lists, start, mid);
            ListNode right = merge(lists, mid + 1, end);
            return mergeTwoListNode(left, right);
        }
    }

    private ListNode mergeTwoListNode(ListNode l1, ListNode l2) {
        if (l1 == null)
            return l2;
        if (l2 == null)
            return l1;
        ListNode head = new ListNode(0);
        ListNode tail = head;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                tail.next = l1;
                tail = tail.next;
                l1 = l1.next;
            } else {
                tail.next = l2;
                tail = tail.next;
                l2 = l2.next;
            }
        }
        if (l1 != null) {
            tail.next = l1;
        }
        if (l2 != null) {
            tail.next = l2;
        }
        return head.next;
    }


}
