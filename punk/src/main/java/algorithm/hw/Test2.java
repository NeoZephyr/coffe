package algorithm.hw;

import java.util.*;

public class Test2 {

    public static void main(String[] args) {
    }

    private static void test1() {
        Scanner in = new Scanner(System.in);

        // 可以将 nextLine 改为 next，后者不接受空格回车
        int n = in.nextInt();
        String[] array = new String[n];

        for (int i = 0; i < n; i++) {
            array[i] = in.next();
        }

        Arrays.sort(array);

        for (String str : array) {
            System.out.println(str);
        }
    }

    // 求 int 型正整数在内存中存储时 1 的个数
    // 方法一：Integer.toBinaryString(num);
    // 方法二：将 n 最右边的一个 1 变为 0
    // n &= (n-1);
    private static void test2() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int a = in.nextInt();
            int count = 0;

            while (a != 0) {
                count += (a & 1);
                a = a >>> 1;
            }

            System.out.println(count);
        }
    }

    /**
     * 0 1 背包
     * 剩余容量为 c，从前 i 个物品中得到的最大值
     * dfs(i, c) = max(dfs(i - 1, c), dfs(i - 1, c - w[i]) + v[i])
     *
     * 完全背包，选完之后可以继续选
     * dfs(i, c) = max(dfs(i - 1, c), dfs(i, c - w[i]) + v[i])
     *
     * 50 5
     * 20 3 5
     * 20 3 5
     * 10 3 0
     * 10 2 0
     * 10 1 0
     */
    private static void test3() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int m = in.nextInt();
            Goods[] gs = new Goods[m + 1];

            for (int i = 1; i < gs.length; i++) {
                gs[i] = new Goods();
            }

            for (int i = 1; i <= m; i++) {
                int v = in.nextInt();
                int w = in.nextInt();
                int no = in.nextInt();

                gs[i].value = v;
                gs[i].prefer =  v * w;

                if (no != 0) {
                    gs[i].isAttach = true;
                    gs[no].attaches.add(i);
                } else {
                    gs[i].isAttach = false;
                }
            }

            // dp[i][j] 表示前 i 件物品，在有 j 金额的时候，最大满意度
            // dp[i][j] 的取值选择以下最大：
            // dp[i - 1][j]
            // dp[i - 1][j - g[i].v] + g[i].p
            // dp[i - 1][j - g[i].v - a1.v] + g[i].p + a1.p
            // dp[i - 1][j - g[i].v - a2.v] + g[i].p + a2.p
            // dp[i - 1][j - g[i].v - a1.v - a2.v] + g[i].p + a1.p + a2.p
            int[][] dp = new int[m + 1][n + 1];

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    int max = dp[i - 1][j];

                    if (gs[i].isAttach) {
                        dp[i][j] = max;
                        continue;
                    }

                    if (j >= gs[i].value) {
                        max = Math.max(max, dp[i - 1][j - gs[i].value] + gs[i].prefer);
                    }

                    if (!gs[i].attaches.isEmpty()) {
                        int k = gs[i].attaches.get(0);

                        if (j >= gs[i].value + gs[k].value) {
                            max = Math.max(max, dp[i - 1][j - gs[i].value - gs[k].value] + gs[i].prefer + gs[k].prefer);
                        }
                    }

                    if (gs[i].attaches.size() >= 2) {
                        int k = gs[i].attaches.get(1);

                        if (j >= gs[i].value + gs[k].value) {
                            max = Math.max(max, dp[i - 1][j - gs[i].value - gs[k].value] + gs[i].prefer + gs[k].prefer);
                        }
                    }

                    if (gs[i].attaches.size() >= 2) {
                        int k1 = gs[i].attaches.get(0);
                        int k2 = gs[i].attaches.get(1);

                        if (j >= gs[i].value + gs[k1].value + gs[k2].value) {
                            max = Math.max(max, dp[i - 1][j - gs[i].value - gs[k1].value - gs[k2].value] + gs[i].prefer + gs[k1].prefer + gs[k2].prefer);
                        }
                    }

                    dp[i][j] = max;
                }
            }

            System.out.println(dp[m][n]);
        }
    }

    private static void test4() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int m = in.nextInt();
            Goods[] gs = new Goods[m + 1];

            for (int i = 1; i < gs.length; i++) {
                gs[i] = new Goods();
            }

            for (int i = 1; i <= m; i++) {
                int v = in.nextInt();
                int w = in.nextInt();
                int no = in.nextInt();

                gs[i].value = v;
                gs[i].prefer =  v * w;

                if (no != 0) {
                    gs[i].isAttach = true;
                    gs[no].attaches.add(i);
                } else {
                    gs[i].isAttach = false;
                }
            }

            int max = dfs(gs, m, n);
            System.out.println(max);
        }
    }

