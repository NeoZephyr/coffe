package algorithm.leetcode.recursion;

import java.util.*;

public class Permutations2 {

    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> seq = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        Map<Integer, Integer> count = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            Integer c = count.getOrDefault(nums[i], 0);
            count.put(nums[i], c + 1);
        }

        Set<Integer> uniqueNums = count.keySet();
        dfs(seq, path, nums, uniqueNums, count);
        return seq;
    }

    // for + dfs 对比 coin change
    public void dfs(List<List<Integer>> seq, List<Integer> path, int[] nums, Set<Integer> uniqueNums, Map<Integer, Integer> count) {
        if (path.size() == nums.length) {
            seq.add(new ArrayList<>(path));
            return;
        }

        for (Integer n : uniqueNums) {
            int c = count.get(n);

            if (c > 0) {
                path.add(n);
                c--;
                count.put(n, c);
                dfs(seq, path, nums, uniqueNums, count);
                c++;
                count.put(n, c);
                path.remove(path.size() - 1);
            }
        }
    }

    public List<List<Integer>> permuteUnique1(int[] nums) {
        List<List<Integer>> seq = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[nums.length];
        Arrays.sort(nums);
        dfs(seq, path, nums, visited);
        return seq;
    }

    public void dfs(List<List<Integer>> seq, List<Integer> path, int[] nums, boolean[] visited) {
        if (path.size() == nums.length) {
            seq.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) {
                continue;
            }

            if (i > 0 && nums[i] == nums[i - 1] && !visited[i - 1]) {
                continue;
            }

            visited[i] = true;
            path.add(nums[i]);
            dfs(seq, path, nums, visited);
            visited[i] = false;
            path.remove(path.size() - 1);
        }
    }
}
