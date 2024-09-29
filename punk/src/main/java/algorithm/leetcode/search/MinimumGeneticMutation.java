package algorithm.leetcode.search;

import java.util.*;

public class MinimumGeneticMutation {

    public int minMutation(String startGene, String endGene, String[] bank) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> set = new HashSet<>(Arrays.asList(bank));
        Set<String> visited = new HashSet<>();
        char[] keys = new char[]{'A', 'C', 'G', 'T'};

        if (!set.contains(endGene)) {
            return -1;
        }

        queue.offer(startGene);
        visited.add(startGene);
        int level = 0;

        while (!queue.isEmpty()) {
            int sz = queue.size();

            for (int i = 0; i < sz; i++) {
                String gene = queue.poll();

                if (gene.equals(endGene)) {
                    return level;
                }

                for (int j = 0; j < 8; j++) {
                    // 直接向 end gene 的方向变换，可能不在有效的 gene 序列内
                    for (char c : keys) {
                        if (gene.charAt(j) != c) {
                            StringBuilder sb = new StringBuilder(gene);
                            sb.setCharAt(j, c);
                            String s = sb.toString();

                            if (set.contains(s) && !visited.contains(s)) {
                                queue.offer(sb.toString());
                                visited.add(s);
                            }
                        }
                    }
                }
            }
            level++;
        }

        return -1;
    }

    public int minMutation1(String startGene, String endGene, String[] bank) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.offer(startGene);
        visited.add(startGene);
        int level = 0;

        while (!queue.isEmpty()) {
            int sz = queue.size();
            level++;

            for (int i = 0; i < sz; i++) {
                String gene = queue.poll();

                // 不盲目搜索，只搜索存在在 bank 中的，且与当前的只差一个字符的 gene
                for (String item : bank) {
                    if (item.equals(gene)) {
                        continue;
                    }

                    if (visited.contains(item)) {
                        continue;
                    }

                    int diff = 0;

                    for (int j = 0; j < 8; j++) {
                        if (gene.charAt(j) != item.charAt(j)) {
                            diff++;
                        }

                        if (diff > 1) {
                            break;
                        }
                    }

                    if (diff != 1) {
                        continue;
                    }

                    if (item.equals(endGene)) {
                        return level;
                    }

                    queue.offer(item);
                    visited.add(item);
                }
            }
        }

        return -1;
    }

    int ans = Integer.MAX_VALUE;

    public int minMutation2(String startGene, String endGene, String[] bank) {
        boolean[] visited = new boolean[bank.length];
        dfs(startGene, endGene, bank, visited, 0);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    public void dfs(String gene, String endGene, String[] bank, boolean[] visited, int level) {
        if (gene.equals(endGene)) {
            ans = Math.min(ans, level);
            return;
        }

        for (int i = 0; i < bank.length; i++) {
            if (visited[i]) {
                continue;
            }

            int diff = 0;

            for (int j = 0; j < 8; j++) {
                if (gene.charAt(j) != bank[i].charAt(j)) {
                    diff++;
                }

                if (diff > 1) {
                    break;
                }
            }

            if (diff == 1) {
                visited[i] = true;
                dfs(bank[i], endGene, bank, visited, level + 1);
                visited[i] = false;
            }
        }
    }

    // bfs 双向
    public int minMutation3(String startGene, String endGene, String[] bank) {
        Queue<String> queue1 = new ArrayDeque<>();
        Queue<String> queue2 = new ArrayDeque<>();
        Set<String> visited1 = new HashSet<>();
        Set<String> visited2 = new HashSet<>();
        Set<String> dict = new HashSet<>(Arrays.asList(bank));

        if (!dict.contains(endGene)) {
            return -1;
        }

        queue1.offer(startGene);
        queue2.offer(endGene);
        visited1.add(startGene);
        visited2.add(endGene);
        int level = 0;

        while (!queue1.isEmpty() && !queue2.isEmpty()) {

            if (queue1.size() > queue2.size()) {
                Queue<String> q = queue2;
                queue2 = queue1;
                queue1 = q;

                Set<String> v = visited2;
                visited2 = visited1;
                visited1 = v;
            }

            int sz = queue1.size();

            for (int i = 0; i < sz; i++) {
                String gene = queue1.poll();

                // 不盲目搜索，只搜索存在在 bank 中的，且与当前的只差一个字符的 gene
                for (String item : bank) {
                    int diff = 0;

                    for (int j = 0; j < 8; j++) {
                        if (gene.charAt(j) != item.charAt(j)) {
                            diff++;
                        }

                        if (diff > 1) {
                            break;
                        }
                    }

                    if (diff != 1) {
                        continue;
                    }

                    if (visited1.contains(item)) {
                        continue;
                    }

                    if (item.equals(endGene)) {
                        return level + 1;
                    }

                    if (visited2.contains(item)) {
                        return level + 1;
                    }

                    queue1.offer(item);
                    visited1.add(item);
                }
            }

            level++;
        }

        return -1;
    }
}
