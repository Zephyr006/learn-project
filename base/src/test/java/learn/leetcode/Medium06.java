package learn.leetcode;

/**
 *  删除链表的倒数第N个节点
 *
 *  给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。
 *
 * 示例：
 *
 * 给定一个链表: 1->2->3->4->5, 和 n = 2.
 *
 * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
 * 说明：
 *
 * 给定的 n 保证是有效的。
 *
 * 进阶：
 *
 * 你能尝试使用一趟扫描实现吗？
 *
 * https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/
 *
 * @author Zephyr
 * @date 2020/8/15.
 */
public class Medium06 {

    public static void main(String[] args) {
        ListNode listNode = ListNode.build(1, 2, 3, 4);
        new Medium06().removeNthFromEnd(listNode, 2);
    }

    // 双指针，第一个指针遍历整个链表直到链表的结尾，第二个指针指到需要删除的节点的前一个节点
    // 遍历完成后，做删除节点操作并返回结果
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (head == null)
            return null;

        ListNode o = head;
        ListNode previewNeedDel = head;

        // 定位到要删除的节点的前一个节点，previewNeedDel的初始值必须为head，然后向后偏移
        int i = 1;
        for (; head != null; i++) {
            if (i > n+1) {
                previewNeedDel = previewNeedDel.next;
            }
            head = head.next;
        }
        // 题目要求里写明了n一定有效，所以这里其实永远为false
        if (i-1 == n) {
            return o.next;
        }
        if (i-1 > n){
            // 删除应该删除的节点
            ListNode needDel = previewNeedDel.next;
            previewNeedDel.next = previewNeedDel.next.next;
            needDel.next = null;
        }
        return o;
    }

    public ListNode removeNthFromEndOfficial(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode first = dummy;
        ListNode second = dummy;
        // Advances first pointer so that the gap between first and second is n nodes apart
        for (int i = 1; i <= n + 1; i++) {
            first = first.next;
        }
        // Move first to the end, maintaining the gap
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        second.next = second.next.next;
        return dummy.next;
    }

}
