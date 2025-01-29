package algorithm.leetcode.dp;

public class LongestPalindromicSubstring {

    public static void main(String[] args) {
        LongestPalindromicSubstring instance = new LongestPalindromicSubstring();
        System.out.println(instance.longestPalindrome1("babad"));
    }

    // 中心扩散法
    public String longestPalindrome(String s) {
        int max = Integer.MIN_VALUE;
        String ss = "";

        for (int i = 0; i < s.length(); i++) {
            int left = i - 1;
            int right = i + 1;
            int len = 1;

            while (left >= 0 && s.charAt(left) == s.charAt(i)) {
                left--;
                len++;
            }

            while (right < s.length() && s.charAt(right) == s.charAt(i)) {
                right++;
                len++;
            }

            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
                len += 2;
            }

            if (len > max) {
                max = len;
                ss = s.substring(left + 1, right);
            }
        }

        return ss;
    }

    /**
     * 用一个 boolean dp[i][j] 表示字符串从 i 到 j 这段是否位回文
     *
     * 初始状态，i = j 时， dp[i][j] = true
     * 如果 dp[i][j] = true，要判断 dp[i - 1][j + 1] 是否位回文，只需要判断字符串在 i - 1 和 j + 1 两个位置是否为相同的字符
     *
     * a  b  c  a
     * a  a  a  a
     * a  a  a  a
     * a  a  a  a
     *
     * f(i, j) = f(i + 1, j - 1) & S[i] == S[j]
     * 边界：i == j     长度为 1
     * 边界：i == j - 1 长度为 2
     */
    public String longestPalindrome1(String s) {
        boolean[][] dp = new boolean[s.length()][s.length()];
        int max = Integer.MIN_VALUE;
        String ss = "";

        // ABBA
        for (int i = s.length() - 1; i >= 0; i--) {
            for (int j = i; j < s.length(); j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || dp[i + 1][j - 1])) {
                    dp[i][j] = true;

                    if (j - i + 1 > max) {
                        max = j - i + 1;
                        ss = s.substring(i, j + 1);
                    }
                }
            }
        }

        return ss;
    }
}
