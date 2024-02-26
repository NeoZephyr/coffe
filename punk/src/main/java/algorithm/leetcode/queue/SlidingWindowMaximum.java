package algorithm.leetcode.queue;

import java.util.Deque;
import java.util.LinkedList;

public class SlidingWindowMaximum {

    /**
     * 单调队列 + 存储数组下标
     */
    public int[] maxSlidingWindow(int[] nums, int k) {
        if ((nums == null) || (nums.length == 0) || (nums.length < k)) {
            return new int[]{};
        }

        Deque<Integer> queue = new LinkedList<>();
        int left = 0;
        int right = 0;
        queue.offer(right++);

        while (right < k) {
            while (!queue.isEmpty() && nums[queue.peekLast()] < nums[right]) {
                queue.pollLast();
            }

            queue.offer(right++);
        }

        int[] result = new int[nums.length - k + 1];
        int j = 0;
        result[j++] = nums[queue.peekFirst()];

        while (right < nums.length) {
            while (!queue.isEmpty() && nums[queue.peekLast()] < nums[right]) {
                queue.pollLast();
            }

            queue.offer(right++);
            left++;

            if (!queue.isEmpty() && queue.peekFirst() < left) {
                queue.pollFirst();
            }

            result[j++] = nums[queue.peekFirst()];
        }

        return result;
    }
}
