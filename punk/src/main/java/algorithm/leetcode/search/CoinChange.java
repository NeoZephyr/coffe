package algorithm.leetcode.search;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class CoinChange {

    int ans = Integer.MAX_VALUE;

    public static void main(String[] args) {
        CoinChange cc = new CoinChange();
        int count = cc.coinChange(new int[]{2, 9, 10}, 38);
        // int count = cc.coinChange(new int[]{411,412,413,414,415,416,417,418,419,420,421,422}, 9864);
        // int count = cc.coinChange(new int[]{284,260,393,494}, 7066);
        // [411,412,413,414,415,416,417,418,419,420,421,422]
        // int count = cc.coinChange(new int[]{2, 5, 1}, 11);
        System.out.println(count);
    }

    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.sort(coins);
        // Deque<CoinBatch> queue = new ArrayDeque<>();
        // dfs(coins, amount, coins.length - 1, 0, queue);

        System.out.println(Arrays.toString(memo));

        return coinChange(coins, amount, memo);
    }

    // subsets 与 permutation 的区别
    private void dfs(int[] coins, int amount, int pos, int count, Deque<CoinBatch> queue) {
        if (amount == 0) {
            if (ans > count) {
                ans = count;
            }
            // System.out.println(queue);
            return;
        }

        if (count >= ans) {
            return;
        }

        while (pos >=0 && amount < coins[pos]) {
            pos--;
        }

        if (pos < 0) {
            return;
        }

        int num = amount / coins[pos];
        // System.out.println("coin: " + coins[pos] + " num: " + num);

        for (int i = num; i >= 0; i--) {
            amount -= i * coins[pos];
            queue.add(new CoinBatch(coins[pos], i));
            dfs(coins, amount, pos - 1, count + i, queue);
            amount += i * coins[pos];
            queue.removeLast();
        }
    }

    // 记忆化搜索
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

            if (count + 1 < ans) {
                ans = count + 1;
            }
        }

        if (ans != Integer.MAX_VALUE) {
            memo[amount] = ans;
        } else {
            memo[amount] = -1;
        }

        return memo[amount];
    }

    static class CoinBatch {
        int coin;
        int count;

        public CoinBatch(int coin, int count) {
            this.coin = coin;
            this.count = count;
        }

        @Override
        public String toString() {
            return "CoinBatch{" +
                    "coin=" + coin +
                    ", count=" + count +
                    '}';
        }
    }
}
