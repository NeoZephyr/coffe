package algorithm.hw;

import java.util.*;

public class Test6 {

    // https://hr.nowcoder.com/v1/s/wFMp5DOX#
    public static void main(String[] args) {
        // System.out.println("Hello");
        // test1();
        // test2();
        test2();
        // System.out.println(replace1("ASSSAWAA"));
        // test4();
    }

    // 评委给学员打分
    private static void test1() {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNext()) { // 注意 while 处理多个 case
            String s = in.nextLine();
            String[] items = s.split(",");
            int m = Integer.parseInt(items[0]);
            int n = Integer.parseInt(items[1]);
            Stu[] stus = new Stu[n];
            boolean invalid = false;

            if (n < 3 || n > 100 || m < 3 || m > 10) {
                invalid = true;
            }

            for (int i = 0; i < stus.length; i++) {
                stus[i] = new Stu(i + 1, 0);
            }

            for (int i = 0; i < m; i++) {
                s = in.nextLine();
                items = s.split(",");

                for (int j = 0; j < n; j++) {
                    int c = Integer.parseInt(items[j]);

                    if (c < 1 || c > 11) {
                        invalid = true;
                    } else {
                        stus[j].score += c;
                        stus[j].scores[c]++;
                    }
                }
            }

            if (invalid) {
                System.out.println("-1");
            } else {
                Arrays.sort(stus, new Comparator<Stu>() {
                    @Override
                    public int compare(Stu o1, Stu o2) {
                        if (o2.score != o1.score) {
                            return o2.score - o1.score;
                        }

                        for (int i = 10; i >= 1; i--) {
                            if (o2.scores[i] != o1.scores[i]) {
                                return o2.scores[i] - o1.scores[i];
                            }
                        }

                        return o1.no - o2.no;
                    }
                });
                // System.out.println(Arrays.toString(stus));
                System.out.printf("%d,%d,%d\n", stus[0].no, stus[1].no, stus[2].no);
            }
        }
    }

    static class Stu {
        int no;
        int score;

        int[] scores = new int[11];

        public Stu(int no, int score) {
            this.no = no;
            this.score = score;
        }

        @Override
        public String toString() {
            return "Stu{" +
                    "no=" + no +
                    ", score=" + score +
                    '}';
        }
    }

    // 完美走位
    private static void test2() {
        Scanner in = new Scanner(System.in);

        // ASDW
        // A -> (-1, 0)
        // S -> (0, -1)
        // D -> (1, 0)
        // W -> (0, 1)
        // AASW
        // AAAA    3
        // AASS
        // 注意 hasNext 和 hasNextLine 的区别

        while (in.hasNext()) {
            String str = in.nextLine();
            char[] cmds = str.toCharArray();
            int sum = str.length();
            int a = 0;
            int s = 0;
            int d = 0;
            int w = 0;

            for (char cmd : cmds) {
                if (cmd == 'A') {
                    a++;
                } else if (cmd == 'S') {
                    s++;
                } else if (cmd == 'D') {
                    d++;
                } else {
                    w++;
                }
            }

            int avg = sum / 4;

            if ((a == avg) && (s == avg) && (d == avg) && (w == avg)) {
                System.out.println(0);
                continue;
            }

            Set<Character> set = new HashSet<>();
            Map<Character, Integer> differ = new HashMap<>();
            int alter = 0;

            if (a > avg) {
                set.add('A');
                differ.put('A', a - avg);
                alter += a - avg;
            }

            if (s > avg) {
                set.add('S');
                differ.put('S', s - avg);
                alter += s - avg;
            }

            if (d > avg) {
                set.add('D');
                differ.put('D', d - avg);
                alter += d - avg;
            }

            if (w > avg) {
                set.add('W');
                differ.put('W', w - avg);
                alter += w - avg;
            }

            int left = 0;
            int right = 0;
            int min = Integer.MAX_VALUE;

            while (right < cmds.length) {
                if (isOk(differ)) {
                    if (set.contains(cmds[left])) {
                        int v = differ.get(cmds[left]);
                        differ.put(cmds[left], v + 1);
                    }

                    min = Math.min(min, right - left);
                    left++;
                    continue;
                }

                if (set.contains(cmds[right])) {
                    int v = differ.get(cmds[right]);
                    differ.put(cmds[right], v - 1);
                }

                right++;
            }

            if (isOk(differ)) {
                // 此时 left 还有再次右移动的机会
                // 把 if 改成 while
                min = Math.min(min, right - left);
            }

            if (min < alter) {
                throw new RuntimeException(str);
            }

            System.out.println(min);
        }

        // ASWDAWDD
    }

    private static boolean isOk(Map<Character, Integer> map) {
        Set<Map.Entry<Character, Integer>> entries = map.entrySet();
        boolean ok = true;

        for (Map.Entry<Character, Integer> entry : entries) {
            if (entry.getValue() > 0) {
                return false;
            }
        }

        return ok;
    }

    // 感染天数
    private static void test3() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String str = in.nextLine();
            String[] items = str.split(",");
            int len = items.length;
            int n = (int) Math.sqrt(len);
            int[][] map = new int[n][n];
            int k = 0;
            boolean notFlu = true;
            boolean flu = true;
            Queue<Point> queue = new ArrayDeque<>();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    map[i][j] = Integer.parseInt(items[k++]);

                    if (map[i][j] == 1) {
                        notFlu = false;
                        queue.add(new Point(i, j));
                    }

                    if (map[i][j] == 0) {
                        flu = false;
                    }
                }
            }

            if (notFlu || flu) {
                System.out.println(-1);
                continue;
            }

            int day = 0;
            int[][] dd = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

            while (!queue.isEmpty()) {
                int size = queue.size();

                while (size > 0) {
                    Point point = queue.poll();

                    for (int[] d : dd) {
                        int i = point.m + d[0];
                        int j = point.n + d[1];

                        if (i < 0 || i >= n || j < 0 || j >= n || map[i][j] == 1) {
                            continue;
                        }

                        map[i][j] = 1;
                        queue.add(new Point(i, j));
                    }
                    size--;
                }
                day++;
            }

            // 1 0 1
            // 0 0 0
            // 1 0 1

            System.out.println(day - 1);
        }
    }

    static class Point {
        int m;
        int n;

        public Point(int m, int n) {
            this.m = m;
            this.n = n;
        }
    }

    public static void test4() {
        char[] cc = new char[]{'A', 'W', 'D', 'S'};
        dfs("", cc);
    }

    private static void dfs(String s, char[] cc) {
        if (!s.isEmpty() && s.length() % 4 == 0) {
            int len1 = replace1(s);
            int len2 = replace2(s);

            if (len1 != len2) {
                System.out.printf("len1: %d, len2: %d, s: %s\n", len1, len2, s);
                throw new RuntimeException("WTF");
            } else {
                System.out.printf("len1: %d, len2: %d, s: %s\n", len1, len2, s);
            }
        }

        if (s.length() >= 8) {
            return;
        }

        for (char c : cc) {
            dfs(s + c, cc);
        }
    }

    public static int replace1(String inputStr) {
        // 初始化方向键计数字典
        HashMap<Character, Integer> directionCount = new HashMap<>();
        directionCount.put('W', 0);
        directionCount.put('A', 0);
        directionCount.put('S', 0);
        directionCount.put('D', 0);

        // 统计输入字符串中每个方向键的出现次数
        for (char c : inputStr.toCharArray()) {
            directionCount.put(c, directionCount.get(c) + 1);
        }

        // 初始化左右指针和结果变量
        int left = 0;
        int right = 0;
        int minLength = inputStr.length();

        // 更新右指针对应的方向键计数
        directionCount.put(inputStr.charAt(0), directionCount.get(inputStr.charAt(0)) - 1);

        while (true) {
            // 计算当前最大方向键计数
            int maxCount = 0;
            for (int count : directionCount.values()) {
                maxCount = Math.max(maxCount, count);
            }

            // 计算当前窗口长度和可替换的字符数
            int windowLength = right - left + 1;
            int replaceableChars = windowLength;
            for (int count : directionCount.values()) {
                replaceableChars -= maxCount - count;
            }

            // 如果可替换字符数大于等于0且能被4整除，则更新结果变量
            if (replaceableChars >= 0 && replaceableChars % 4 == 0) {
                minLength = Math.min(minLength, windowLength);

                // 更新左指针并检查是否越界
                if (left < inputStr.length()) {
                    directionCount.put(inputStr.charAt(left), directionCount.get(inputStr.charAt(left)) + 1);
                    left++;
                } else {
                    break;
                }
            } else {
                // 更新右指针并检查是否越界
                right++;
                if (right >= inputStr.length()) {
                    break;
                }
                directionCount.put(inputStr.charAt(right), directionCount.get(inputStr.charAt(right)) - 1);
            }
        }

        return minLength;
    }

    public static int replace2(String str) {
        char[] cmds = str.toCharArray();
        int sum = str.length();
        int a = 0;
        int s = 0;
        int d = 0;
        int w = 0;

        for (char cmd : cmds) {
            if (cmd == 'A') {
                a++;
            } else if (cmd == 'S') {
                s++;
            } else if (cmd == 'D') {
                d++;
            } else {
                w++;
            }
        }

        int avg = sum / 4;

        if ((a == avg) && (s == avg) && (d == avg) && (w == avg)) {
            return 0;
        }

        Set<Character> set = new HashSet<>();
        Map<Character, Integer> differ = new HashMap<>();

        if (a > avg) {
            set.add('A');
            differ.put('A', a - avg);
        }

        if (s > avg) {
            set.add('S');
            differ.put('S', s - avg);
        }

        if (d > avg) {
            set.add('D');
            differ.put('D', d - avg);
        }

        if (w > avg) {
            set.add('W');
            differ.put('W', w - avg);
        }

        int left = 0;
        int right = 0;
        int min = Integer.MAX_VALUE;

        while (right < cmds.length) {
            if (isOk(differ)) {
                if (set.contains(cmds[left])) {
                    int v = differ.get(cmds[left]);
                    differ.put(cmds[left], v + 1);
                }

                min = Math.min(min, right - left);
                left++;
                continue;
            }

            if (set.contains(cmds[right])) {
                int v = differ.get(cmds[right]);
                differ.put(cmds[right], v - 1);
            }

            right++;
        }

        if (isOk(differ)) {
            min = Math.min(min, right - left);
        }

        return min;
    }
}
