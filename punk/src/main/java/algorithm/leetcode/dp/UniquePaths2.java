package algorithm.leetcode.dp;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class UniquePaths2 {

    public static void main(String[] args) {

        int[][] grid1 = new int[][]{
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };
        int[][] grid2 = new int[][]{
                {0, 1},
                {0, 0}
        };
        int[][] grid3 = new int[][]{
                {1, 0}
        };
        System.out.println(new UniquePaths2().uniquePathsWithObstacles(grid1));
        System.out.println(new UniquePaths2().uniquePathsWithObstacles(grid2));
        System.out.println(new UniquePaths2().uniquePathsWithObstacles(grid3));
    }

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        int[] f = new int[n];

        for (int i = 0; i < n; i++) {
            if (obstacleGrid[0][i] == 1) {
                break;
            }

            f[i] = 1;
        }

        boolean stop = f[0] == 0;

        for (int i = 1; i < m; i++) {
            if (stop || obstacleGrid[i][0] == 1) {
                stop = true;
                f[0] = 0;
            } else {
                f[0] = 1;
            }

            for (int j = 1; j < n; j++) {
                f[j] = obstacleGrid[i][j] == 1 ? 0 : f[j - 1] + f[j];
            }
        }

        return f[n - 1];
    }

    public int uniquePathsWithObstacles1(int[][] obstacleGrid) {
        int n = obstacleGrid.length;
        int m = obstacleGrid[0].length;
        int[] dp = new int[m];

        // 起点设置初始值
        dp[0] = (obstacleGrid[0][0] == 1) ? 0 : 1;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                // 有障碍物的格子直接赋 0
                if (obstacleGrid[i][j] == 1) {
                    dp[j] = 0;
                }
                // 否则 dp[j] 的值由左方和上一次迭代的 dp[j] 累加而来
                else if (obstacleGrid[i][j] == 0 && j - 1 >= 0) {
                    dp[j] = dp[j] + dp[j - 1];
                }
            }
        }
        return dp[m - 1];
    }

    public int uniquePathsWithObstacles2(int[][] obstacleGrid) {
        return dfs(new HashMap<Pair, Integer>(), obstacleGrid, 0, 0);
    }

    private int dfs(Map<Pair, Integer> cache, int[][] arr, int i, int j) {
        Pair p = new Pair(i, j);
        if (cache.containsKey(p)) {
            return cache.get(p);
        }
        // 边界/障碍物检查
        if (i >= arr.length || j >= arr[0].length || arr[i][j] == 1) {
            return 0;
        }
        // 达到终点了
        if (i == arr.length - 1 && j == arr[0].length - 1) {
            return 1;
        }
        // 继续往右(i,j+1)、往下(i+1,j)递归调用
        int res = dfs(cache, arr, i + 1, j) + dfs(cache, arr, i, j + 1);
        cache.put(p, res);
        return res;
    }
}
