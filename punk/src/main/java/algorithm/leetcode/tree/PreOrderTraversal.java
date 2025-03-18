package algorithm.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * https://leetcode.cn/problems/binary-tree-preorder-traversal/description/
 */
public class PreOrderTraversal {

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        traversal(root, seq);
        return seq;
    }

    public List<Integer> preorderTraversal1(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        Deque<TreeNode> queue = new LinkedList<>();

        while (!queue.isEmpty() || root != null) {
            // 参考 in order，只需要改 seq.add(root.val) 的为止
            if (root != null) {
                seq.add(root.val);
                queue.push(root);
                root = root.left;
            } else {
                root = queue.pop();
                root = root.right;
            }
        }
        return seq;
    }

    public List<Integer> preorderTraversal11(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        Deque<TreeNode> queue = new LinkedList<>();

        while (!queue.isEmpty() || root != null) {
            while (root != null) {
                seq.add(root.val);
                queue.push(root);
                root = root.left;
            }

            root = queue.pop();
            root = root.right;
        }
        return seq;
    }

    public List<Integer> preorderTraversal3(TreeNode root) {
        List<Integer> seq = new ArrayList<>();

        if (root == null) {
            return seq;
        }

        Deque<TreeNode> queue = new LinkedList<>();
        queue.push(root);

        while (!queue.isEmpty()) {
            root = queue.pop();
            seq.add(root.val);

            // 先压右，再压左。保证弹出时，先处理左子树

            if (root.right != null) {
                queue.push(root.right);
            }

            if (root.left != null) {
                queue.push(root.left);
            }
        }

        return seq;
    }

    public List<Integer> preorderTraversal2(TreeNode root) {
        List<Integer> seq = new ArrayList<>();

        while (root != null) {
            if (root.left == null) {
                seq.add(root.val);
                root = root.right;
            } else {
                TreeNode pre = root.left;

                while (pre.right != null && pre.right != root) {
                    pre = pre.right;
                }

                if (pre.right == null) {
                    seq.add(root.val);
                    pre.right = root;
                    root = root.left;
                } else {
                    pre.right = null;
                    root = root.right;
                }
            }
        }

        return seq;
    }

    private void traversal(TreeNode root, List<Integer> seq) {
        if (root == null) {
            return;
        }
        seq.add(root.val);
        // 1. 第一次触碰 root
        traversal(root.left, seq);
        // 2. 第二次触碰 root
        traversal(root.right, seq);
        // 3. 第三次触碰 root
    }
}
