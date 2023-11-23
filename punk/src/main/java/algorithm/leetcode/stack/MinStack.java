package algorithm.leetcode.stack;

import java.util.Stack;

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
}
