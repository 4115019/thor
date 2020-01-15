/**
 * @author huangpin
 * @date 2019-12-27
 */
public class D20191227L2 {
    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int add = 0;
        ListNode result = null;
        ListNode tmp = null;
        while (l1 != null || l2 != null || add != 0) {
            int sum = add;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            add = sum / 10;
            sum %= 10;

            if (tmp == null) {
                tmp = new ListNode(sum);
                result = tmp;
            } else {
                tmp.next = new ListNode(sum);
                tmp = tmp.next;
            }
        }
        return result;
    }

}
