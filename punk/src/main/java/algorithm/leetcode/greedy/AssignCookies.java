package algorithm.leetcode.greedy;

import java.util.Arrays;
import java.util.Collections;

public class AssignCookies {

    public static void main(String[] args) {
        AssignCookies ac = new AssignCookies();
        System.out.println(ac.findContentChildren(new int[]{1, 2, 3}, new int[]{1, 1}));
        // System.out.println(ac.findContentChildren(new int[]{1, 2}, new int[]{1, 2, 3}));
    }

    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int i = g.length - 1;
        int j = s.length - 1;
        int ans = 0;

        while (i >= 0 && j >= 0) {
            if (s[j] >= g[i]) {
                ans++;
                j--;
                i--;
            } else {
                i--;
            }
        }

        return ans;
    }
}
