package algorithm.leetcode.stack;

import java.util.Stack;
import java.util.concurrent.Executors;

/**
 * https://leetcode.cn/problems/sort-of-stacks-lcci/description/
 */
public class SortedStack {

    Stack<Integer> s1 = new Stack<>();
    Stack<Integer> s2 = new Stack<>();

    // 对栈进行排序使最小元素位于栈顶

    public SortedStack() {
    }

    public void push(int val) {
        if (s1.isEmpty()) {
            s1.push(val);
            return;
        }

        while (!s1.isEmpty() && s1.peek() < val) {
            s2.push(s1.pop());
        }

        s1.push(val);

        while (!s2.isEmpty()) {
            s1.push(s2.pop());
        }
    }

    public void pop() {
        if (!s1.isEmpty()) {
            s1.pop();
        }
    }

    public int peek() {
        if (s1.isEmpty()) {
            return -1;
        }

        return s1.peek();
    }

    public boolean isEmpty() {
        return s1.isEmpty();
    }

    static class SortedStack1 {
        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();

        // 对栈进行排序使最小元素位于栈顶

        public SortedStack1() {
        }

        public void push(int val) {
            int max = s1.isEmpty() ? Integer.MAX_VALUE : s1.peek();
            int min = s2.isEmpty() ? Integer.MIN_VALUE : s2.peek();

            // 比较原始栈与辅助栈栈顶值，使其满足 辅助栈 <= val <= 原始栈
            while (true) {
                if (val > max) {
                    s2.push(s1.pop());
                    max = s1.isEmpty() ? Integer.MAX_VALUE : s1.peek();
                } else if (val < min) {
                    s1.push(s2.pop());
                    min = s2.isEmpty() ? Integer.MIN_VALUE : s2.peek();
                } else {
                    s1.push(val);
                    break;
                }
            }
        }

        public void pop() {
            // 将辅助栈元素 push 回原始栈
            while (!s2.isEmpty()) {
                s1.push(s2.pop());
            }
            if (!s1.isEmpty())
                s1.pop();
        }

        public int peek() {
            // 将辅助栈元素 push 回原始栈
            while (!s2.isEmpty()) {
                s1.push(s2.pop());
            }
            return s1.isEmpty() ? -1 : s1.peek();
        }

        public boolean isEmpty() {
            return s1.isEmpty() && s2.isEmpty();
        }
    }

    static class SortedStack2 {
        Stack<Integer> data = new Stack<>();

        // 对栈进行排序使最小元素位于栈顶

        public SortedStack2() {
        }

        public void push(int val) {
            if (data.isEmpty() || val < data.peek()) {
                data.push(val);
                return;
            }

            int top = data.pop();
            push(val);
            data.push(top);
        }

        public void pop() {
            if (!data.isEmpty()) {
                data.pop();
            }
        }

        public int peek() {
            if (data.isEmpty()) {
                return -1;
            }

            return data.peek();
        }

        public boolean isEmpty() {
            return data.isEmpty();
        }
    }
}
