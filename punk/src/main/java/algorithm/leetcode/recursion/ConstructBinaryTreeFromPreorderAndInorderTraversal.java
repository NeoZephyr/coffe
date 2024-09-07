package algorithm.leetcode.recursion;

import algorithm.leetcode.tree.TreeNode;

public class ConstructBinaryTreeFromPreorderAndInorderTraversal {

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        return buildTree(preorder, 0, inorder, 0, inorder.length - 1);
    }

    // 在中序里面寻找位置时，还可以使用哈希表加速
    private TreeNode buildTree(int[] preorder, int pos, int[] inorder, int l, int r) {
        if (l > r) {
            return null;
        }

        int v = preorder[pos];
        int p = l;

        while (inorder[p] != v) {
            p++;
        }

        TreeNode root = new TreeNode();
        root.val = v;
        root.left = buildTree(preorder, pos + 1, inorder, l, p - 1);

        int leftCount = p - l;
        root.right = buildTree(preorder, pos + leftCount + 1, inorder, p + 1, r);
        return root;
    }
}
