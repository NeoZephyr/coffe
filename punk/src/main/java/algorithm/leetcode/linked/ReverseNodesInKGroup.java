package algorithm.leetcode.linked;

public class ReverseNodesInKGroup {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public ListNode reverseKGroup1(ListNode head, int k) {
        if (head == null || head.next == null || k < 2) {
            return head;
        }

        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode prev = dummy;
        ListNode first = head;
        ListNode last = head;

        while (true) {
            for (int i = 0; i < k; ++i) {

                if (last == null) {
                    return dummy.next;
                }
                last = last.next;
            }

            ListNode nextPrev = first;

            for (int i = 0; i < k; ++i) {
                ListNode tmp = first.next;
                prev.next = first;
                first.next = last;
                last = first;
                first = tmp;
            }

            prev = nextPrev;
            last = first;
        }
    }

    public ListNode reverseKGroup2(ListNode head, int k) {
        ListNode tail = head;

        for (int i = 0; i < k; ++i) {
            if (tail == null) {
                return head;
            }

            tail = tail.next;
        }

        ListNode newHead = reverse(head, tail);

        head.next = reverseKGroup2(tail, k);

        return newHead;
    }

    private ListNode reverse(ListNode head, ListNode tail) {
        ListNode newHead = null;

        while (head != tail) {
            ListNode tmp = head.next;

            head.next = newHead;
            newHead = head;
            head = tmp;
        }

        return newHead;
    }
}
