package foundation.lab.concurrent;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExitTest {

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
                Set<Thread> threads = traces.keySet();
                String threadInfo = threads.stream().map(x -> x.getName()).collect(Collectors.joining(" | "));
                System.out.printf("=== t1 running %s ===\n", threadInfo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        new Thread(() -> {
            System.out.println("=== t2 running ===");
            try {
                Thread.sleep(10000);
                System.exit(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t2").start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("=== TERMINATING ===")));
    }
}
