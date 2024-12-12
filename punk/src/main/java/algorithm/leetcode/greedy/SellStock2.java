package algorithm.leetcode.greedy;

public class SellStock2 {

    public int maxProfit(int[] prices) {
        int ans = 0;

        for (int i = 0; i < prices.length - 1; i++) {
            if (prices[i + 1] >= prices[i]) {
                ans += prices[i + 1] - prices[i];
            }
        }

        return ans;
    }

    // 暴力搜索
    // 没有买入时，可以买入或者不买入 status = 0
    // 买入后，可以选择卖出或者不卖出 status = 1
}
