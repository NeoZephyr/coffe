package algorithm.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class NaryTreePostorderTraversal {

    public List<Integer> postorder(Node root) {
        List<Integer> seq = new ArrayList<>();
        postorder(root, seq);
        return seq;
    }

    public List<Integer> postorder1(Node root) {
        List<Integer> seq = new ArrayList<>();
        Deque<Node> queue = new LinkedList<>();

        if (root == null) {
            return seq;
        }

        queue.push(root);
        Node prev = null;

        while (!queue.isEmpty()) {
            root = queue.pop();

            if (root.children.size() == 0) {
                seq.add(root.val);
                prev = root;
            } else {
                int count = root.children.size();

                if (prev != root.children.get(count - 1)) {
                    queue.push(root);

                    for (int i = count - 1; i >= 0; i--) {
                        queue.push(root.children.get(i));
                    }
                } else {
                    seq.add(root.val);
                    prev = root;
                }
            }
        }

        return seq;
    }

    private void postorder(Node root, List<Integer> seq) {
        if (root == null) {
            return;
        }

        for (Node child : root.children) {
            postorder(child, seq);
        }

        seq.add(root.val);
    }
}
