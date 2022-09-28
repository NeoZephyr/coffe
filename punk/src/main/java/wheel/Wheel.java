package wheel;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Wheel {
    Integer round = 1;
    int ticks;
    LinkedList<Timer>[] slots;

    public Wheel(Integer round, int ticks) {
        this.round = round;
        this.ticks = ticks;
        this.slots = new LinkedList[ticks];

        for (int i = 0; i < ticks; i++) {
            slots[i] = new LinkedList<>();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(1);
        Thread.sleep(1);

        // System.out.println(TimeUnit.MILLISECONDS.toNanos(1));
        // LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
        // LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
        long end = System.currentTimeMillis();

        System.out.println(end);
        System.out.println(start);
        System.out.println(end - start);
    }
}
