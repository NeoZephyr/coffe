package algorithm.leetcode.hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstringWithConcatenationWords {

    public static void main(String[] args) {
        SubstringWithConcatenationWords instance = new SubstringWithConcatenationWords();
        System.out.println(instance.findSubstring(
                "barfoofoobarthefoobarman",
                new String[]{"bar","foo","the"}));
    }

    // 滑动窗口 + 哈希表
    public List<Integer> findSubstring(String s, String[] words) {
        int wordLen = words[0].length();
        List<Integer> ans = new ArrayList<>();

        for (int r = 0; r < wordLen; r++) {
            if (r + wordLen * words.length > s.length()) {
                break;
            }

            Map<String, Integer> differ = new HashMap<>();
            int left = r;
            int right = left + wordLen * words.length;

            for (int i = left; i < right; i += wordLen) {
                String k = s.substring(i, i + wordLen);
                int v = differ.getOrDefault(k, 0);
                differ.put(k, v + 1);
            }

            for (String word : words) {
                int v = differ.getOrDefault(word, 0);

                if (v == 1) {
                    differ.remove(word);
                } else {
                    differ.put(word, v - 1);
                }
            }

            if (differ.isEmpty()) {
                ans.add(left);
            }

            while (right <= s.length() - wordLen) {
                String deleteWord = s.substring(left, left + wordLen);
                String incrWord = s.substring(right, right + wordLen);
                int v = differ.getOrDefault(deleteWord, 0);

                if (v == 1) {
                    differ.remove(deleteWord);
                } else {
                    differ.put(deleteWord, v - 1);
                }

                v = differ.getOrDefault(incrWord, 0);

                if (v == -1) {
                    differ.remove(incrWord);
                } else {
                    differ.put(incrWord, v + 1);
                }

                left += wordLen;
                right += wordLen;

                if (differ.isEmpty()) {
                    ans.add(left);
                }
            }
        }

        return ans;
    }
}
