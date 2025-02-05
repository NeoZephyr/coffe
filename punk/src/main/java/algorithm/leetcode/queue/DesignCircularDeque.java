package algorithm.leetcode.queue;

import java.util.Deque;
import java.util.LinkedList;

public class DesignCircularDeque {

    private int limit;
    private int size;
    private Deque<Integer> queue;

    public DesignCircularDeque(int k) {
        limit = k;
        size = 0;
        queue = new LinkedList<>();
    }

    public boolean insertFront(int value) {
        if (isFull()) {
            return false;
        }

        size++;
        queue.offerFirst(value);
        return true;
    }

    public boolean insertLast(int value) {
        if (isFull()) {
            return false;
        }

        size++;
        queue.offerLast(value);
        return true;
    }

    public boolean deleteFront() {
        if (isEmpty()) {
            return false;
        }

        size--;
        queue.pollFirst();
        return true;
    }

    public boolean deleteLast() {
        if (isEmpty()) {
            return false;
        }

        size--;
        queue.pollLast();
        return true;
    }

    public int getFront() {
        if (isEmpty()) {
            return -1;
        }

        return queue.peekFirst();
    }

    public int getRear() {
        if (isEmpty()) {
            return -1;
        }

        return queue.peekLast();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == limit;
    }

    static class Queue {
        private int limit;
        private int front;
        private int rear;
        private int size;
        private int[] data;

        public Queue(int k) {
            limit = k;
            size = 0;
            data = new int[k];
        }

        public boolean insertFront(int value) {
            if (isFull()) {
                return false;
            }

            if (isEmpty()) {
                front = 0;
                rear = 0;
                data[0] = value;
            } else {
                front = front == 0 ? limit - 1 : front - 1;
                data[front] = value;
            }

            size++;
            return true;
        }

        public boolean insertLast(int value) {
            if (isFull()) {
                return false;
            }

            if (isEmpty()) {
                front = 0;
                rear = 0;
                data[0] = value;
            } else {
                rear = rear == limit - 1 ? 0 : rear + 1;
                data[rear] = value;
            }

            size++;
            return true;
        }

        public boolean deleteFront() {
            if (isEmpty()) {
                return false;
            }

            front = front == limit - 1 ? 0 : front + 1;
            size--;
            return true;
        }

        public boolean deleteLast() {
            if (isEmpty()) {
                return false;
            }

            rear = rear == 0 ? limit - 1 : rear - 1;
            size--;
            return true;
        }

        public int getFront() {
            if (isEmpty()) {
                return -1;
            }

            return data[front];
        }

        public int getRear() {
            if (isEmpty()) {
                return -1;
            }

            return data[rear];
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean isFull() {
            return size == limit;
        }
    }
}
