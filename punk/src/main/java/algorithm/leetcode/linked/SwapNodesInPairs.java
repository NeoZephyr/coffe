package algorithm.leetcode.linked;

public class SwapNodesInPairs {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public ListNode swapPairs1(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // 维护三个指针
        ListNode prev = null;
        ListNode prev1 = head;
        ListNode prev2 = head.next;

        head = prev2;

        while (prev1 != null && prev2 != null) {
            if (prev != null) {
                prev.next = prev2;
            }

            prev1.next = prev2.next;
            prev2.next = prev1;

            if (prev1.next == null) {
                break;
            }

            prev = prev1;
            prev1 = prev1.next;
            prev2 = prev1.next;
        }

        return head;
    }

    public ListNode swapPairs2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode dummy = new ListNode(-1);
        dummy.next = head;

        ListNode prev = dummy;
        ListNode first = head;
        ListNode second = head.next;

        while (first != null && second != null) {
            first.next = second.next;
            second.next = first;
            prev.next = second;

            if (first.next == null) {
                break;
            }

            prev = first;
            first = first.next;
            second = first.next;
        }

        return dummy.next;
    }

    public ListNode swapPairs3(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }

        ListNode p = swapPairs3(head.next.next);

        // 没有反转的跟反转的接上
        ListNode tmp = head.next;
        head.next = p;
        tmp.next = head;

        return tmp;
    }
}
