package algorithm.acm.math;

public class CountBit {

    public static void main(String[] args) {
        System.out.println(new CountBit().count(0x1101));
    }

    public int count(int x) {
        int c = 0;

        while (x != 0) {
            // 1001 & 1010 = 1000
            // 每次计算将最低位的 1 消除
            x = (x - 1) & x;
            c++;
        }

        return c;
    }
}
