package algorithm.leetcode.greedy;

public class LemonadeChange {

    public static void main(String[] args) {
        LemonadeChange change = new LemonadeChange();
        System.out.println(change.lemonadeChange(new int[]{5,5,5,10,20}));
        System.out.println(change.lemonadeChange(new int[]{5,5,10,20,5,5,5,5,5,5,5,5,5,10,5,5,20,5,20,5}));
    }

    // [5,5,5,10,20]
    // [5,5,10,10,20]
    public boolean lemonadeChange(int[] bills) {
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;

        for (int bill : bills) {
            if (bill == 5) {
                count1++;
                continue;
            }

            if (bill == 10) {
                if (count1 > 0) {
                    count2++;
                    count1--;
                    continue;
                } else {
                    return false;
                }
            }

            if (bill == 20) {
                if (count1 > 0 && count2 > 0) {
                    count1--;
                    count2--;
                    count3++;
                    continue;
                } else if (count1 >= 3) {
                    count1 -= 3;
                    count3++;
                } else {
                    return false;
                }
            }
        }

        return true;
    }
}
