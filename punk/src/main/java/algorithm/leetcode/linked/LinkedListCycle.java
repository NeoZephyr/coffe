package algorithm.leetcode.linked;

import java.util.HashSet;
import java.util.Set;

public class LinkedListCycle {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public boolean hasCycle1(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode quick = head.next.next;

        // 其实，值需要检查 quick 是否为 null 即可
        while (slow != null && quick != null) {
            if (slow == quick) {
                return true;
            }

            slow = slow.next;

            if (quick.next == null) {
                return false;
            }

            quick = quick.next.next;
        }

        return false;
    }

    public boolean hasCycle2(ListNode head) {
        Set<ListNode> visitedNodes = new HashSet<>();

        while (head != null) {
            if (visitedNodes.contains(head)) {
                return true;
            }

            visitedNodes.add(head);
            head = head.next;
        }

        return false;
    }

    public boolean hasCycle3(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode quick = head.next;

        while (slow != quick) {
            if (quick == null || quick.next == null) {
                return false;
            }

            slow = slow.next;
            quick = quick.next.next;
        }

        return true;
    }

    public boolean hasCycle4(ListNode head) {

        ListNode slow = head;
        ListNode quick = head;

        while (quick != null && quick.next != null) {
            slow = slow.next;
            quick = quick.next.next;

            if (slow == quick) {
                return true;
            }
        }

        return false;
    }
}
