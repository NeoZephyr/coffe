package algorithm.leetcode.linked;

public class PartitionList {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    public ListNode partition(ListNode head, int x) {
        ListNode rightHead = null, rightTail = null;
        ListNode leftHead = null, leftTail = null;

        while (head != null) {
            ListNode next = head.next;
            head.next = null;

            if (head.val < x) {
                if (rightHead == null) {
                    rightHead = head;
                } else {
                    rightTail.next = head;
                }

                rightTail = head;
            } else {
                if (leftHead == null) {
                    leftHead = head;
                } else {
                    leftTail.next = head;
                }

                leftTail = head;
            }

            head = next;
        }

        if (rightHead == null) {
            return leftHead;
        } else {
            rightTail.next = leftHead;
            return rightHead;
        }
    }
}
