package algorithm.leetcode.recursion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Subsets {

    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        Deque<Integer> path = new ArrayDeque<>();
        dfs(ans, path, nums, 0);
        return ans;
    }

    public void dfs(List<List<Integer>> ans, Deque<Integer> path, int[] nums, int pos) {
        if (pos == nums.length) {
            ans.add(new ArrayList<>(path));
            return;
        }

        // 不选
        dfs(ans, path, nums, pos + 1);

        // 选
        path.addLast(nums[pos]);
        dfs(ans, path, nums, pos + 1);
        path.removeLast();
    }

    List<Integer> t = new ArrayList<>();
    List<List<Integer>> ans = new ArrayList<>();

    public List<List<Integer>> subsets1(int[] nums) {
        int n = nums.length;

        // 原序列中的每个数字的状态可能有两种，即「在子集中」和「不在子集中」
        // 用 1 表示「在子集中」，0 表示不在子集中，那么每一个子集可以对应一个长度为 n 的 0/1 序列
        // 第 i 位表示第 i 个数是否在子集中
        for (int mask = 0; mask < (1 << n); ++mask) {
            t.clear();
            for (int i = 0; i < n; ++i) {
                if ((mask & (1 << i)) != 0) {
                    t.add(nums[i]);
                }
            }
            ans.add(new ArrayList<>(t));
        }
        return ans;
    }

    // 逐个枚举，空集的幂集只有空集，每增加一个元素，让之前幂集中的每个集合，追加这个元素，就是新增的子集
}
