package algorithm.structure.sequence;

public class LinkedStack {
    private Node top = null;

    public void push(String value) {
        Node node = new Node(value, null);

        if (top != null) {
            node.next = top;
        }

        top = node;
    }

    public String pop() {
        if (top == null) {
            return null;
        }

        String res = top.data;
        top = top.next;

        return res;
    }

    private static class Node {
        private String data;
        private Node next;

        public Node(String data, Node next) {
            this.data = data;
            this.next = next;
        }

        public String getData() {
            return data;
        }
    }
}
