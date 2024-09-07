package algorithm.leetcode.recursion;

import java.util.ArrayList;
import java.util.List;

public class Permutations {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> seq = new ArrayList<>();
        List<Integer> path = new ArrayList<>(nums.length);
        boolean[] visited = new boolean[nums.length];
        dfs(path, seq, visited, nums);
        return seq;
    }

    /**
     *
     * 全排列问题的树形结构
     * 1. 每一个结点表示了求解全排列问题的不同的阶段，这些阶段通过变量的「不同的值」体现，这些变量的不同的值，称之为「状态」
     *
     * 2. 使用深度优先遍历有「回头」的过程，在「回头」以后，状态变量需要设置成为和先前一样，因此在回到上一层结点的过程中，
     * 需要撤销上一次的选择，这个操作称之为「状态重置」
     *
     * 3. 深度优先遍历，借助系统栈空间，保存所需要的状态变量，在编码中只需要注意遍历到相应的结点的时候，状态变量的值是正确的
     * 具体的做法是：往下走一层的时候，path 变量在尾部追加，而往回走的时候，需要撤销上一次的选择，也是在尾部操作
     *
     * 4. 深度优先遍历通过「回溯」操作，实现了全局使用一份状态变量的效果
     *
     *
     */
    public void dfs(List<Integer> path, List<List<Integer>> seq, boolean[] visited, int[] nums) {
        if (path.size() == nums.length) {
            seq.add(new ArrayList<>(path));
        }

        for (int i = 0; i < nums.length; i++) {
            if (!visited[i]) {
                visited[i] = true;
                path.add(nums[i]);
                dfs(path, seq, visited, nums);

                // 回溯
                path.remove(path.size() - 1);
                visited[i] = false;
            }
        }
    }
}
