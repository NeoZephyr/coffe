package algorithm.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class NaryTreePreorderTraversal {

    public List<Integer> preorder(Node root) {
        List<Integer> seq = new ArrayList<>();
        preorder(root, seq);
        return seq;
    }

    public List<Integer> preorder1(Node root) {
        List<Integer> seq = new ArrayList<>();
        Deque<Node> queue = new LinkedList<>();

        if (root == null) {
            return seq;
        }

        queue.push(root);

        while (!queue.isEmpty()) {
            root = queue.pop();
            seq.add(root.val);

            for (int i = root.children.size() - 1; i >= 0; i--) {
                queue.push(root.children.get(i));
            }
        }

        return seq;
    }

    private void preorder(Node root, List<Integer> seq) {
        if (root == null) {
            return;
        }

        seq.add(root.val);

        for (Node child : root.children) {
            preorder(child, seq);
        }
    }
}
