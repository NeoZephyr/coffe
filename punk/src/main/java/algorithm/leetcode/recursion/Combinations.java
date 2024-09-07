package algorithm.leetcode.recursion;

import java.util.ArrayList;
import java.util.List;

public class Combinations {

    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> seq = new ArrayList<>();
        combine(n, k, 1, new ArrayList<>(), seq);
        return seq;
    }

    public void combine(int n, int k, int pos, List<Integer> path, List<List<Integer>> seq) {
        if (path.size() == k) {
            seq.add(new ArrayList<>(path));
            return;
        }

        // 剩下的数已经不足了
        if (pos > n - (k - path.size()) + 1) {
            return;
        }

        for (int i = pos; i <= n; i++) {
            path.add(i);
            combine(n, k, i + 1, path, seq);
            path.remove(path.size() - 1);
        }
    }

    public void dfs(int n, int k, int pos, List<Integer> path, List<List<Integer>> seq) {
        if (path.size() == k) {
            seq.add(new ArrayList<>(path));
            return;
        }

        if (pos > n - (k - path.size()) + 1) {
            return;
        }

        // 按照每一个数选与不选进行递归

        // 不选当前的数
        dfs(n, k, pos + 1, path, seq);

        // 选当前的数
        path.add(pos);
        dfs(n, k, pos + 1, path, seq);
        path.remove(path.size() - 1);
    }
}
