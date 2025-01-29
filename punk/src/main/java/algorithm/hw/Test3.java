package algorithm.hw;

import java.util.*;

public class Test3 {

    public static void main(String[] args) {
        // test1();
        // test2();
        // test3();
        // test4();
        // test5();
        test6();
    }

    // 素数伴侣
    private static void test1() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            List<Integer> evens = new ArrayList<>();
            List<Integer> odds = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                int num = in.nextInt();

                if (num % 2 == 0) {
                    evens.add(num);
                } else {
                    odds.add(num);
                }
            }

            int[] matches = new int[evens.size()]; // 记录偶数匹配的奇数
            int count = 0;

            for (Integer odd : odds) {
                boolean[] used = new boolean[evens.size()];

                if (find(odd, evens, matches, used)) {
                    count++;
                }
            }

            System.out.println(count);
        }
    }

    /**
     * A1 -> (B1, B2)
     * A2 -> (B1, B3)
     * A3 -> (B2)
     * <p>
     * 第一步
     * A1 -> B1
     * <p>
     * 第二步
     * A2 -> B1
     * A1 -> B2
     * <p>
     * 第三步
     * A3 -> B2
     * A1 -> B1
     * A2 -> B3
     */
    private static boolean find(int odd, List<Integer> evens, int[] matches, boolean[] used) {
        for (int i = 0; i < evens.size(); i++) {
            if (!isPrime(odd + evens.get(i))) {
                continue;
            }

            // 第 i 个偶数被用了
            if (used[i]) {
                continue;
            }

            used[i] = true;

            // 第 i 个偶数没有匹配过
            if (matches[i] == 0) {
                matches[i] = odd;
                return true;
            }

            if (find(matches[i], evens, matches, used)) {
                matches[i] = odd;
                return true;
            }
        }

        return false;
    }

    private static boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }

    // 匹配非字母的字符进行分割
    // s.split("[^A-Za-z]");
    // System.out.printf("%.6f\n", sum);

    // 称砝码
    private static void test2() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int[] g = new int[n];
            int[] c = new int[n];

            for (int i = 0; i < n; i++) {
                g[i] = in.nextInt();
            }

            for (int i = 0; i < n; i++) {
                c[i] = in.nextInt();
            }

            Set<Integer> set = new HashSet<>();
            dfs(g, c, 0, set);
            System.out.println(set.size() + 1);
        }
    }

    private static void dfs(int[] g, int[] c, int count, Set<Integer> set) {
        for (int i = 0; i < c.length; i++) {
            if (c[i] > 0) {
                count += g[i];
                c[i]--;
                set.add(count);
                dfs(g, c, count, set);
                count -= g[i];
                c[i]++;
            }
        }
    }

    private static void test3() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int[] g = new int[n];
            int[] c = new int[n];

            for (int i = 0; i < n; i++) {
                g[i] = in.nextInt();
            }

            for (int i = 0; i < n; i++) {
                c[i] = in.nextInt();
            }

            Set<Integer> set = new HashSet<>();
            set.add(0);

            for (int i = 0; i < n; i++) {
                List<Integer> list = new ArrayList<>(set);

                for (int j = 1; j <= c[i]; j++) {
                    for (int amount : list) {
                        set.add(amount + j * g[i]);
                    }
                }
            }

            System.out.println(set.size());
        }
    }

    private static void test4() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int[] g = new int[n];
            int[] c = new int[n];

            for (int i = 0; i < n; i++) {
                g[i] = in.nextInt();
            }

            for (int i = 0; i < n; i++) {
                c[i] = in.nextInt();
            }

            int amount = 0;

            for (int i = 0; i < n; i++) {
                amount += g[i] * c[i];
            }

            boolean[] dp = new boolean[amount + 1];
            dp[0] = true;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < c[i]; j++) {

                    // 正序的话会重复计算
                    for (int k = amount; k >= g[i]; k--) {
                        if (dp[k - g[i]]) {
                            dp[k] = true;
                        }
                    }
                }
            }

            int count = 0;

            for (boolean flag : dp) {
                if (flag) {
                    count++;
                }
            }

            System.out.println(count);
        }
    }

    private static void test5() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int[] g = new int[n];
            int[] c = new int[n];

            for (int i = 0; i < n; i++) {
                g[i] = in.nextInt();
            }

            for (int i = 0; i < n; i++) {
                c[i] = in.nextInt();
            }

            int amount = 0;
            List<Integer> ws = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                amount += g[i] * c[i];

                for (int j = 0; j < c[i]; j++) {
                    ws.add(g[i]);
                }
            }

            // dp[i][j] 表示使用前 i 个砝码，是否可以称出重量 j
            boolean[][] dp = new boolean[ws.size() + 1][amount + 1];
            dp[0][0] = true;


            // dp[i][j] = dp[i - 1][j] || dp[i - 1][j - w]
            //     1  2  3  4  5  6
            // 1   1  0  0  0  0  0
            // 2   1  1  1  0  0  0
            // 3   1  1  1  1  1  1
            for (int i = 1; i <= ws.size(); i++) {
                int w = ws.get(i - 1);
                dp[i][w] = true;

                for (int j = 0; j <= amount; j++) {
                    if (dp[i - 1][j]) {
                        dp[i][j] = true;
                        continue;
                    }

                    if (j > w && dp[i - 1][j - w]) {
                        dp[i][j] = true;
                    }
                }
            }

            int count = 0;

            for (int i = 0; i <= amount; i++) {
                if (dp[ws.size()][i]) {
                    count++;
                }
            }

            System.out.println(count);
        }
    }

    static String[] table1 = new String[]{
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine"
    };
    static String[] table2 = new String[]{
            "ten",
            "eleven",
            "twelve",
            "thirteen",
            "fourteen",
            "fifteen",
            "sixteen",
            "seventeen",
            "eighteen",
            "nineteen"
    };

    static String[] table3 = new String[]{
            "twenty",
            "thirty",
            "forty",
            "fifty",
            "sixty",
            "seventy",
            "eighty",
            "ninety"
    };

    private static void test6() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            System.out.println(spell(n));
        }
    }

    private static String spell(int n) {
        String s = "";

        int a = n / 1000000;

        if (a != 0) {
            s += spell(a) + " million ";
            n = n % 1000000;
        }

        a = n / 1000;

        if (a != 0) {
            s += spell(a) + " thousand ";
            n = n % 1000;
        }

        a = n / 100;
        boolean flag = false;

        if (a != 0) {
            s += table1[a - 1] + " hundred ";
            n = n % 100;
            flag = true;
        }

        if (n == 0) {
            return s.substring(0, s.length() - 1);
        }

        if (flag) {
            s += "and ";
        }

        if (n >= 10 && n <= 19) {
            s += table2[n - 10];
            return s;
        }

        a = n / 10;

        if (a != 0) {
            n = n % 10;

            if (n == 0) {
                s += table3[a - 2];
                return s;
            } else {
                s += table3[a - 2] + " ";
            }
        }

        s += table1[n - 1];

        return s;
    }

    public static String[] t1 = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    public static String[] t2 = new String[]{"ten", "eleven", "twelve", "thirteen", "forteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
    public static String[] t3 = new String[]{"zero", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

    public static int[] range = new int[]{(int) 1e2, (int) 1e3, (int) 1e6, (int) 1e9, (int) 1e12};
    public static String[] ranges = new String[]{"hundred", "thousand", "million", "billion"};

    public static String transfer(int num) {
        if (num <= 9) return t1[num];
        if (num <= 19) return t2[num % 10];
        if (num <= 99) return t3[num / 10] + (num % 10 == 0 ? "" : " " + t1[num % 10]);

        // 递归调用
        for (int i = 0; i < 4; i++) {
            if (num < range[i + 1]) {
                return transfer(num / range[i]) + " " + ranges[i] +
                        (num % range[i] == 0 ? " " : (i != 0 ? " " : " and ") + transfer(num % range[i]));
            }
        }
        return "";
    }
}
