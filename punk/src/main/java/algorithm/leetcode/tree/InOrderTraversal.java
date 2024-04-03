package algorithm.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class InOrderTraversal {

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        traversal(root, seq);
        return seq;
    }

    public List<Integer> inorderTraversal1(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        Deque<TreeNode> queue = new LinkedList<>();

        while (root != null || !queue.isEmpty()) {
            while (root != null) {
                queue.push(root);
                root = root.left;
            }

            root = queue.pop();
            seq.add(root.val);
            root = root.right;
        }

        return seq;
    }

    /**
     * 树的链接是单向的，从根节点出发，只有通往子节点的单向路程
     * 中序遍历迭代法的难点就在于，需要先访问当前节点的左子树，才能访问当前节点
     * 但是只有通往左子树的单向路程，而没有回程路，因此无法进行下去，除非用额外的数据结构记录下回程的路
     *
     * 可以利用当前节点的前驱节点，建立回程的路，也不需要消耗额外的空间。当前节点的前驱节点的右子节点是为空的，因此可以用其保存回程的路
     *
     * 注意，这是建立在破坏了树的结构的基础上的，因此最后还有一步消除链接的步骤，将树的结构还原
     */
    public List<Integer> inorderTraversal2(TreeNode root) {
        List<Integer> seq = new ArrayList<>();

        while (root != null) {
            if (root.left != null) {
                TreeNode pre = root.left;

                // 找 root 的前驱节点
                while (pre.right != null && pre.right != root) {
                    pre = pre.right;
                }

                // 第一个次经过 root
                if (pre.right == null) {
                    pre.right = root;
                    root = root.left;
                } else { // 通过 pre 节点第二次找到了 root

                    // 消除链接
                    pre.right = null;
                    seq.add(root.val);
                    root = root.right;
                }
            } else {
                seq.add(root.val);
                root = root.right;
            }
        }

        return seq;
    }

    public void traversal(TreeNode root, List<Integer> seq) {
        if (root == null) {
            return;
        }

        traversal(root.left, seq);
        seq.add(root.val);
        traversal(root.right, seq);
    }
}
