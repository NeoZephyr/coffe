package algorithm.leetcode.stack;

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

        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = new HashMap<Character, Character>() {{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);

            if (!pairs.containsKey(c)) { // 左括号就压入
                stack.push(c);
            } else if (stack.isEmpty() || pairs.get(c) != stack.pop()) { // 右括号检查
                return false;
            }
        }

        return stack.isEmpty(); // 是否完全匹配
    }
}