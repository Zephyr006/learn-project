package learn.leetcode;

/**
 * 反转单向链表
 * https://blog.csdn.net/superxiaolong123/article/details/86687733
 *
 * @author Zephyr
 * @date 2020/7/12.
 */
public class ReverseLinkedList {

    public LinkedNode reverse(LinkedNode nodes) {
        LinkedNode resultList = new LinkedNode(-1, null);
        resultList.next= nodes;
        LinkedNode p = nodes;
        LinkedNode pNext = p.next;
        while (pNext!=null){
            p.next = pNext.next;
            pNext.next = resultList.next;
            resultList.next = pNext;
            pNext=p.next;
        }
        return resultList.next;
    }


    public static void main(String[] args) {
        LinkedNode linkedNode1 = new LinkedNode(1, null);
        LinkedNode linkedNode2 = new LinkedNode(2, linkedNode1);
        LinkedNode linkedNode3 = new LinkedNode(3, linkedNode2);
        LinkedNode linkedNode4 = new LinkedNode(4, linkedNode3);

        LinkedNode reverse = new ReverseLinkedList().reverse(linkedNode4);
        while (reverse != null) {
            System.out.println(reverse.toString());
            reverse = reverse.next;
        }
    }

    static class LinkedNode {
        Integer val;
        LinkedNode next;

        public LinkedNode(Integer val, LinkedNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
            return "LinkedNode{" +
                    "val=" + val +
                    '}';
        }
    }
}
