package learn.leetcode;

/**
 * 142. 环形链表 II
 * https://leetcode.cn/problems/linked-list-cycle-ii/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium142 {

    /**
     * 链表/快慢指针：
     * 快慢指针，先尝试找到第一个相遇点，证明链表有环
     * 再让指针从 head 开始出发，和慢指针一样每次走一步，再次相遇即是结果（环的入口）
     */
    public ListNode detectCycle(ListNode head) {
        ListNode fast = head, slow = head;

        while (true) {
            if (fast == null || fast.next == null) {
                return null;
            }
            fast = fast.next.next;
            slow = slow.next;
            // 找到第一个相遇点，证明链表有环
            if (fast == slow) {
                break;
            }
        }

        // 设 slow 走的总路径为 s ，fast 走的就是 2s ，同时又有 fast 的路径总共走过了 a + nb （从链表头到环的入口+在环中转了若干圈）
        // 由此得出 2s = s + a + nb ，所以 s = nb （等价于 slow 走了环的若干圈）
        // 此时 slow 已经走了 s = nb 步，还需要 a （从链表头到环的入口）步即可，总路径 a + nb 结束点刚好是环的开头/结尾
        // 所以复用 fast 指针，让他从 head 开始每次一步，再次相遇时就是环的入口
        fast = head;
        while (fast != slow) {
            fast = fast.next;
            slow = slow.next;
        }
        return fast;
    }
}
