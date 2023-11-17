package algorithm.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ValidParentheses {

    public boolean isValid(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        int n = s.length();

        if (n % 2 == 1) {
            return false;
        }

        Map<Character, Character> pairs = new HashMap<Character, Character>() {{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};

        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if ((c == '(') || (c == '[') || (c == '{')) {
                stack.push(c);
                continue;
            }

            if (stack.isEmpty()) {
                return false;
            }

            if (c != pairs.get(stack.pop())) {
                return false;
            }
        }

        return stack.isEmpty();
    }

    public static void main(String[] args) {
        new ValidParentheses().isValid("()");
    }
}
