package algorithm.leetcode.stack;

import java.util.Stack;

public class LargestRectangleInHistogram {

    /**
     * 以第 i 根柱子为最矮柱子所能延伸的最大面积
     */
    public int largestRectangleArea(int[] heights) {
        if (heights == null || heights.length == 0) {
            return 0;
        }

        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;

        for (int i = 0; i < heights.length; i++) {

            while (!stack.isEmpty() && heights[i] < heights[stack.peek()]) {
                int curHeight = heights[stack.pop()];

                while (!stack.isEmpty() && heights[stack.peek()] == curHeight) {
                    stack.pop();
                }

                int width;

                if (stack.isEmpty()) {
                    width = i;
                } else {
                    width = i - stack.peek() - 1;
                }

                int area = width * curHeight;

                if (area > maxArea) {
                    maxArea = area;
                }
            }

            stack.push(i);
        }

        while (!stack.isEmpty()) {
            int curHeight = heights[stack.pop()];

            while (!stack.isEmpty() && heights[stack.peek()] == curHeight) {
                stack.pop();
            }

            int width;
            int idx = heights.length;

            if (stack.isEmpty()) {
                width = idx;
            } else {
                width = idx - stack.peek() - 1;
            }

            int area = width * curHeight;

            if (area > maxArea) {
                maxArea = area;
            }
        }

        return maxArea;
    }
}
