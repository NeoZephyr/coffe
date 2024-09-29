package algorithm.leetcode.search;

import algorithm.leetcode.tree.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FindLargestValueEachTreeRow {

    public List<Integer> largestValues(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();

        if (root == null) {
            return ans;
        }

        queue.offer(root);

        while (!queue.isEmpty()) {
            int sz = queue.size();
            int max = Integer.MIN_VALUE;

            for (int i = 0; i < sz; i++) {
                TreeNode node = queue.poll();

                if (node.val > max) {
                    max = node.val;
                }

                if (node.left != null) {
                    queue.offer(node.left);
                }

                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            ans.add(max);
        }

        return ans;
    }

    public List<Integer> largestValues1(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        dfs(root, ans, 1);
        return ans;
    }

    public void dfs(TreeNode node, List<Integer> ans, int level) {
        if (node == null) {
            return;
        }

        if (ans.size() < level) {
            ans.add(node.val);
        } else {
            ans.set(level - 1, Math.max(ans.get(level - 1), node.val));
        }

        dfs(node.left, ans, level + 1);
        dfs(node.right, ans, level + 1);
    }
}
