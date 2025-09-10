package learn.leetcode;

/**
 *2095. 删除链表的中间节点
 *
 * https://leetcode.cn/problems/delete-the-middle-node-of-a-linked-list/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium2095 {
    public static void main(String[] args) {
        ListNode listNode = LeetcodeHelper.toListNode(1, 2, 3, 4);
        ListNode result = new Medium2095().deleteMiddle(listNode);
        LeetcodeHelper.print(result);
    }

    /**
     * 快慢指针：快指针向后遍历，慢指针找中间节点的前一个节点
     */
    public ListNode deleteMiddle(ListNode head) {
        // 特殊情况处理
        if (head == null || head.next == null) {
            return null;
        }

        // 总节点数
        int n = 1;
        // 记录第 n/2 个节点的下标位置，用于移动 slow 节点
        int mid = 0;
        // slow 指向需要删除节点的前一个节点，用于最终删除中间节点
        ListNode slow = new ListNode(0, head);

        ListNode fast = head;// 用于向后遍历的节点指针
        while (fast.next != null) {
            n++;
            if (n / 2 != mid) {
                mid = n / 2;
                slow = slow.next;
            }
            fast = fast.next;
        }
        // 找到中间节点，执行删除
        slow.next = slow.next.next;
        return head;
    }
}
