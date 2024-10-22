package algorithm.leetcode.search;

public class CoinChange {

    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
    }

    private void traverse(int[] coins, int[] memo, int amount) {
        if (amount == 0) {
            return;
        }

        for (int i = 0; i < coins.length; i++) {
            // TODO/kj
        }
    }

    // 记忆化搜索
}
