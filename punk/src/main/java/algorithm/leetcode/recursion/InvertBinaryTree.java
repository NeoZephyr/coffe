package algorithm.leetcode.recursion;

import algorithm.leetcode.tree.TreeNode;

import java.util.Deque;
import java.util.LinkedList;

public class InvertBinaryTree {

    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return root;
        }

        TreeNode tmp = root.left;
        root.left = root.right;
        root.right = tmp;

        invertTree(root.left);
        invertTree(root.right);

        return root;
    }

    public TreeNode invertTree1(TreeNode root) {
        Deque<TreeNode> queue = new LinkedList<>();

        if (root != null) {
            queue.offer(root);
        }

        // 广度遍历方式
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            TreeNode tmp = node.left;
            node.left = node.right;
            node.right = tmp;

            if (node.left != null) {
                queue.offer(node.left);
            }

            if (node.right != null) {
                queue.offer(node.right);
            }
        }

        return root;
    }
}
