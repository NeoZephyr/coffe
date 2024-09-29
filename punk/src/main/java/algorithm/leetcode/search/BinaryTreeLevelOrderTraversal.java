package algorithm.leetcode.search;

import algorithm.leetcode.tree.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BinaryTreeLevelOrderTraversal {

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();

        if (root == null) {
            return ans;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        List<Integer> values = new ArrayList<>();
        int c1 = 1;
        int c2 = 0;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node.left != null) {
                queue.offer(node.left);
                c2++;
            }

            if (node.right != null) {
                queue.offer(node.right);
                c2++;
            }

            values.add(node.val);
            c1--;

            if (c1 == 0) {
                ans.add(values);
                values = new ArrayList<>();
                c1 = c2;
                c2 = 0;
            }
        }

        return ans;
    }

    public List<List<Integer>> levelOrder1(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();

        if (root == null) {
            return ans;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            List<Integer> values = new ArrayList<>();
            int count = queue.size();

            for (int i = 0; i < count; i++) {
                TreeNode node = queue.poll();
                values.add(node.val);

                if (node.left != null) {
                    queue.offer(node.left);
                }

                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            ans.add(values);
        }

        return ans;
    }
}
