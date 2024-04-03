package algorithm.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class NaryTreeLevelorderTraversal {

    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> seq = new ArrayList<>();
        Deque<Node> queue = new LinkedList<>();

        if (root == null) {
            return seq;
        }

        queue.add(root);

        while (!queue.isEmpty()) {
            int count = queue.size();
            List<Integer> subSeq = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                root = queue.poll();
                subSeq.add(root.val);

                for (Node child : root.children) {
                    queue.offer(child);
                }
            }

            seq.add(subSeq);
        }
        return seq;
    }
}
