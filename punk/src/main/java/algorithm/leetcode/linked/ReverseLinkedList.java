package algorithm.leetcode.linked;

public class ReverseLinkedList {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public ListNode reverseList1(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode nextTemp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextTemp;
        }
        return prev;
    }

    /**
     * 1 -> 2 -> 3 -> 4 -> 5 -> null
     *
     * 1 -> 2 -> 3 -> 4         null <- 4 <- 5
     * 1 -> 2 -> 3              null <- 3 <- 4 <- 5
     * 1 -> 2                   null <- 2 <- 3 <- 4 <- 5
     *                          null <- 1 <- 2 <- 3 <- 4 <- 5
     */
    public ListNode reverseList2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // 这个 p 就是之前正序的第一个节点
        ListNode p = reverseList2(head.next);

        // 将反转链表的尾结点 head.next 的 next 指向当前即将反转的节点
        head.next.next = head;

        // 让当前节点变成反转链表的尾结点
        head.next = null;

        return p;
    }

    /**
     * 1 -> 2 -> 3 -> 4 -> 5 -> null
     *
     * 1 -> null                       2 -> 3 -> 4 -> 5 -> null
     * 2 -> 1 -> null                  3 -> 4 -> 5 -> null
     * 3 -> 2 -> 1 -> null             4 -> 5 -> null
     * 4 -> 3 -> 2 -> 1 -> null        5 -> null
     * 5 -> 4 -> 3 -> 2 -> 1 -> null
     */
    public ListNode reverseList3(ListNode head) {
        return doReverseList(null, head);
    }

    public ListNode doReverseList(ListNode reversed, ListNode remain) {
        if (remain == null) {
            return reversed;
        }

        ListNode newRemain = remain.next;
        remain.next = reversed;
        return doReverseList(remain, newRemain);
    }
}
