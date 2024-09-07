package algorithm.leetcode.recursion;

import algorithm.leetcode.tree.TreeNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LowestCommonAncestor {

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        return lowestCommonAncestor1(root, p, q);
    }

    private TreeNode lowestCommonAncestor1(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            // 只要当前根节点是 p 和 q 中的任意一个，就返回
            // 不能比这个更深了，再深 p 和 q 中的一个就没了
            return root;
        }

        // 根节点不是 p 和 q 中的任意一个，继续分别往左子树和右子树找 p 和 q
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // 左右子树都找到 p 和 q，说明 p 和 q 分别在左右两个子树上，所以此时的最近公共祖先就是 root
        if (left != null && right != null) {
            return root;
        }

        // 右子树没有 p 也没有 q 就返回左子树的结果
        if (left != null) {
            return left;
        }

        if (right != null) {
            return right;
        }

        // p 和 q 都没找到，就没有
        return null;
    }

    private Map<TreeNode, TreeNode> parents = new HashMap<>();
    private Set<TreeNode> visits = new HashSet<>();

    private TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        dfs(root);

        // 不用引入新的变量，直接使用 p q 即可
        while (p != null) {
            visits.add(p);
            p = parents.get(p);
        }

        while (q != null) {
            if (visits.contains(q)) {
                return q;
            }
            q = parents.get(q);
        }

        return null;
    }

    private void dfs(TreeNode root) {
        if (root.left != null) {
            parents.put(root.left, root);
            dfs(root.left);
        }

        if (root.right != null) {
            parents.put(root.right, root);
            dfs(root.right);
        }
    }
}
