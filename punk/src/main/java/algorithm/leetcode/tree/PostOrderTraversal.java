package algorithm.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * https://leetcode.cn/problems/binary-tree-postorder-traversal/
 */
public class PostOrderTraversal {

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        traversal(root, seq);
        return seq;
    }

    public List<Integer> postorderTraversal1(TreeNode root) {
        List<Integer> seq = new ArrayList<>();
        Deque<TreeNode> queue = new LinkedList<>();

        // 后序遍历中，从栈中弹出的节点，只能确定其左子树访问完了，但是无法确定右子树是否访问过
        // 引入 prev 来记录历史访问记录，当访问完一棵子树的时候，用 prev 指向该节点
        TreeNode prev = null;

        while (!queue.isEmpty() || root != null) {
            while (root != null) {
                queue.push(root);
                root = root.left;
            }

            root = queue.pop();

            // 是否有右子树，或者右子树是否访问过
            // 左子树一定是在父节点之前出栈并且处理
            if (root.right == null || root.right == prev) {
                seq.add(root.val);

                // 更新历史访问记录
                prev = root;
                root = null;
            } else {
                queue.push(root);
                root = root.right;
            }
        }

        return seq;
    }

    public List<Integer> postorderTraversal2(TreeNode root) {
        List<Integer> seq = new ArrayList<>();

        if (root == null) {
            return seq;
        }

        Deque<TreeNode> queue = new LinkedList<>();
        queue.push(root);

        while (!queue.isEmpty()) {
            TreeNode cur = queue.peek();

            if (cur.left != null && root != cur.left && root != cur.right) {
                queue.push(cur.left); // 左右子树都还没有访问
            } else if (cur.right != null && root != cur.right) {
                queue.push(cur.right); // 右子树还没有访问
            } else {
                root = queue.pop();
                seq.add(cur.val);
            }
        }

        return seq;
    }

    // 还有方法：
    // 原有的先序遍历是：中左右，可以改为中右左，然后逆序就是左右中，即为后续遍历的顺序了

    private void traversal(TreeNode root, List<Integer> seq) {
        if (root == null) {
            return;
        }

        traversal(root.left, seq);
        traversal(root.right, seq);
        seq.add(root.val);
    }
}
