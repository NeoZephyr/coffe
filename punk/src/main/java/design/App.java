package design;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class App {

    ArrayDeque<Message<String>> queue = new ArrayDeque<>();

    int cap;
    ReentrantLock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();

    public App(int cap) {
        if (cap <= 0) {
            throw new IllegalArgumentException("cap must greater than 0");
        }

        this.cap = cap;
    }

    public void produce(Message<String> message) {

        try {
            lock.lock();

            while (queue.size() >= cap) {
                notFull.await();
            }

            queue.addLast(message);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            // ignore
        } finally {
            lock.unlock();
        }
    }

    public void consume() {
        Message<String> message = null;

        try {
            lock.lock();

            while (queue.isEmpty()) {
                notEmpty.await();
            }

            message = queue.pollFirst();
            notFull.signalAll();
        } catch (InterruptedException e) {
            // ignore
        } finally {
            lock.unlock();
        }

        if (message != null) {
            // process message
        }
    }
}

class Message<T> {
    T data;

    public Message(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data=" + data +
                '}';
    }
}