package algorithm.leetcode.dp;

import java.util.Arrays;

public class UniquePaths {

    public static void main(String[] args) {
        System.out.println(new UniquePaths().uniquePaths(3, 7));
        System.out.println(new UniquePaths().uniquePaths(3, 2));
        System.out.println(new UniquePaths().uniquePaths(3, 3));
    }

    public int uniquePaths(int m, int n) {
        int[][] paths = new int[m][];

        for (int i = 0; i < m; i++) {
            paths[i] = new int[n];
        }

        int row = m - 1;
        int col = n - 1;

        while (row >= 0 && col >= 0) {
            if (col + 1 > n - 1 && row + 1 > m - 1) {
                paths[row][col] = 1;
            } else if (col + 1 > n - 1) {
                paths[row][col] = paths[row + 1][col];
            } else if (row + 1 > m - 1) {
                paths[row][col] = paths[row][col + 1];
            } else {
                paths[row][col] = paths[row + 1][col] + paths[row][col + 1];
            }

            for (int i = row - 1; i >= 0; i--) {
                if (col + 1 > n - 1) {
                    paths[i][col] = paths[i + 1][col];
                } else {
                    paths[i][col] = paths[i + 1][col] + paths[i][col + 1];
                }
            }

            for (int i = col - 1; i >= 0; i--) {
                if (row + 1 > m - 1) {
                    paths[row][i] = paths[row][i + 1];
                } else {
                    paths[row][i] = paths[row][i + 1] + paths[row + 1][i];
                }
            }

            row--;
            col--;
        }

        return paths[0][0];
    }

    // 正向思考
    // 正向：从 start 到 <i, j> 的不同路径
    // 反向：从 <i, j> 到 end 的不同路径
    public int uniquePaths1(int m, int n) {
        int[][] f = new int[m][n];

        // 第一行，第一列都是 1
        for (int i = 0; i < m; ++i) {
            f[i][0] = 1;
        }
        for (int j = 0; j < n; ++j) {
            f[0][j] = 1;
        }

        for (int i = 1; i < m; ++i) {
            for (int j = 1; j < n; ++j) {
                f[i][j] = f[i - 1][j] + f[i][j - 1];
            }
        }
        return f[m - 1][n - 1];
    }

    // 空间复杂度优化
    // 由于 f(i,j) 仅与第 i 行和第 i−1 行的状态有关，因此我们可以使用滚动数组代替代码中的二维数组
    public int uniquePaths2(int m, int n) {
        int[] f = new int[n];
        Arrays.fill(f, 1);

        for (int i = 1; i < m; ++i) {
            for (int j = 1; j < n; ++j) {
                f[j] += f[j - 1];
            }
        }
        return f[n - 1];
    }
}
