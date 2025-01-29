package algorithm.hw;

import java.util.*;

public class Test5 {

    public static void main(String[] args) {
        // test1();
        System.out.printf("%08.3f\n", 3.14);
        System.out.printf("%-4d\n", 44);
    }

    // 火车出站入站
    private static void test1() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            String[] ss = new String[n + 1];

            for (int i = 0; i < n; i++) {
                ss[i + 1] = String.valueOf(in.nextInt());
            }

            List<String> ans = new ArrayList<>();
            List<String> path = new ArrayList<>();
            Stack<String> s = new Stack<>();
            dfs(ss, 1, n, s, path, ans);
            Collections.sort(ans);
            for (String l : ans) {
                System.out.println(l);
            }
        }
    }

    private static void dfs(String[] ss, int level, int n, Stack<String> s, List<String> path, List<String> ans) {
        if (level > n) {
            Stack<String> tmp = new Stack<>();

            while (!s.isEmpty()) {
                String i = s.pop();
                path.add(i);
                tmp.push(i);
            }

            ans.add(String.join(" ", path));

            while (!tmp.isEmpty()) { // 恢复
                String i = tmp.pop();
                s.push(i);
                path.remove(path.size() - 1);
            }
            return;
        }

        s.push(ss[level]);
        dfs(ss, level + 1, n, s, path, ans);
        s.pop(); // 恢复

        if (!s.isEmpty()) {
            path.add(s.pop());
            dfs(ss, level, n, s, path, ans);
            String i = path.remove(path.size() - 1);
            s.push(i); // 恢复
        }
    }

    // 埃及分数 a < b
    // a / b = a / (a * p + r)
    // b = a * p + r
    // a / (a * p + r)
    // = (a * p + r - r + a) / (a * p + r)(p + 1)
    // = 1 / (p + 1) + (a - r) / (a * p + r)(p + 1)

    private static void test2() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            List<Integer> nums = new ArrayList<>();
            int sum5 = 0;
            int sum3 = 0;
            int sum = 0;

            for (int i = 0; i < n; i++) {
                int k = in.nextInt();

                if (k % 5 == 0) {
                    sum5 += k;
                } else if (k % 3 == 0) {
                    sum3 += k;
                } else {
                    nums.add(k);
                }

                sum += k;
            }

            if (sum % 2 != 0) {
                System.out.println("false");
                return;
            }

            int target = sum / 2 - sum3;
            boolean ans = dfs(nums, 0, target);
            System.out.println(ans ? "true" : "false");
        }
    }

    private static boolean dfs(List<Integer> nums, int pos, int target) {
        if (target == 0) {
            return true;
        }

        if (pos == nums.size()) {
            return false;
        }

        return dfs(nums, pos + 1, target) || dfs(nums, pos + 1, target - nums.get(pos));
    }

    // String.format("%.1f", average)
}
