package algorithm.structure.sequence;

public class LinkedList<T> {

    private Node<T> head = null;

    public Node<T> findByValue(T e) {
        Node<T> p = head;

        while (p != null) {
            if (p.data.equals(e)) {
                break;
            }

            p = p.next;
        }

        return p;
    }

    public Node<T> findByIndex(int index) {
        Node<T> p = head;
        int pos = 0;

        while (p != null && pos < index) {
            pos++;
            p = p.next;
        }

        return p;
    }

    public void insertToHead(Node<T> newNode) {
        newNode.next = head;
        head = newNode;
    }

    public void insertToHead(T e) {
        Node<T> node = new Node<>(e, null);
        insertToHead(node);
    }

    public void insertToTail(Node<T> newNode) {
        if (head == null) {
            head = newNode;
        } else {
            Node<T> p = head;

            while (p.next != null) {
                p = p.next;
            }

            p.next = newNode;
        }
    }

    public void insertToTail(T e) {
        Node<T> node = new Node<>(e, null);
        insertToTail(node);
    }

    public void insertAfter(Node<T> p, Node<T> newNode) {
        if (p == null) {
            return;
        }

        newNode.next = p.next;
        p.next = newNode;
    }

    public void insertAfter(Node<T> p, T e) {
        Node<T> newNode = new Node<>(e, null);
        insertAfter(p, newNode);
    }

    public void insertBefore(Node<T> p, Node<T> newNode) {
        if (p == null || head == null) {
            return;
        }

        if (p == head) {
            insertToHead(newNode);
            return;
        }

        Node<T> pp = head;

        while (pp != null && pp.next != p) {
            pp = pp.next;
        }

        if (pp != null) {
            newNode.next = p;
            pp.next = newNode;
        }
    }

    public void insertBefore(Node<T> p, T e) {
        Node<T> newNode = new Node<>(e, null);
        insertBefore(p, newNode);
    }

    public void deleteByNode(Node<T> p) {
        if (p == null || head == null) {
            return;
        }

        if (p == head) {
            head = head.next;
            return;
        }

        Node<T> pp = head;

        while (pp != null && pp.next != p) {
            pp = pp.next;
        }

        if (pp != null) {
            pp.next = pp.next.next;
        }
    }

    public void deleteByValue(T e) {
        if (head == null) {
            return;
        }

        if (head.data.equals(e)) {
            head = head.next;
            return;
        }

        Node<T> pp = head;

        while (pp.next != null) {

            if (pp.next.data.equals(e)) {
                break;
            }

            pp.next = pp;
        }

        if (pp.next != null) {
            pp.next = pp.next.next;
        }
    }

    public void printAll() {
        Node<T> p = head;

        while (p != null) {
            System.out.print(p.data + " ");
            p = p.next;
        }
        System.out.println();
    }

    public static class Node<T> {
        private T data;
        private Node<T> next;

        public Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }

        public T getData() {
            return data;
        }
    }

    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();

        list.insertToTail("java");
        list.insertToTail("php");
        list.insertToTail("c++");
        list.insertToTail("python");
        list.insertToTail("c");
        list.insertToTail("scala");

        list.printAll();
    }
}
