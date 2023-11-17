package algorithm.leetcode.array;

public class ReverseString {

    public String reverseString1(String s) {
        return new StringBuffer(s).reverse().toString();
    }

    public String reverseString2(String s) {
        char[] arr = s.toCharArray();
        int i = 0, j = arr.length - 1;
        while (i < j) {
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++;
            j--;
        }
        return String.valueOf(arr);
    }
}
