package algorithm.hw;

import java.io.*;
import java.util.*;

public class Test1 {

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Integer next = (Integer) iterator.next();

            if (next == 3) {
                iterator.remove();
            }
        }

        System.out.println(list);

        for (int i = 0; i < list.size(); i++) {
            Integer o = (Integer) list.get(i);
            if (o == 4 || o == 5) {
                list.remove(i);
            }
        }

        System.out.println(list);

        list.forEach(x -> {
            if (x == 6) {
                list.remove(x);
            }
        });
    }

    private static void test0() throws IOException {
        // 相比与 Scanner，提高效率
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // 忽略空格、回车，高效读取
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out));

        // 按照行读取
        // reader.readLine();

        // 文件没有结束就继续
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            String s = tokenizer.sval;
            // 每次读之前，都要调用一下 nextToken
        }

        // 答案输出
        writer.println("");
        writer.flush();
        writer.close();
    }

    // 如果数值有范围，可以用一个数组
    // int[] arr = new int[1001];
    // arr[num] = 1;
    private static void test1() {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        TreeSet<Integer> set = new TreeSet<>();

        while (n > 0) {
            set.add(in.nextInt());
            n--;
        }

        for (Integer integer : set) {
            System.out.println(integer);
        }
    }

    // 16 进制 -> 10 进制
    // Integer.parseInt(FF, 16);
    private static void test2() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String s = in.next();
            int ans = 0;

            for (int i = 2; i < s.length(); i++) {
                char c = s.charAt(i);

                if (c >= 'A' && c <= 'F') {
                    ans = ans * 16 + (s.charAt(i) - 'A' + 10);
                } else {
                    ans = ans * 16 + (s.charAt(i) - '0');
                }
            }

            System.out.println(ans);
        }
    }

    // 质数因子
    private static void test3() {
        Scanner in = new Scanner(System.in);

        while (in.hasNextInt()) {
            int s = in.nextInt();
            int upper = (int) (Math.sqrt(s) + 1);
            int i = 2;

            while (i < upper) {
                if (isPrime(i)) {
                    while (s % i == 0) {
                        System.out.print(i + " ");
                        s = s / i;
                    }
                }
                i++;
            }

            if (s != 1) {
                System.out.println(s);
            }
        }
    }

    private static boolean isPrime(int num) {
        if (num == 1) {
            return false;
        }

        int upper = (int) Math.sqrt(num) + 1;

        for (int i = 2; i < upper; i++) {
            if (num % i == 0) {
                return false;
            }
        }

        return true;
    }

    // 质数因子
    private static void test4() {
        Scanner in = new Scanner(System.in);

        while (in.hasNextInt()) {
            int num = in.nextInt();
            int upper = (int) (Math.sqrt(num) + 1);

            for (int i = 2; i <= upper; i++) {
                while (num % i == 0) {
                    System.out.print(i + " ");
                    num = num / i;
                }
            }

            if (num != 1) {
                System.out.println(num);
            }
        }
    }

    // 取近似值
    // System.out.println((int)(number + 0.5));
    // Math.round(d);

    // 合并表记录
    // 可以一直 nextInt，跨行也可以
    private static void test5() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            int n = in.nextInt();
            in.nextLine(); // 避免输入的回车会被后续 nextLine 给接受
            TreeMap<Integer, Integer> map = new TreeMap<>();

            while (n > 0) {
                String s = in.nextLine();
                String[] items = s.split(" ");
                int index = Integer.parseInt(items[0]);
                int delta = Integer.parseInt(items[1]);
                int count = map.getOrDefault(index, 0);
                map.put(index, count + delta);
                n--;
            }

            for (Integer key : map.keySet()) {
                System.out.println(key + " " + map.get(key));
            }
        }
    }

    // 可以用 BitSet
    private static void test6() {
        Scanner in = new Scanner(System.in);

        while(in.hasNext()) {
            String s = in.nextLine();
            HashSet<Character> set = new HashSet<>();

            for (int i = 0; i < s.length(); i++) {
                set.add(s.charAt(i));
            }

            System.out.println(set.size());
        }
    }
}
