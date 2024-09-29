package algorithm.leetcode.recursion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GenerateParentheses {

    public static void main(String[] args) {
        System.out.println(new GenerateParentheses().generateParenthesis(3));;
    }

    public List<String> generateParenthesis(int n) {
        List<String> seq = new ArrayList<>();
        gen(n, n, "", seq);
        return seq;
    }

    private void gen(int left, int right, String s, List<String> seq) {
        if (left == 0 && right == 0) {
            seq.add(s);
            return;
        }

        // 条件判断，进行剪枝
        if (left > 0) {
            gen(left - 1, right, s + "(", seq);
        }

        if (right > left) {
            gen(left, right - 1, s + ")", seq);
        }

        // 这里隐藏退出了
    }

    // 可以用 Node 结构记录 left right
    public List<String> generateParenthesis1(int n) {
        List<String> seq = new ArrayList<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("");

        while (!queue.isEmpty()) {
            int sz = queue.size();

            for (int i = 0; i < sz; i++) {
                String s = queue.poll();
                int left = 0;
                int right = 0;

                if (s.length() == 2 * n) {
                    seq.add(s);
                    continue;
                }

                for (int j = 0; j < s.length(); j++) {
                    if (s.charAt(j) == '(') {
                        left++;
                    } else {
                        right++;
                    }
                }

                if (left > right) {
                    queue.offer(s + ')');
                }

                if (left < n) {
                    queue.offer(s + '(');
                }
            }
        }

        return seq;
    }
}
