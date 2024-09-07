package algorithm.leetcode.recursion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LetterCombinationsPhoneNumber {

    public List<String> letterCombinations(String digits) {
//        Map<Character, String> phoneMap = new HashMap<Character, String>() {{
//            put('2', "abc");
//            put('3', "def");
//            put('4', "ghi");
//            put('5', "jkl");
//            put('6', "mno");
//            put('7', "pqrs");
//            put('8', "tuv");
//            put('9', "wxyz");
//        }};

        Map<Character, String> mapping = new HashMap<>();
        mapping.put('2', "abc");
        mapping.put('3', "def");
        mapping.put('4', "ghi");
        mapping.put('5', "jkl");
        mapping.put('6', "mno");
        mapping.put('7', "pqrs");
        mapping.put('8', "tuv");
        mapping.put('9', "wxyz");

        List<String> ans = new ArrayList<>();

        if (digits.isEmpty()) {
            return ans;
        }

        StringBuilder sb = new StringBuilder();
        combine(ans, mapping, digits, sb, 0);
        return ans;
    }

    public void combine(List<String> ans, Map<Character, String> mapping, String digits, StringBuilder sb, int pos) {
        if (sb.length() == digits.length()) {
            ans.add(sb.toString());
            return;
        }

        char num = digits.charAt(pos);
        String chs = mapping.get(num);

        for (int i = 0; i < chs.length(); i++) {
            sb.append(chs.charAt(i));
            combine(ans, mapping, digits, sb, pos + 1);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
