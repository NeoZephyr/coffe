package algorithm.hw;

import java.util.*;

public class Test4 {

    public static void main(String[] args) {
        // test1();
        // test2();
        // test3();
        // test4();
        // test4();
        // test5();
        // test6();
        test7();
    }

    // 走迷宫
    private static void test1() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int m = in.nextInt();
            int n = in.nextInt();
            int[][] grid = new int[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    grid[i][j] = in.nextInt();
                }
            }

            ArrayList<List<Point>> ans = new ArrayList<>();
            ArrayList<Point> path = new ArrayList<>();
            boolean[][] visited = new boolean[m][n];
            visited[0][0] = true;
            path.add(new Point(0, 0));
            dfs(new Point(0, 0), m, n, grid, visited, path, ans);

            for (Point point : ans.get(0)) {
                System.out.printf("(%d,%d)\n", point.mm, point.nn);
            }
        }
    }

    // m, n 参数可以省略
    // 保证只有一条，ans 可以省略
    private static void dfs(Point point, int m, int n, int[][] grid, boolean[][] visited, List<Point> path, ArrayList<List<Point>> ans) {
        if (point.mm == m - 1 && point.nn == n - 1) {
            ans.add(new ArrayList<>(path));
            return;
        }

        if (!ans.isEmpty()) {
            return;
        }

        for (int[] d : dd) {
            int mm = point.mm + d[0];
            int nn = point.nn + d[1];

            if (mm < 0 || mm >= m || nn < 0 || nn >= n || grid[mm][nn] == 1 || visited[mm][nn]) {
                continue;
            }

            visited[mm][nn] = true;
            path.add(new Point(mm, nn));
            dfs(new Point(mm, nn), m, n, grid, visited, path, ans);
            path.remove(path.size() - 1);
            visited[mm][nn] = false;
        }
    }

    private static boolean dfs(Point point, int m, int n, int[][] grid, boolean[][] visited, List<Point> path) {
        if (point.mm == m - 1 && point.nn == n - 1) {
            return true;
        }

        for (int[] d : dd) {
            int mm = point.mm + d[0];
            int nn = point.nn + d[1];

            if (mm < 0 || mm >= m || nn < 0 || nn >= n || grid[mm][nn] == 1 || visited[mm][nn]) {
                continue;
            }

            visited[mm][nn] = true;
            path.add(new Point(mm, nn));

            if (dfs(new Point(mm, nn), m, n, grid, visited, path)) {
                return true;
            }

            path.remove(path.size() - 1);
            visited[mm][nn] = false;
        }

        return false;
    }

    static int[][] dd = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    static class Point {
        int mm;
        int nn;

        public Point(int mm, int nn) {
            this.mm = mm;
            this.nn = nn;
        }
    }

    private static void test2() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int m = in.nextInt();
            int n = in.nextInt();
            int[][] grid = new int[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    grid[i][j] = in.nextInt();
                }
            }

            ArrayList<Point> path = new ArrayList<>();
            boolean[][] visited = new boolean[m][n];
            visited[0][0] = true;
            path.add(new Point(0, 0));
            dfs(new Point(0, 0), m, n, grid, visited, path);

            for (Point point : path) {
                System.out.printf("(%d,%d)\n", point.mm, point.nn);
            }
        }
    }

    // 数独
    // 还可以广度遍历，在队列里面存储尾节点
    private static void test3() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int[][] grid = new int[9][9];
            List<Point> zeros = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    grid[i][j] = in.nextInt();

                    if (grid[i][j] == 0) {
                        zeros.add(new Point(i, j));
                    }
                }
            }

            dfs(grid, zeros, 0);

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    System.out.print(grid[i][j] + " ");
                }
                System.out.println();
            }
        }
    }

    private static boolean dfs(int[][] grid, List<Point> zeros, int pos) {
        if (pos >= zeros.size()) {
            return true;
        }

        Point point = zeros.get(pos);
        int m = point.mm;
        int n = point.nn;

        for (int i = 1; i <= 9; i++) {
            grid[m][n] = i;

            if (valid(grid, m, n)) {
                if (dfs(grid, zeros, pos + 1)) {
                    return true;
                }
            }

            grid[m][n] = 0;
        }

        return false;
    }

    private static boolean valid(int[][] grid, int i, int j) {
        // 可以不用 Set，毕竟之前的都是合法的，只需要比较新的值是否重复就可以了
        Set<Integer> set = new HashSet<>();

        for (int k = 0; k < grid[i].length; k++) {
            if (grid[i][k] != 0) {
                if (set.contains(grid[i][k])) {
                    return false;
                }

                set.add(grid[i][k]);
            }
        }

        set.clear();

        for (int k = 0; k < grid.length; k++) {
            if (grid[k][j] != 0) {
                if (set.contains(grid[k][j])) {
                    return false;
                }

                set.add(grid[k][j]);
            }
        }

        set.clear();

        int bi = i / 3 * 3;
        int bj = j / 3 * 3;

        for (int oi = bi; oi < bi + 3; oi++) {
            for (int oj = bj; oj < bj + 3; oj++) {
                if (grid[oi][oj] != 0) {
                    if (set.contains(grid[oi][oj])) {
                        return false;
                    }

                    set.add(grid[oi][oj]);
                }
            }
        }

        return true;
    }

    /**
     * 解析表达式
     * ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
     * System.out.println(scriptEngine.eval(input));
     *
     * 双栈法
     */
    private static void test4() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String line = in.nextLine();
            char[] ss = line.toCharArray();
            Stack<Integer> opt1 = new Stack<>();
            Stack<Character> opt2 = new Stack<>();
            boolean flag = false; // true 表示是运算符

            for (int i = 0; i < ss.length; i++){
                if (ss[i] == '(' || ss[i] == '[' || ss[i] == '{') {
                    opt2.push('('); // 统一换成小括号
                    continue;
                }

                if (ss[i] == ')' || ss[i] == ']' || ss[i] == '}') {
                    while (opt2.peek() != '(') {
                        compute(opt1, opt2); // 弹出开始计算直到遇到左括号
                    }
                    opt2.pop(); // 弹出左括号
                    continue;
                }

                if (flag) {
                    // ss[i] 优先级高于栈顶，就入栈
                    // ss[i] 优先级低于栈顶，就开始计算
                    while (!opt2.isEmpty() && !enter(opt2.peek(), ss[i])) {
                        compute(opt1, opt2);
                    }
                    opt2.push(ss[i]);
                    flag = false;
                    continue;
                }

                int start = i;
                while (++i < line.length() && Character.isDigit(ss[i]));
                opt1.push(Integer.parseInt(new String(ss, start, i - start)));
                flag = true;
                i--;
            }

            while (!opt2.isEmpty()) {
                compute(opt1, opt2);
            }

            System.out.println(opt1.pop());
        }
    }

    // opt2 是新的操作符
    private static boolean enter(Character top, Character next) {
        if (top == '(') {
            return true;
        }

        if ((top == '+' || top == '-') && (next == '*' || next == '/')) {
            return true;
        }

        return false;
    }

    private static void compute(Stack<Integer> s1, Stack<Character> s2) {
        int b = s1.pop();
        int a = s1.pop();
        Character opt = s2.pop();

        System.out.printf("%d %s %d\n", a, opt, b);

        if (opt == '+') {
            s1.push(a + b);
        } else if (opt == '-') {
            s1.push(a - b);
        } else if (opt == '*') {
            s1.push(a * b);
        } else {
            s1.push(a / b);
        }
    }

    // 逆波兰表达式
    // 9 + (3 - 1) * 3 + 10 / 2
    // 9 3 1 - 3 * + 10 2 / +
    private static void test5() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String line = in.nextLine();
            char[] ss = line.toCharArray();
            Stack<Integer> opt1 = new Stack<>();
            Stack<Character> opt2 = new Stack<>();
            boolean flag = false; // true 表示是运算符

            for (int i = 0; i < ss.length; i++) {
                if (ss[i] == '(' || ss[i] == '[' || ss[i] == '{') {
                    opt2.push('('); // 统一换成小括号
                    continue;
                }

                if (ss[i] == ')' || ss[i] == ']' || ss[i] == '}') {
                    while (opt2.peek() != '(') {
                        opt1.push(Integer.valueOf(opt2.pop()));
                    }
                    opt2.pop();
                    continue;
                }

                if (flag) {
                    while (!opt2.isEmpty() && !enter(opt2.peek(), ss[i])) {
                        opt1.push(Integer.valueOf(opt2.pop()));
                    }

                    opt2.push(ss[i]);
                    flag = false;
                    continue;
                }

                int start = i;
                while (++i < ss.length && Character.isDigit(ss[i]));
                opt1.push(Integer.parseInt(new String(ss, start, i - start)));
                i--;
                flag = true;
            }

            while (!opt2.isEmpty()) {
                opt1.push(Integer.valueOf(opt2.pop()));
            }

            LinkedList<Integer> list = new LinkedList<>();

            while (!opt1.isEmpty()) {
                list.addFirst(opt1.pop());
            }

            System.out.println(list);

            Stack<Integer> tmp = new Stack<>();

            for (int i = 0; i < list.size(); i++) {
                if (isOperator(list.get(i))) {
                    int b = tmp.pop();
                    int a = tmp.pop();
                    tmp.push(cal(a, b, list.get(i)));
                } else {
                    tmp.push(list.get(i));
                }
            }

            System.out.println(tmp.pop());
        }
    }

    private static boolean isOperator(Integer c) {
        return c == '-' || c == '*' || c == '/' || c == '+';
    }

    private static int cal(int a, int b, Integer opt) {
        if (opt == '+') {
            return a + b;
        } else if (opt == '-') {
            return a - b;
        } else if (opt == '*') {
            return a * b;
        } else {
            return a / b;
        }
    }

    /**
     * 放苹果分为两种情况，一种是有盘子为空，一种是每个盘子上都有苹果
     * f(m, n) 表示将 m 个苹果放入 n 个盘子中的摆放方法总数
     *
     * 1. 假设有一个盘子为空，则 f(m,n) 问题转化为将 m 个苹果放在 n-1 个盘子上，即求得 f(m, n-1) 即可
     * 2. 假设所有盘子都装有苹果，则每个盘子上至少有一个苹果，即最多剩下 m-n 个苹果，问题转化为将 m-n 个苹果放到 n 个盘子上，即求 f(m-n，n)
     *
     * 如果每个 m <= n
     * 则至少有 n - m 个盘子是空的
     */
    private static void test6() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int m = in.nextInt();
            int n = in.nextInt();
            int ans = dfs(m, n);
            System.out.println(ans);
        }
    }

    private static int dfs(int m, int n) {
        if (n == 0) {
            return 0;
        }

        if (m == 1 || n == 1 || m == 0) {
            return 1;
        }

        // 苹果小于盘子
        if (m < n) {
            return dfs(m, m);
        }

        return dfs(m, n - 1) + dfs(m - n, n);
    }

    // 24 点
    private static void test7() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int[] nums = new int[4];
            nums[0] = in.nextInt();
            nums[1] = in.nextInt();
            nums[2] = in.nextInt();
            nums[3] = in.nextInt();
            boolean[] visited = new boolean[4];
            List<String> path = new ArrayList<>();
            boolean ans = dfs(nums, visited, 0, path, "");
            System.out.println(ans);
        }
    }

    private static boolean dfs(int[] nums, boolean[] visited, int result, List<String> path, String item) {
        for (int i = 0; i < nums.length; i++) {
            if (!visited[i]) {
                visited[i] = true;
                path.add(item);

                if (dfs(nums, visited, result + nums[i], path, result + "+" + nums[i])
                        // || dfs(nums, visited, result - nums[i], path, result + "-" + nums[i])
                        || dfs(nums, visited, result * nums[i], path, result + "*" + nums[i])
                        || dfs(nums, visited, result / nums[i], path, result + "/" + nums[i]))
                    return true;

                path.remove(path.size() - 1);
                visited[i] = false;
            }
        }

        if (result == 24) {
            System.out.println(path);
            System.out.println(result);
        }

        return result == 24;
    }
}
