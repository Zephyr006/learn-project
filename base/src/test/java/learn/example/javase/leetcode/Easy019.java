package learn.example.javase.leetcode;

/**
 * 83. 删除排序链表中的重复元素
 *
 * 给定一个排序链表，删除所有重复的元素，使得每个元素只出现一次。
 *
 * 示例 1:
 *
 * 输入: 1->1->2
 * 输出: 1->2
 * 示例 2:
 *
 * 输入: 1->1->2->3->3
 * 输出: 1->2->3
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/7/13.
 */
public class Easy019 {

    public ListNode deleteDuplicates(ListNode head) {
        ListNode current = head;
        // 必须要判断 current != null ，以应对空list
        while (current != null && current.next != null) {
            if (current.val == current.next.val) {
                current.next = current.next == null ? null : current.next.next;
            } else {
                current = current.next;
            }
        }
        return head;
    }

    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(1, null);
        ListNode listNode2 = new ListNode(1, listNode1);
        ListNode listNode3 = new ListNode(1, listNode2);

        ListNode listNode = new Easy019().deleteDuplicates(listNode3);
    }
}
