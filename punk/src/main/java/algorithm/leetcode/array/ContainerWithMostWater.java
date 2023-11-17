package algorithm.leetcode.array;

public class ContainerWithMostWater {
    public int maxArea(int[] height) {
        if (height == null || height.length < 2) {
            return 0;
        }

        int l = 0, r = height.length - 1;

        int max = -1;
        while (l < r) {

            int cand = (r - l) * Math.min(height[r], height[l]);
            if (cand > max) {
                max = cand;
            }

            // 右墙左移，舍弃候选区间：[(l, l + 1, ...), r]，而这些区间的面积一定小于 [l, r] 区间
            if (height[r] < height[l]) {
                --r;
            } else {
                ++l;
            }
        }

        return max;
    }

    public int maxArea1(int[] height) {
        int max = -1;

        for (int i = 0; i < height.length; ++i) {
            for (int j = i + 1; j < height.length; ++j) {
                int curArea = (j - i) * Math.min(height[i], height[j]);
                max = Math.max(max, curArea);
            }
        }

        return max;
    }

    public int maxArea2(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int max = -1;

        while (left < right) {
            int curHeight = (Math.min(height[left], height[right]));
            int curArea = curHeight * (right - left);
            max = Math.max(max, curArea);

            if (curHeight == height[left]) {
                left++;
            } else {
                right--;
            }
        }

        return max;
    }
}
