package algorithm.leetcode.recursion;

import algorithm.leetcode.tree.TreeNode;

public class ValidateBinarySearchTree {

    public boolean isValidBST(TreeNode root) {
        if (root == null) {
            return true;
        }

        if (root.left != null) {
            TreeNode pre = root.left;

            while (pre.right != null) {
                pre = pre.right;
            }

            if (pre.val >= root.val) {
                return false;
            }
        }

        if (root.right != null) {
            TreeNode next = root.right;

            while (next.left != null) {
                next = next.left;
            }

            if (next.val <= root.val) {
                return false;
            }
        }

        return isValidBST(root.left) && isValidBST(root.right);
    }

    public boolean isValidBST1(TreeNode root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isValidBST(TreeNode root, long low, long high) {
        if (root == null) {
            return true;
        }

        if (root.val > low && root.val < high) {
            return isValidBST(root.left, low, root.val) && isValidBST(root.right, root.val, high);
        } else {
            return false;
        }
    }

    // 还可以用中序遍历，保证是递增的就可以了
}
