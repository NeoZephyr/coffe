package algorithm.leetcode.hash;

import java.util.*;

public class GroupAnagrams {

    // 还可以用 int[26] 统计字符出现的次数，然后将每个出现次数大于 0 的字母和出现次数按顺序拼接成字符串，作为哈希表的键
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String str : strs) {
            char[] array = str.toCharArray();
            Arrays.sort(array);
            String key = new String(array);
            List<String> list = map.getOrDefault(key, new ArrayList<>());
            list.add(str);
            map.put(key, list);
        }

        return new ArrayList<>(map.values());
    }

}
