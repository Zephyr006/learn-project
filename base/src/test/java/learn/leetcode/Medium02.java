package learn.leetcode;

/**
 * 2. 两数相加
 *
 * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
 *
 * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
 *
 * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 * 示例：
 *
 * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 * 输出：7 -> 0 -> 8
 * 原因：342 + 465 = 807
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-two-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-07-19.
 */
public class Medium02 {

    /**
     * 傻瓜思路：使用变量来跟踪进位，并从包含最低有效位的表头开始模拟逐位相加的过程。
     * 直接对两个进行相加，因为左边本身就是低位，所以直接从连表头开始逐位向后处理即可
     * 记住：两个链表都处理到结尾时，务必查看进位标志位是否还有值！！！
     * 如果是只处理到其中一个链表结束就跳出循环的情况，务必查看另一个链表中的元素是否有剩余！！
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int carry = 0;
        ListNode result = new ListNode(-1);
        ListNode current = result;
        while (l1!=null || l2!=null) {
            int currentVal;
            if (l1 != null && l2 != null) {
                currentVal = l1.val + l2.val + carry;
            } else {
                currentVal = l1 != null ? l1.val + carry : l2.val + carry;
            }
            carry = currentVal / 10;
            current.next = new ListNode(carry > 0 ? currentVal-10 : currentVal);
            current = current.next;
            l1 = l1 != null ? l1.next : l1;
            l2 = l2 != null ? l2.next : l2;
        }
        //务必不要忘记最后的进位结果
        if (carry > 0) {
            current.next = new ListNode(carry);
        }
        return result.next;
    }
}
