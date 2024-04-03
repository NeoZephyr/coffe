package algorithm.leetcode.recursion;

import algorithm.leetcode.tree.TreeNode;

public class MaximumDepthOfBinaryTree {

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
        return 1 + Math.max(left, right);
    }

    // 还可以层序遍历，广度优先算法
}
