package algorithm.leetcode.dp;

import java.util.Arrays;

/**
 * https://leetcode.cn/problems/longest-common-subsequence/description/
 */
public class LongestCommonSubsequence {

    public static void main(String[] args) {
        LongestCommonSubsequence instance = new LongestCommonSubsequence();
        System.out.println(instance.longestCommonSubsequence3("abcde", "ace"));
        System.out.println(instance.longestCommonSubsequence3("abc", "abc"));
        System.out.println(instance.longestCommonSubsequence3("abc", "def"));
        System.out.println(instance.longestCommonSubsequence3("bsbininm", "jmjkbkjkv"));
        System.out.println(instance.longestCommonSubsequence3("ezupkr", "ubmrapg"));
    }

    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m][n];

        if (text1.charAt(0) == text2.charAt(0)) {
            dp[0][0] = 1;
        }

        for (int i = 1; i < n; i++) {
            dp[0][i] = Math.max(text1.charAt(0) == text2.charAt(i) ? 1 : 0, dp[0][i - 1]);
        }

        for (int i = 1; i < m; i++) {
            dp[i][0] = Math.max(text1.charAt(i) == text2.charAt(0) ? 1 : 0, dp[i - 1][0]);
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                int delta = text1.charAt(i) == text2.charAt(j) ? 1 : 0;
                dp[i][j] = Math.max(Math.max(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1] + delta);
            }
        }

        return dp[m - 1][n - 1];
    }

    public int longestCommonSubsequence1(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();

        // dp 中第 0 行，0 列存储的是初始值
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp[i + 1][j + 1] = text1.charAt(i) == text2.charAt(j) ? dp[i][j] + 1 : Math.max(dp[i][j + 1], dp[i + 1][j]);
            }
        }

        return dp[m][n];
    }

    // 空间优化：两个数组
    public int longestCommonSubsequence2(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();

        int[] dp1 = new int[n + 1];
        int[] dp2 = new int[n + 1];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp2[j + 1] = text1.charAt(i) == text2.charAt(j) ? dp1[j] + 1 : Math.max(dp1[j + 1], dp2[j]);
            }

            dp1 = dp2;
        }

        return dp2[n];
    }

    public int longestCommonSubsequence3(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] mem = new int[m][n];

        for (int[] row : mem) {
            Arrays.fill(row, - 1);
        }

        return dfs(text1, text2, mem, m - 1, n - 1);
    }

    private int dfs(String s1, String s2, int[][] mem, int i, int j) {
        if (i < 0 || j < 0) {
            return 0;
        }

        // 已经计算过
        if (mem[i][j] != -1) {
            return mem[i][j];
        }

        if (s1.charAt(i) == s2.charAt(j)) {
            mem[i][j] = 1 + dfs(s1, s2, mem, i - 1, j - 1);
        } else {
            mem[i][j] = Math.max(
                    dfs(s1, s2, mem, i - 1, j),
                    dfs(s1, s2, mem, i, j - 1));
        }

        return mem[i][j];
    }
}
