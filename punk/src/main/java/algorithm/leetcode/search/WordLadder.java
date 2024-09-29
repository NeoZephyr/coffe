package algorithm.leetcode.search;

import java.util.*;

public class WordLadder {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> set = new HashSet<>(wordList);
        Set<String> visited = new HashSet<>();

        if (!set.contains(endWord)) {
            return 0;
        }

        Queue<String> queue = new ArrayDeque<>();
        queue.offer(beginWord);
        visited.add(beginWord);
        int level = 1;

        while (!queue.isEmpty()) {
            int sz = queue.size();

            for (int i = 0; i < sz; i++) {
                String word = queue.poll();

                if (endWord.equals(word)) {
                    return level;
                }

                // word_size <= 10
                // word_size * 26
                // word_list_size <= 5000

                // method1
                // 优化：把 visited 从 HashSet 改成 boolean 数组，通过 index 判断是否已访问
//                for (String w : wordList) {
//                    if (!visited.contains(w) && adjacent(w, word)) {
//                        queue.offer(w);
//                        visited.add(w);
//                    }
//                }

                // method2
                char[] chars = word.toCharArray();

                for (int j = 0; j < chars.length; j++) {
                    char oc = chars[j];

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == oc) {
                            continue;
                        }

                        chars[j] = c;
                        String nextWord = new String(chars);

                        if (set.contains(nextWord) && !visited.contains(nextWord)) {
                            queue.offer(nextWord);
                            visited.add(nextWord);
                        }
                    }

                    chars[j] = oc;
                }
            }

            level++;
        }

        return 0;
    }

    int ans = Integer.MAX_VALUE;

    public int ladderLength1(String beginWord, String endWord, List<String> wordList) {
        Set<String> words = new HashSet<>(wordList);
        Set<String> visited = new HashSet<>();
        int level = 1;
        visited.add(beginWord);
        dfs(beginWord, endWord, words, visited, level);
        return (ans == Integer.MAX_VALUE ? 0 : ans);
    }

    public int ladderLength2(String beginWord, String endWord, List<String> wordList) {
        Set<String> begin = new HashSet<>();
        Set<String> end = new HashSet<>();
        Set<String> words = new HashSet<>(wordList);
        Set<String> visited = new HashSet<>();
        int level = 1;
        begin.add(beginWord);
        end.add(endWord);
        visited.add(beginWord);
        visited.add(endWord);

        if (!words.contains(endWord)) {
            return 0;
        }

        while (!begin.isEmpty()) {
            if (begin.size() > end.size()) {
                Set<String> tmp = end;
                end = begin;
                begin = tmp;
            }

            Set<String> next = new HashSet<>();

            for (String w : begin) {
                char[] chars = w.toCharArray();

                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i];

                    for (char k = 'a'; k <= 'z'; k++) {
                        if (k == c) {
                            continue;
                        }

                        chars[i] = k;
                        String nextWord = new String(chars);

                        if (end.contains(nextWord)) {
                            return level + 1;
                        }

                        if (words.contains(nextWord) && !visited.contains(nextWord)) {
                            visited.add(nextWord);
                            next.add(nextWord);
                        }
                    }

                    chars[i] = c;
                }
            }

            begin = next;
            level++;
        }

        return 0;
    }

    public int ladderLength3(String beginWord, String endWord, List<String> wordList) {
        Map<String, List<String>> adj = new HashMap<>();

        // 填充无向图的邻接表
        for (String word : wordList) {
            addToAdj(word, adj);
        }
        addToAdj(beginWord, adj);

        Queue<String> queue = new ArrayDeque<>();
        queue.offer(beginWord);

        Map<String, Integer> dist = new HashMap<>();
        dist.put(beginWord, 0);

        while (!queue.isEmpty()) {
            String word = queue.poll();
            List<String> neighbors = adj.get(word);

            for (String neighbor : neighbors) {
                if (!dist.containsKey(neighbor)) {
                    dist.put(neighbor, dist.get(word) + 1);
                    queue.offer(neighbor);

                    if (neighbor.equals(endWord)) {
                        return dist.get(neighbor) / 2 + 1;
                    }
                }
            }
        }

        return 0;
    }

    public void addToAdj(String word, Map<String, List<String>> adj) {
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            chars[i] = '*';
            String w = new String(chars);
            adj.computeIfAbsent(word, x -> new ArrayList<>()).add(w);
            adj.computeIfAbsent(w, x -> new ArrayList<>()).add(word);
            chars[i] = c;
        }
    }

    public void dfs(String current, String end, Set<String> words, Set<String> visited, int level) {
        if (current.equals(end)) {
            ans = Math.min(ans, level);
            return;
        }

        for (String w : words) {
            if (!visited.contains(w) && adjacent(w, current)) {
                visited.add(w);
                level++;
                dfs(w, end, words, visited, level);
                visited.remove(w);
                level--;
            }
        }
    }

    public boolean adjacent(String word1, String word2) {
        int l = word1.length();
        int d = 0;

        for (int i = 0; i < l; i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                d++;

                if (d > 1) {
                    return false;
                }
            }
        }

        return d == 1;
    }
}
