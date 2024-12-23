package algorithm.leetcode.queue;

import algorithm.leetcode.Question;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class DesignCircularQueue {

    int cap;
    int[] data;
    int front;
    int rear;
    int size;

    // 牺牲一个存储单元
    // (rear − front + capacity) mod capacity

    public DesignCircularQueue(int k) {
        cap = k;
        data = new int[cap];
        front = 0;
        rear = 0;
        size = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }

        data[rear] = value;
        rear = (rear == cap - 1) ? 0 : rear + 1;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }

        front = (front == cap - 1) ? 0 : front + 1;
        size--;
        return true;
    }

    public int Front() {
        if (isEmpty()) {
            return -1;
        }

        return data[front];
    }

    public int Rear() {
        if (isEmpty()) {
            return -1;
        }

        return data[rear == 0 ? cap - 1 : rear - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == cap;
    }

    public static class LinkedCircularQueue {

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

        ListNode head;
        ListNode tail;
        int size;
        int cap;

        public LinkedCircularQueue(int k) {
            cap = k;
            size = 0;
            head = null;
            tail = null;
        }

        public boolean enQueue(int value) {
            if (isFull()) {
                return false;
            }

            ListNode node = new ListNode(value);

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }

            size++;
            return true;
        }

        public boolean deQueue() {
            if (isEmpty()) {
                return false;
            }

            head = head.next;
            size--;
            return true;
        }

        public int Front() {
            if (isEmpty()) {
                return -1;
            }

            return head.val;
        }

        public int Rear() {
            if (isEmpty()) {
                return -1;
            }

            return tail.val;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean isFull() {
            return size == cap;
        }
    }

    public static class Queue1 {

        // java 中的双向链表 LinkedList
        // 单向链表就足够了
        public Queue<Integer> queue = new LinkedList<>();

        // 调用任何方法之前，先调用这个方法来判断队列内是否有东西
        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void offer(int num) {
            queue.offer(num);
        }

        // 从队列拿，从头拿
        public int poll() {
            return queue.poll();
        }

        // 返回队列头的元素但是不弹出
        public int peek() {
            return queue.peek();
        }

        // 返回目前队列里有几个数
        public int size() {
            return queue.size();
        }

    }

    // 如果可以确定加入操作的总次数不超过 n，那么可以用
    public static class Queue2 {

        public int[] queue;
        public int l;
        public int r;

        // 加入操作的总次数上限是多少，一定要明确
        public Queue2(int n) {
            queue = new int[n];
            l = 0;
            r = 0;
        }

        // 调用任何方法之前，先调用这个方法来判断队列内是否有东西
        public boolean isEmpty() {
            return l == r;
        }

        public void offer(int num) {
            queue[r++] = num;
        }

        public int poll() {
            return queue[l++];
        }

        // [l..r)
        public int head() {
            return queue[l];
        }

        public int tail() {
            return queue[r - 1];
        }

        public int size() {
            return r - l;
        }

    }

    // 直接用 java 内部的实现
    // 其实就是动态数组，不过常数时间并不好
    public static class Stack1 {

        public Stack<Integer> stack = new Stack<>();

        // 调用任何方法之前，先调用这个方法来判断栈内是否有东西
        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public void push(int num) {
            stack.push(num);
        }

        public int pop() {
            return stack.pop();
        }

        public int peek() {
            return stack.peek();
        }

        public int size() {
            return stack.size();
        }

    }

    // 实际刷题时更常见的写法，常数时间好
    // 如果可以保证同时在栈里的元素个数不会超过 n，那么可以用
    // 也就是发生弹出操作之后，空间可以复用
    // 一般笔试、面试都会有一个明确数据量，所以这是最常用的方式
    public static class Stack2 {

        public int[] stack;
        public int size;

        // 同时在栈里的元素个数不会超过 n
        public Stack2(int n) {
            stack = new int[n];
            size = 0;
        }

        // 调用任何方法之前，先调用这个方法来判断栈内是否有东西
        public boolean isEmpty() {
            return size == 0;
        }

        public void push(int num) {
            stack[size++] = num;
        }

        public int pop() {
            return stack[--size];
        }

        public int peek() {
            return stack[size - 1];
        }

        public int size() {
            return size;
        }
    }
}
