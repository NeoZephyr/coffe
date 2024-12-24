package algorithm.leetcode.hash;

public class LongestSubstringWithoutRepeatingCharacters {

    public static void main(String[] args) {
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring("abcabcbb"));
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring("bbbbb"));
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring("pwwkew"));
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
                if (s.charAt(start) == s.charAt(j)) {
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
}