//50 5
//20 3 5
//20 3 5
//10 3 0
//10 2 0
//10 1 0

    private static int dfs(Goods[] gs, int pos, int total) {
        if (pos <= 0) {
            return 0;
        }

        if (gs[pos].isAttach) {
            return dfs(gs, pos - 1, total);
        }

        if (gs[pos].value > total) {
            return dfs(gs, pos - 1, total);
        }

        int max = dfs(gs, pos - 1, total);
        max = Math.max(max, dfs(gs, pos - 1, total - gs[pos].value) + gs[pos].prefer);

        for (int k : gs[pos].attaches) {
            max = Math.max(max, dfs(gs, pos - 1, total - gs[pos].value - gs[k].value) + gs[pos].prefer + gs[k].prefer);
        }

        if (gs[pos].attaches.size() >= 2) {
            int k1 = gs[pos].attaches.get(0);
            int k2 = gs[pos].attaches.get(1);
            max = Math.max(max, dfs(gs, pos - 1, total - gs[pos].value - gs[k1].value - gs[k2].value) + gs[pos].prefer + gs[k1].prefer + gs[k2].prefer);
        }

        return max;
    }

    static class Goods {
        int value;
        int prefer; // 满意度
        boolean isAttach;
        List<Integer> attaches = new ArrayList<>();

        public Goods() {}
    }

    private static void test5() {
        Scanner in = new Scanner(System.in);
        int aNum = 0;
        int bNum = 0;
        int cNum = 0;
        int dNum = 0;
        int eNum = 0;
        int errNum = 0;
        int pNum = 0;

        // 五类 IP 地址 ABCDE
        // "1.0.0.0"-"126.255.255.255"
        // "128.0.0.0"-"191.255.255.255"
        // "192.0.0.0"-"223.255.255.255"
        // "224.0.0.0"-"239.255.255.255"
        // "240.0.0.0"-"255.255.255.255"

        // 私有 IP 地址
        // "10.0.0.0"-"10.255.255.255"
        // "172.16.0.0"-"172.31.255.255"
        // "192.168.0.0"-"192.168.255.255"

        // "0.*.*.*" 和 "127.*.*.*" 的 IP 地址不属于上述输入的任意一类，也不属于不合法地址
        // 一个 IP 地址既可以是私有 IP 地址，也可以是五类 IP 地址之一，计数时请分别计入

        // 合法的子网掩码
        // 将 IP 地址转换为二进制后，必须由若干个连续的 1 后跟若干个连续的 0 组成
        // 全为 1 或全为 0 的子网掩码也是非法的
        while (in.hasNextLine()) {
            String str = in.nextLine();
            String[] items = str.split("~");
            int part1 = getIp(items[0], 0);

            if (part1 == 0 || part1 == 127) {
                continue;
            }

            if (isInvalidMask(items[1])) {
                errNum++;
                continue;
            }

            if (isInvalidIp(items[0])) {
                errNum++;
                continue;
            }

            if (part1 >= 1 && part1 <= 126) {
                aNum++;
            }

            if (part1 >= 128 && part1 <= 191) {
                bNum++;
            }

            if (part1 >= 192 && part1 <= 223) {
                cNum++;
            }

            if (part1 >= 224 && part1 <= 239) {
                dNum++;
            }

            if (part1 >= 240 && part1 <= 255) {
                eNum++;
            }

            int part2 = getIp(items[0], 1);

            if (part1 == 10 || (part1 == 172 && part2 >= 16 && part2 <=31) || (part1 == 192 && part2 == 168)) {
                pNum++;
            }
        }

        System.out.println(aNum + " " + bNum + " " + cNum + " " + dNum + " " + eNum + " " + errNum + " " + pNum);
    }

    public static int getIp(String ip, int index) {
        String[] items = ip.split("\\.");
        return Integer.parseInt(items[index]);
    }

    public static boolean isInvalidMask(String mask) {
        String[] items = mask.split("\\.");

        if (items.length != 4) {
            return true;
        }
        String maskBinary = toBinary(items[0]) + toBinary(items[1]) + toBinary(items[2]) + toBinary(items[3]);

        return !maskBinary.matches("[1]+[0]+");
    }

    public static String toBinary(String num) {
        String numBinary = Integer.toBinaryString(Integer.parseInt(num));

        while (numBinary.length() < 8) {
            numBinary = "0" + numBinary;
        }
        return numBinary;
    }

    public static boolean isInvalidIp(String ip) {
        String[] items = ip.split("\\.");

        if (items.length != 4) {
            return true;
        }

        return Integer.parseInt(items[0]) > 255 || Integer.parseInt(items[1]) > 255 || Integer.parseInt(items[2]) > 255 || Integer.parseInt(items[3]) > 255;
    }

    // 合唱队
    private static void test6() {
        // 186 186 150 200 160 130 197 200
        // 1   1   1   2   2    1  3   4
        // 3   3   4   1   3    3   2   1
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            int[] heights = new int[n];

            for (int i = 0; i < n; i++) {
                heights[i] = in.nextInt();
            }

            // dp1[i] 表示从左到右，以 i 位置结束的最长递增子序列长度
            int[] dp1 = new int[n];

            // dp2[i] 表示从右到左，以 i 位置结束的最长递增子序列长度
            int[] dp2 = new int[n];

            for (int i = 0; i < n; i++) {
                int max = dp1[i];

                for (int j = 0; j < i; j++) {
                    if (heights[i] > heights[j]) {
                        max = Math.max(max, dp1[j] + 1);
                    }
                }

                dp1[i] = max;
            }

            for (int i = n - 1; i >= 0; i--) {
                int max = dp2[i];

                for (int j = n - 1; j > i; j--) {
                    if (heights[i] > heights[j]) {
                        max = Math.max(max, dp2[j] + 1);
                    }
                }

                dp2[i] = max;
            }

            int max = Integer.MIN_VALUE;

            for (int i = 0; i < n; i++) {
                max = Math.max(max, dp1[i] + dp2[i]);
            }

            System.out.println(n - (max + 1));
        }
    }
}
