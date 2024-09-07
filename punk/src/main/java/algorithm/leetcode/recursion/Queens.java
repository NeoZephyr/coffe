package algorithm.leetcode.recursion;

import java.util.*;

public class Queens {

    Set<Integer> rows;
    Set<Integer> cols;
    Set<Integer> lrs;
    Set<Integer> rls;
    int size;

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> ans = new ArrayList<>();
        List<String> path = new ArrayList<>();
        rows = new HashSet<>();
        cols = new HashSet<>();
        lrs = new HashSet<>();
        rls = new HashSet<>();
        size = n;
        dfs(ans, path, 0);
        return ans;
    }

    public void dfs(List<List<String>> ans, List<String> path, int level) {
        if (path.size() == size) {
            ans.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < size; i++) {
            if (!cols.contains(i) && !rows.contains(level) && !lrs.contains(level - i) && !rls.contains(level + i)) {
                path.add(genRow(size, i));
                cols.add(i);
                rows.add(level);
                lrs.add(level - i);
                rls.add(level + i);
                dfs(ans, path, level + 1);
                path.remove(path.size() - 1);
                cols.remove(i);
                rows.remove(level);
                lrs.remove(level - i);
                rls.remove(level + i);
            }
        }
    }

    public String genRow(int n, int idx) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            if (i != idx) {
                sb.append(".");
            } else {
                sb.append("Q");
            }
        }

        return sb.toString();
    }

    int n;

    // 状态压缩
    // 布尔数组可以只用一个整数来代替
    // int 类型整数等价于一个 32 位布尔数组，long 类型整数等价于一个 64 位布尔数组
    boolean[] col;
    boolean[] main;
    boolean[] sub;

    public List<List<String>> solveNQueens1(int n) {
        List<List<String>> ans = new ArrayList<>();
        this.n = n;
        this.col = new boolean[n];
        this.main = new boolean[2 * n - 1];
        this.sub = new boolean[2 * n - 1];
        Deque<Integer> path = new ArrayDeque<>();
        dfs(ans, path, 0);
        return ans;
    }

    public void dfs(List<List<String>> ans, Deque<Integer> path, int row) {
        if (path.size() == n) {
            List<String> board = convert2board(path);
            ans.add(board);
            return;
        }

        for (int i = 0; i < n; i++) {
            if (!col[i] && !main[row - i + n - 1] && !sub[row + i]) {
                path.addLast(i);
                col[i] = true;

                // 主对角线：横 - 纵的值固定
                // 副对角线：横 + 纵的值固定
                main[row - i + n - 1] = true;
                sub[row + i] = true;

                dfs(ans, path, row + 1);
                sub[row + i] = false;
                main[row - i + n - 1] = false;
                col[i] = false;
                path.removeLast();
            }
        }
    }

    List<String> convert2board(Deque<Integer> path) {
        List<String> board = new ArrayList<>();

        for (Integer num : path) {
            StringBuilder row = new StringBuilder();
            row.append(String.join("", Collections.nCopies(n, ".")));
            row.replace(num, num + 1, "Q");
            board.add(row.toString());
        }
        return board;
    }
}
