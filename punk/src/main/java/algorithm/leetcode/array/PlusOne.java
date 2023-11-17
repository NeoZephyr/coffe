package algorithm.leetcode.array;

public class PlusOne {

    public int[] plusOne(int[] digits) {
        int pos = digits.length - 1;

        while (pos >= 0) {
            digits[pos] = (digits[pos] + 1) % 10;

            if (digits[pos] != 0) {
                break;
            }

            pos--;
        }

        if (pos < 0) {
            digits = new int[digits.length + 1];
            digits[0] = 1;
        }

        return digits;
    }
}
