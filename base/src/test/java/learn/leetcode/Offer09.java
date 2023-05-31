package learn.leetcode;

import java.util.Stack;

/**
 * 剑指 Offer 09. 用两个栈实现队列
 *
 * @author Zephyr
 * @date 2022/4/10.
 */
public class Offer09 {

    public static void main(String[] args) {
        CQueueByMe cQueue = new CQueueByMe();
        System.out.println(cQueue.deleteHead());
        cQueue.appendTail(1);
        cQueue.appendTail(2);
        System.out.println(cQueue.deleteHead());
        System.out.println(cQueue.deleteHead());
        //cQueue.appendTail(3);
        //cQueue.appendTail(4);
        //cQueue.appendTail(5);
        //cQueue.appendTail(6);

    }

    /**
     * 优化解法：
     */
    static class CQueue {
        Stack<Integer> head = new Stack<>();
        Stack<Integer> tail = new Stack<>();

        public CQueue() {

        }

        public void appendTail(int value) {
            tail.push(value);
        }

        public int deleteHead() {
            if (!head.isEmpty()) {
                return head.pop();
            }
            while (!tail.isEmpty()) {
                head.push(tail.pop());
            }
            return head.isEmpty() ? -1 : head.pop();
        }
    }

    /**
     * 自己想到的版本：栈tail专门用来向队列中添加元素，head栈专门用来提供队列的消费功能
     */
    static class CQueueByMe {
        boolean usingTail = true;
        Stack<Integer> head = new Stack<>();
        Stack<Integer> tail = new Stack<>();

        public CQueueByMe() {

        }

        public void appendTail(int value) {
            if (!usingTail) {
                reverse();
            }
            usingTail = true;
            tail.add(value);
        }

        public int deleteHead() {
            if (usingTail) {
                reverse();
            }
            usingTail = false;
            if (head.empty()) {
                return -1;
            }
            return head.pop();
        }

        private void reverse() {
            if (usingTail) {
                while (!tail.empty()) {
                    head.push(tail.pop());
                }
            } else {
                while (!head.empty()) {
                    tail.push(head.pop());
                }
            }
        }
    }
}
