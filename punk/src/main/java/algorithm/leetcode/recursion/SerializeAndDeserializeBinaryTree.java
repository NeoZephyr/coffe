package algorithm.leetcode.recursion;

import algorithm.leetcode.tree.TreeNode;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class SerializeAndDeserializeBinaryTree {

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if (root == null) {
            return "()";
        }

        return String.format("(%d%s%s)", root.val, serialize(root.left), serialize(root.right));
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        Object[] node = deserialize(data, 0);
        return (TreeNode) node[0];
    }

    // 通常使用的前序、中序、后序、层序遍历记录的二叉树的信息不完整，即唯一的输出序列可能对应着多种二叉树可能性
    // 因此，序列化的字符串应携带完整的二叉树信息
    // 为完整表示二叉树，考虑将叶节点下的 null 也记录
    // 在此基础上，对于列表中任意某节点 node，其左子节点 node.left 和右子节点 node.right 在序列中的位置都是唯一确定的
    public String serialize1(TreeNode root) {
        if (root == null) {
            return "#";
        }

        return String.format("%d,%s,%s", root.val, serialize(root.left), serialize(root.right));
    }

    public TreeNode deserialize1(String data) {
        Deque<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserialize(queue);
    }

    public String serialize2(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        Deque<TreeNode> queue = new LinkedList<>();
        queue.push(root);

        while (!queue.isEmpty()) {
            root = queue.poll();

            if (root == null) {
                sb.append("#,");
            } else {
                sb.append(root.val).append(",");
                queue.offer(root.left);
                queue.offer(root.right);
            }
        }

        return sb.substring(0, sb.length() - 1);
    }

    public TreeNode deserialize2(String data) {
        List<String> list = new LinkedList<>(Arrays.asList(data.split(",")));
        Deque<TreeNode> queue = new LinkedList<>();

        if ("#".equals(list.get(0))) {
            return null;
        }

        TreeNode root = new TreeNode();
        root.val = Integer.parseInt(list.get(0));
        queue.offer(root);
        int i = 1;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if ("#".equals(list.get(i))) {
                node.left = null;
            } else {
                TreeNode left = new TreeNode();
                left.val = Integer.parseInt(list.get(i));
                node.left = left;
                queue.offer(left);
            }

            i++;

            if ("#".equals(list.get(i))) {
                node.right = null;
            } else {
                TreeNode right = new TreeNode();
                right.val = Integer.parseInt(list.get(i));
                node.right = right;
                queue.offer(right);
            }

            i++;
        }

        return root;
    }

    private Object[] deserialize(String data, int start) {
        int nextStart = data.indexOf('(', start + 1);
        int end = data.indexOf(")", start + 1);

        if (end - start == 1) {
            return new Object[]{null, end + 1};
        }

        int val = Integer.parseInt(data.substring(start + 1, nextStart));
        TreeNode node = new TreeNode();
        node.val = val;

        Object[] left = deserialize(data, nextStart);
        node.left = (TreeNode) left[0];
        Object[] right = deserialize(data, (Integer) left[1]);
        node.right = (TreeNode) right[0];
        int rightEnd = (int) right[1];
        return new Object[]{node, rightEnd + 1};
    }

    private TreeNode deserialize(Deque<String> queue) {
        String item = queue.poll();

        if ("#".equals(item)) {
            return null;
        }

        TreeNode node = new TreeNode();
        node.val = Integer.parseInt(item);
        node.left = deserialize(queue);
        node.right = deserialize(queue);
        return node;
    }
}
