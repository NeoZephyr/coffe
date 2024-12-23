package algorithm.leetcode;

import org.springframework.boot.autoconfigure.SpringBootApplication;

public class Question {

    static class ListNode {
        int val;
        ListNode next;

        public ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        public ListNode(int val) {
            this.val = val;
            this.next = null;
        }
    }

    public ListNode remove(ListNode head, int val) {
        ListNode h = null, e = null, cur = head;

        while (cur != null) {
            if (cur.val == val) {
                cur = cur.next;
                continue;
            }

            ListNode tmp = cur.next;
            cur.next = null;

            if (h == null) {
                h = cur;
                e = cur;
            } else {
                e.next = cur;
                e = e.next;
            }

            cur = tmp;
        }

        return h;
    }

    public static void main(String[] args) {
        // 1,2,6,3,4,5,6
        ListNode h = new ListNode(1,
                new ListNode(2,
                        new ListNode(6,
                                new ListNode(3,
                                        new ListNode(4,
                                                new ListNode(5,
                                                        new ListNode(6)))))));
        print(h);
        print(new Question().remove(h, 6));
        print(new Question().remove(null, 1));
        h = new ListNode(7, new ListNode(7, new ListNode(7, new ListNode(7))));
        print(h);
        print(new Question().remove(h, 7));

    }

    private static void print(ListNode h) {
        while (h != null) {
            System.out.print(h.val + ", ");
            h = h.next;
        }
        System.out.println();
    }


}
