package algorithm.leetcode.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangle {

    public static void main(String[] args) {
        Triangle triangle = new Triangle();

        List<List<Integer>> list = new ArrayList<>();
        list.add(Arrays.asList(2));
        list.add(Arrays.asList(3, 4));
        list.add(Arrays.asList(6, 5, 7));
        list.add(Arrays.asList(4, 1, 8, 3));

        System.out.println(triangle.minimumTotal2(list));

        list.clear();
        list.add(Arrays.asList(-10));
        System.out.println(triangle.minimumTotal2(list));
    }

    // [0][0] 到 [i][j] 的最小值
    public int minimumTotal(List<List<Integer>> triangle) {
        List<List<Integer>> dp = new ArrayList<>(triangle.size());
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < triangle.size(); i++) {
            List<Integer> line = triangle.get(i);
            List<Integer> dpLine = new ArrayList<>(line.size());
            min = Integer.MAX_VALUE;

            for (int j = 0; j < line.size(); j++) {
                if (i == 0) {
                    dpLine.add(line.get(j));
                    min = Math.min(min, line.get(j));
                } else {
                    if (j == 0) {
                        dpLine.add(line.get(j) + dp.get(i - 1).get(j));
                    } else if (j == line.size() - 1) {
                        dpLine.add(line.get(j) + dp.get(i - 1).get(j - 1));
                    } else {
                        dpLine.add(
                                Math.min(
                                        line.get(j) + dp.get(i - 1).get(j),
                                        line.get(j) + dp.get(i - 1).get(j - 1)));
                    }

                    min = Math.min(min, dpLine.get(j));
                }
            }

            dp.add(dpLine);
        }

        return min;
    }

    // 自下而上
    // [i][j] 到终点的最小值
    // dp[i][j] = min(f[i + 1][j], f[i][j + 1]) + a[i][j]
    public int minimumTotal1(List<List<Integer>> triangle) {
        int m = triangle.size();
        int n = triangle.get(m - 1).size();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = m - 1; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + triangle.get(i).get(j);
            }
        }

        return dp[0][0];
    }

    // 记忆化搜索
    public int minimumTotal2(List<List<Integer>> triangle) {
        int m = triangle.size();
        int n = triangle.get(m - 1).size();
        Integer[][] mem = new Integer[m][n];
        return dfs(triangle, 0, 0, mem);
    }

    private int dfs(List<List<Integer>> triangle, int i, int j, Integer[][] mem) {
        if (i >= triangle.size()) {
            return 0;
        }

        if (mem[i][j] != null) {
            return mem[i][j];
        }

        mem[i][j] = Math.min(dfs(triangle, i + 1, j, mem), dfs(triangle, i + 1, j + 1, mem)) + triangle.get(i).get(j);
        return mem[i][j];
    }
}
