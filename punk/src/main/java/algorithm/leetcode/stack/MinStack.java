package algorithm.leetcode.stack;

import java.util.Stack;

/**
 * https://leetcode.cn/problems/min-stack/description/
 */
public class MinStack {

    private Stack<Integer> stack;
    private Stack<Integer> minStack;

    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }

    public void push(int val) {
        stack.push(val);

        if (minStack.isEmpty()) {
            minStack.push(val);
            return;
        }

        int min = minStack.peek();

        if (val <= min) {
            minStack.push(val);
        }
    }

    public void pop() {
        Integer val = stack.pop();
        Integer min = minStack.peek();

        if (val.equals(min)) {
            minStack.pop();
        }
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }

    static class MinStack1 {

        private Stack<Integer> data;
        private Stack<Integer> min;

        public MinStack1() {
            this.data = new Stack<>();
            this.min = new Stack<>();
        }

        void push(int val) {
            data.push(val);

            if (min.isEmpty() || val < min.peek()) {
                min.push(val);
            } else {
                min.push(min.peek());
            }
        }

        void pop() {
            data.pop();
            min.pop();
        }

        int top() {
            return data.peek();
        }

        int getMin() {
            return min.peek();
        }
    }
}
