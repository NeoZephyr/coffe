package algorithm;

import java.util.*;

public class Draft {

    public static void main(String[] args) {
        Deque<Integer> queue = new ArrayDeque<>();
        int[] coins = new int[]{2, 9, 10};
        int amount = 38;
        // new Draft().dfs(coins, amount, 0, new ArrayDeque<>());

        Draft draft = new Draft();
        draft.testDfs1();
    }

    private void count(int[] coins, int amount) {
    }

    private void dfs(int[] coins, int amount, int total, Deque<Integer> queue) {
        if (total == amount) {
            System.out.println(queue);
            return;
        }

        if (total > amount) {
            return;
        }

        for (int i = 0; i < coins.length; i++) {
            total += coins[i];
            queue.add(coins[i]);
            dfs(coins, amount, total, queue);
            total -= coins[i];
            queue.removeLast();
        }
    }

    public void testDfs1() {
        int[] nums = {1, 2, 3};
        List<List<Integer>> ans = new ArrayList<>();
        Deque<Integer> path = new ArrayDeque<>();
        // dfs1(ans, path, nums, 0);
        dfs2(ans, path, nums);
        System.out.println(ans);
    }

    public void dfs1(List<List<Integer>> ans, Deque<Integer> path, int[] nums, int pos) {
        if (pos == nums.length) {
            ans.add(new ArrayList<>(path));
            return;
        }

        // 不选
        dfs1(ans, path, nums, pos + 1);

        // 选
        path.addLast(nums[pos]);
        dfs1(ans, path, nums, pos + 1);
        path.removeLast();
    }

    public void dfs2(List<List<Integer>> ans, Deque<Integer> path, int[] nums) {

        if (path.size() == nums.length) {
            ans.add(new ArrayList<>(path));
        }

        for (int i = 0; i < nums.length; i++) {
            // 不选
            // 不选的话，就不能达到终止条件，会栈溢出
            dfs2(ans, path, nums);

            // 选
            path.addLast(nums[i]);
            dfs2(ans, path, nums);
            path.removeLast();
        }
    }
}
