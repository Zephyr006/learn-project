package learn.leetcode;

/**
 * 25. K 个一组翻转链表
 * https://leetcode.cn/problems/reverse-nodes-in-k-group/description/
 */
public class Hard25 {
    public static void main(String[] args) {
        ListNode node = new Hard25().reverseKGroup(LeetcodeHelper.toListNode(1, 2, 3, 4, 5), 2);
        LeetcodeHelper.print(node);
    }

    /**
     * 模拟：每次单独反转 k 个节点，并把反转后的 k 个节点和前后相关节点连接上
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || head.next == null || k == 0 || k == 1) {
            return head;
        }
        ListNode subHead = head;
        ListNode subTail;
        ListNode nextHead;
        ListNode prevGroupTail = null;

        int count = 0;
        while (true) {
            count++;
            subTail = subHead;
            // 遍历找到一组节点（k 个），跳出循环时，subTail 指针应该指向这一组的最后一个节点
            while (subTail != null && subTail.next != null && count % k != 0) {
                subTail = subTail.next;
                count++;
            }
            // 如果这一组节点够 k 个，则反转这一组节点，否则不需要反转，直接结束循环，返回结果
            if (count % k == 0) {
                // 暂存下一组要反转的节点头，并把当前组的 k 个节点断开尾部，用于反转
                nextHead = subTail.next;
                subTail.next = null;
                ListNode reversedHead = reverse(subHead);

                // 如果是第一组节点 要保存到 head 指针，用于返回结果
                if (prevGroupTail == null) {
                    head = reversedHead;
                // 否则说明不是第一组节点，需要把前面一组节点的尾部与当前这组节点连接上
                } else {
                    prevGroupTail.next = reversedHead;
                }
                prevGroupTail = subHead;

                // 拼接已反转的组和后面的节点头：subHead 指针指向下一组要处理的链表节点头
                subHead.next = nextHead;
                subHead = nextHead;
            } else {
                break;
            }
        }
        return head;
    }

    private ListNode reverse(ListNode head) {
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
