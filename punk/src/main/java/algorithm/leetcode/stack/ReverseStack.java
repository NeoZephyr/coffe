package algorithm.leetcode.stack;

import java.util.Stack;

/**
 * 用递归函数逆序一个栈
 */
public class ReverseStack {

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        ReverseStack reverseStack = new ReverseStack();
        reverseStack.reverse(stack);

        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }

    public void reverse(Stack<Integer> stack) {
        if (stack.isEmpty()) {
            return;
        }

        int last = removeLast(stack);
        reverse(stack);
        stack.push(last);
    }

    public int removeLast(Stack<Integer> stack) {
        int elem = stack.pop();

        if (stack.isEmpty()) {
            return elem;
        }

        int last = removeLast(stack);
        stack.push(elem);
        return last;
    }
}
