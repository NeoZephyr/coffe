package algorithm.leetcode.recursion;

import java.util.ArrayList;
import java.util.List;

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
}
