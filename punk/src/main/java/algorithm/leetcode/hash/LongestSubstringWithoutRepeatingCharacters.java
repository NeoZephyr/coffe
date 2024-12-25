package algorithm.leetcode.hash;

import java.util.HashSet;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters {

    public static void main(String[] args) {
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring1("abcabcbb"));
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring1("bbbbb"));
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring1("pwwkew"));
    }

    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        if (s.length() == 1) {
            return 1;
        }

        int ans = 1;
        int start = 0;

        for (int i = 1; i < s.length(); i++) {
            int repeatPos = -1;

            for (int j = start; j < i; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    repeatPos = j;
                    break;
                }
            }

            if (repeatPos == -1) {
                ans = Math.max(ans, (i - start + 1));
            } else {
                start = repeatPos + 1;
            }
        }

        return ans;
    }

    // 滑动窗口
    public int lengthOfLongestSubstring1(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        int ans = 1;
        int left = 0;
        Set<Character> set = new HashSet<>();
        set.add(s.charAt(left));

        for (int i = 1; i < s.length(); i++) {
            // 缩小窗口
            while (set.contains(s.charAt(i))) {
                set.remove(s.charAt(left++));
            }
            set.add(s.charAt(i));
            ans = Math.max(ans, i - left + 1); // 更新窗口大小
        }

        return ans;
    }
}
