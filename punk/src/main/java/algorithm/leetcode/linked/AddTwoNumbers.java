package algorithm.leetcode.linked;

public class AddTwoNumbers {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode ans = null, cur = null;
        int carry = 0;

        for (int sum, val;
             l1 != null || l2 != null;
             l1 = l1 == null ? null : l1.next, l2 = l2 == null ? null : l2.next) {
            sum = (l1 == null ? 0 : l1.val) + (l2 == null ? 0 : l2.val) + carry;
            val = sum % 10;
            carry = sum / 10;

            if (ans == null) {
                ans = new ListNode(val);
                cur = ans;
            } else {
                cur.next = new ListNode(val);
                cur = cur.next;
            }
        }

        if (carry == 1) {
            cur.next = new ListNode(1);
        }

        return ans;
    }

    public ListNode addTwoNumbers1(ListNode l1, ListNode l2) {
        return add(l1, l2, 0);
    }

    public ListNode add(ListNode l1, ListNode l2, int carry) {
        if (l1 == null && l2 == null) {
            if (carry != 0) {
                return new ListNode(carry);
            }
            return null;
        }

        int sum = (l1 == null ? 0 : l1.val) + (l2 == null ? 0 : l2.val) + carry;
        ListNode node = new ListNode(sum % 10);
        carry = sum / 10;
        node.next = add(l1 == null ? null : l1.next, l2 == null ? null : l2.next, carry);
        return node;
    }
}
