package learn.leetcode;

/**
 * 622. 设计循环队列
 * { @link https://leetcode.cn/problems/design-circular-queue/}
 *
 * 提示：
 * 所有的值都在 0 至 1000 的范围内；
 * 操作数将在 1 至 1000 的范围内；
 * 请不要使用内置的队列库。
 *
 * @author Zephyr
 * @since 2023-05-29
 */
public class Medium622 {
    public static void main(String[] args) {
        MyCircularQueue queue = new MyCircularQueue(3);
        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);
        queue.enQueue(4);
        int rear = queue.Rear();
        boolean full = queue.isFull();
        boolean deQueue = queue.deQueue();
        boolean enQueue = queue.enQueue(4);
        int rear1 = queue.Rear();
        System.out.println(rear);
    }


    static class MyCircularQueue {

        private int head = -1;
        private int tail = -1;
        private final int[] elements;

        /**
         * 循环队列是一种线性数据结构，其操作表现基于 FIFO（先进先出）原则并且队尾被连接在队首之后以形成一个循环。它也被称为“环形缓冲器”。
         * 每次出队和入队时更新head或tail的值; 队列中没有值时,head和tail应该 <0,表示不指向任何位置
         */
        public MyCircularQueue(int k) {
            elements = new int[k];
        }

        public boolean enQueue(int value) {
            if (isFull()) {
                return false;
            }
            tail = logicalNextIndex(tail);
            elements[tail] = value;
            // 针对队列中的元素为空的情况
            if (head < 0) {
                head = tail;
            }
            return true;
        }

        public boolean deQueue() {
            if (isEmpty()) {
                return false;
            }
            if (head == tail) {
                head = tail = -1;
            } else {
                head = logicalNextIndex(head);
            }
            return true;
        }

        /**
         * 从队首获取元素。如果队列为空，返回 -1 。
         */
        public int Front() {
            return isEmpty() ? -1 : elements[head];
        }

        /**
         * 获取队尾元素。如果队列为空，返回 -1
         */
        public int Rear() {
            return isEmpty() ? -1 : elements[tail];
        }

        public boolean isEmpty() {
            return head < 0 || tail < 0;
        }

        public boolean isFull() {
            return logicalNextIndex(tail) == head;
        }

        /**
         * 如果当前队列为空,则返回 0,表示从 0 开始进行数据操作
         * 如果队列不为空,则返回当前下标的下一个下标,尤其是,当前下标为最右侧的位置时,下一个下标位置应该为 0
         * 另一种写法: tail = (tail + 1) % size;
         */
        private int logicalNextIndex(int idx) {
            return (idx >= elements.length - 1 || idx < 0) ? 0 : idx + 1;
        }
    }

}
