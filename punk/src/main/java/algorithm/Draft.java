package algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class Draft {

    public static void main(String[] args) {
        Deque<Integer> queue = new ArrayDeque<>();
        int[] coins = new int[]{2, 9, 10};
        int amount = 38;
        new Draft().dfs(coins, amount, 0, new ArrayDeque<>());
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
}
