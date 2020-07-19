package learn.leetcode;

/**
 * 反转单向链表
 * https://blog.csdn.net/superxiaolong123/article/details/86687733
 *
 * @author Zephyr
 * @date 2020/7/12.
 */
public class ReverseLinkedList {

    public ListNode reverse(ListNode nodes) {
        ListNode resultList = new ListNode(-1, null);
        resultList.next= nodes;
        ListNode pNext = nodes.next;

        while (pNext!=null){
            nodes.next = pNext.next;
            pNext.next = resultList.next;
            resultList.next = pNext;
            pNext= nodes.next;
        }
        return resultList.next;
    }


    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(1, null);
        ListNode listNode2 = new ListNode(2, listNode1);
        ListNode listNode3 = new ListNode(3, listNode2);
        ListNode listNode4 = new ListNode(4, listNode3);

        ListNode reverse = new ReverseLinkedList().reverse(listNode4);
        while (reverse != null) {
            System.out.println(reverse.toString());
            reverse = reverse.next;
        }
    }

    
}
