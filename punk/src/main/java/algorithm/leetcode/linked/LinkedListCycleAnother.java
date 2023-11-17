package algorithm.leetcode.linked;

import java.util.HashSet;
import java.util.Set;

public class LinkedListCycleAnother {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public ListNode detectCycle1(ListNode head) {
        Set<ListNode> visitedNodes = new HashSet<>();

        while (head != null) {
            if (visitedNodes.contains(head)) {
                return head;
            }
            visitedNodes.add(head);
            head = head.next;
        }

        return null;
    }

    public ListNode detectCycle2(ListNode head) {
        ListNode slow = head;
        ListNode quick = head;

        while (quick != null && quick.next != null) {
            slow = slow.next;
            quick = quick.next.next;

            if (slow == quick) {
                break;
            }
        }

        if (quick == null || quick.next == null) {
            return null;
        }

        slow = head;

        while (slow != quick) {
            slow = slow.next;
            quick = quick.next;
        }

        return slow;
    }
}
