package learn.leetcode;

/**
 * @author Zephyr
 * @since 2020-7/4.
 */
public class Easy008 {

    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(1, null);
        ListNode listNode2 = new ListNode(2, listNode1);
        ListNode listNode3 = new ListNode(1, listNode2);

        ListNode listNode4 = new ListNode(4, null);
        ListNode listNode5 = new ListNode(3, listNode4);
        ListNode listNode6 = new ListNode(1, listNode5);

        new Easy008().mergeTwoLists(listNode1, null);
    }


    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode head = null;
        //这里构建了一个空的头结点，这个结点在返回结果时将被忽略
        ListNode currentNode = new ListNode(-1);

        ListNode tempNode = null;
        while (l1 != null || l2 != null) {
            if (l1 != null && l2 != null) {
                tempNode = l1.val < l2.val ? l1 : l2;
            } else {
                // 其中一个list已经遍历完，把没有遍历完的list拼接到结果list的结尾即可
                // （也可能其中一个list原本就是空的）
                currentNode.next = l1 != null ? l1 : l2;
                return head == null ? currentNode.next : head;
            }
            currentNode.next =  new ListNode(tempNode.val);
            if (head == null)
                head = currentNode.next;
            currentNode = currentNode.next;
            if (tempNode == l1) {
                l1 = l1.next;
            } else {
                l2 = l2.next;
            }
        }

        /* 原始的、未优化的代码
        while (l1 != null || l2 != null) {
            ListNode tempNode = null;
            if (l1 != null && l2 != null) {
                if (l1.val < l2.val) {
                    currentNode.tempNode = new ListNode(l1.val);
                    currentNode = currentNode.tempNode;
                    l1 = l1.tempNode;
                } else {
                    currentNode.tempNode =  new ListNode(l2.val);
                    currentNode = currentNode.tempNode;
                    l2 = l2.tempNode;
                }
            } else {
                if (l1 != null) {
                    currentNode.tempNode =  new ListNode(l1.val);
                    currentNode = currentNode.tempNode;
                    l1 = l1.tempNode;
                } else {
                    currentNode.tempNode =  new ListNode(l2.val);
                    currentNode = currentNode.tempNode;
                    l2 = l2.tempNode;
                }
            }
        }*/
        return head;
    }


    /**
     * 递归的解法！重点理解
     * @param l1
     * @param l2
     * @return
     */
    public ListNode mergeTwoListsOfficial(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }
        else if (l2 == null) {
            return l1;
        }
        else if (l1.val < l2.val) {
            l1.next = mergeTwoLists(l1.next, l2);
            return l1;
        }
        else {
            l2.next = mergeTwoLists(l1, l2.next);
            return l2;
        }

    }

}
