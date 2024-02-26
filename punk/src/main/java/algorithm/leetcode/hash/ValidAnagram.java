package algorithm.leetcode.hash;

import java.util.HashMap;
import java.util.Map;

public class ValidAnagram {

    /**
     *
     * 首先判断两个字符串长度是否相等，不相等则直接返回 false
     * 若相等，则初始化 26 个字母哈希表，遍历字符串 s 和 t
     * s 负责在对应位置增加，t 负责在对应位置减少
     * 如果哈希表的值都为 0，则二者是字母异位词
     */
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int len = s.length();
        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < len; i++) {
            if (map.containsKey(s.charAt(i))) {
                map.put(s.charAt(i), map.get(s.charAt(i)) + 1);
            } else {
                map.put(s.charAt(i), 1);
            }

            if (map.containsKey(t.charAt(i))) {
                map.put(t.charAt(i), map.get(t.charAt(i)) - 1);
            } else {
                map.put(t.charAt(i), -1);
            }
        }

        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() != 0) {
                return false;
            }
        }

        return true;
    }
}
