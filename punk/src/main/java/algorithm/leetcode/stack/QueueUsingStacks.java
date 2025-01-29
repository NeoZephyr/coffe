package algorithm.leetcode.stack;

import java.util.Stack;

public class QueueUsingStacks {

    Stack<Integer> in;
    Stack<Integer> out;

    public QueueUsingStacks() {
        in = new Stack<>();
        out = new Stack<>();
    }

    public void push(int x) {
        in.push(x);
    }

    public int pop() {
        inToOut();
        return out.pop();
    }

    public int peek() {
        inToOut();
        return out.peek();
    }

    public boolean empty() {
        return in.isEmpty() && out.isEmpty();
    }

    private void inToOut() {
        // 1. out 空了，才能倒数据
        // 2. 如果倒数据，in 必须倒完
        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
    }
}
