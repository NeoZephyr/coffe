package algorithm.leetcode.dp;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * https://leetcode.cn/problems/coin-change/description/
 */
public class CoinChange {

    int ans = Integer.MAX_VALUE;

    public static void main(String[] args) {
        CoinChange cc = new CoinChange();
        // int count = cc.coinChange1(new int[]{2, 9, 10}, 38);
        int count = cc.coinChange3(new int[]{474,83,404,3}, 264);
        // int count = cc.coinChange(new int[]{411,412,413,414,415,416,417,418,419,420,421,422}, 9864);
        // int count = cc.coinChange(new int[]{284,260,393,494}, 7066);
        // [411,412,413,414,415,416,417,418,419,420,421,422]
        // int count = cc.coinChange(new int[]{2, 5, 1}, 11);
        System.out.println(count);
    }

    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.sort(coins);
        return coinChange(coins, amount, memo);
    }

    private int coinChange(int[] coins, int amount, int[] memo) {
        if (amount == 0) {
            return 0;
        }

        if (amount < 0) {
            return -1;
        }

        if (memo[amount] != 0) {
            return memo[amount];
        }

        int ans = Integer.MAX_VALUE;

        for (int i = 0; i < coins.length; i++) {
            int count = coinChange(coins, amount - coins[i], memo);

            if (count == -1) {
                continue;
            }

            ans = Math.min(ans, count + 1);
        }

        if (ans != Integer.MAX_VALUE) {
            memo[amount] = ans;
        } else {
            memo[amount] = -1;
        }

        return memo[amount];
    }

    public int coinChange1(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -1);
        memo[amount] = 0;
        coinChange1(coins, amount, memo);
        return memo[0];
    }

    private void coinChange1(int[] coins, int amount, int[] memo) {
        for (int i = 0; i < coins.length; i++) {
            if (coins[i] > amount) {
                continue;
            }

            int tmp = amount - coins[i];

            // 如果此时 memo[amount] 还不是最小的，那么后续达到最小的时候，逻辑还是会走到这里
            if (memo[tmp] == -1 || memo[tmp] > memo[amount] + 1) {
                memo[tmp] = memo[amount] + 1;
                coinChange1(coins, tmp, memo);
            }
        }
    }

    // bfs 遍历，求位 0 的层次最小的
    public int coinChange2(int[] coins, int amount) {
        Deque<Node> queue = new ArrayDeque<>();
        Arrays.sort(coins);
        queue.add(new Node(amount, 0));
        int ans = Integer.MAX_VALUE;
        boolean[] visited = new boolean[amount + 1];

        while (!queue.isEmpty()) {
            Node node = queue.poll();

            // 只要遇到 0，就找到了一个最短路径
            // count 其实就是当前树的深度，可以用一个 level 变量替换
            if (node.left == 0) {
                ans = node.count;
                break;
            }

            for (int coin : coins) {
                // 由于 coins 升序排序，后面的面值会越来越大，剪枝
                if (node.left < coin) {
                    break;
                }

                if (visited[node.left - coin]) {
                    continue;
                }

                visited[node.left - coin] = true;
                queue.add(new Node(node.left - coin, node.count + 1));
            }
        }

        if (ans == Integer.MAX_VALUE) {
            ans = -1;
        }

        return ans;
    }

    // coins = [1, 2, 5], amount = 11
    // 类比走楼梯
    // 记忆化搜索
    // dp 方程 f(n) = min{f(n - k), k = coins[i]} + 1
    public int coinChange3(int[] coins, int amount) {
        int[] dp = new int[amount + 1];

        for (int i = 1; i <= amount; i++) {
            int min = Integer.MAX_VALUE;

            for (int coin : coins) {
                if (i >= coin && dp[i - coin] != -1) {
                    min = Math.min(min, dp[i - coin] + 1);
                }
            }

            if (min != Integer.MAX_VALUE) {
                dp[i] = min;
            } else {
                dp[i] = -1;
            }
        }

        return dp[amount];
    }

    public int coinChange4(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }

        if (dp[amount] == amount + 1) {
            dp[amount] = -1;
        }
        return dp[amount];
    }

    // dfs 方法，考虑怎么剪枝
    // subsets 与 permutation 的区别

    static class Node {
        int left;
        int count;

        public Node(int left, int count) {
            this.left = left;
            this.count = count;
        }
    }
}
