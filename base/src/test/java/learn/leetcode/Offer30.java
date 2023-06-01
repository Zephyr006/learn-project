package learn.leetcode;

import java.util.Stack;

/**
 * 剑指 Offer 30. 包含min函数的栈
 *
 * @author Zephyr
 * @date 2022/4/10.
 */
public class Offer30 {

    public static void main(String[] args) {
        //["MinStack","push","push","push","top","pop","min","pop","min","pop","push","top","min","push","top","min","pop","min"]
        //[[],[2147483646],[2147483646],[2147483647],[],[],[],[],[],[],[2147483647],[],[],[-2147483648],[],[],[],[]]
        MinStack stack = new MinStack();
        stack.push(2147483646);
        stack.push(2147483646);
        stack.push(2147483647);
        stack.top();
        stack.pop();
        stack.min();
        stack.pop();
    }

    static class MinStack {
        Stack<Integer> xStack = new Stack<>();
        Stack<Integer> minStack = new Stack<>();
        int min = Integer.MAX_VALUE;

        /** initialize your data structure here. */
        public MinStack() {

        }

        public void push(int x) {
            min = Math.min(min, x);
            xStack.push(x);
            minStack.push(min);
        }

        public void pop() {
            xStack.pop();
            minStack.pop();
            min = minStack.isEmpty() ? Integer.MAX_VALUE : minStack.peek();
        }

        public int top() {
            return xStack.peek();
        }

        public int min() {
            return minStack.peek();
        }
    }
}
