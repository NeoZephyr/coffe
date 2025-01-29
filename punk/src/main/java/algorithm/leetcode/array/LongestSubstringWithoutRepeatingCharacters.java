package algorithm.leetcode.array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters {

    // 滑动窗口
    public int lengthOfLongestSubstring1(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        if (s.length() == 1) {
            return 1;
        }

        int i = 0, j = -1;

        Map<Character, Integer> map = new HashMap<>();

        int len = -1;

        while (j + 1 < s.length()) {
            while (j + 1 < s.length() && map.get(s.charAt(j + 1)) == null) {
                map.put(s.charAt(j + 1), 1);
                ++j;
            }

            len = Math.max(len, j - i + 1);

            if (j + 1 == s.length()) {
                break;
            }

            while (i <= j) {
                map.remove(s.charAt(i));

                if (s.charAt(i++) == s.charAt(j + 1)) {
                    break;
                }
            }
        }

        return len;
    }

    public int lengthOfLongestSubstring2(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        if (s.length() == 1) {
            return 1;
        }

        int i = 0, j = -1;

        // 可以改成 boolean
        int[] freq = new int[256];

        int len = -1;

        while (j + 1 < s.length()) {
            if (j + 1 < s.length() && freq[s.charAt(j + 1)] == 0) {
                freq[s.charAt(++j)]++;
            } else {
                freq[s.charAt(i++)]--;
            }

            len = Math.max(len, j - i + 1);
        }

        return len;
    }

    public int lengthOfLongestSubstring3(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        int ans = 1;
        int left = 0;
        Set<Character> set = new HashSet<>();
        set.add(s.charAt(left));

        for (int i = 1; i < s.length(); i++) {
            while (set.contains(s.charAt(i))) {
                set.remove(s.charAt(left++));
            }
            set.add(s.charAt(i));
            ans = Math.max(ans, i - left + 1);
        }

        return ans;
    }
}
