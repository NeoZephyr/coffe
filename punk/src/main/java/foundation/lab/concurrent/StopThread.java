package foundation.lab.concurrent;

public class StopThread {
    public static void main(String[] args) throws InterruptedException {
        interruptBlockedThread();
    }


    // public void interrupt()                 Thread 实例方法，用于设置中断标记，但是不能立即中断线程
    // public boolean isInterrupted()          Thread 实例方法，用于获取当前线程的中断标记
    // public static boolean interrupted()     Thread 静态方法，用于获取当前线程的中断标记，并且会清除中断标记


    // 如果线程阻塞在支持中断的方法上，会立即结束阻塞，并向外抛出 InterruptedException
    // 如果线程没有阻塞在支持中断的方法上，则该方法不能立即停止线程

    // JUC 类库中的所有阻塞队列、锁、Object 的 wait 等方法都支持中断

    private static void interruptBlockedThread() throws InterruptedException {
        Runnable run = () -> {
            int count = 0;

            while (!Thread.currentThread().isInterrupted() && count <= 1000) {
                count++;

                if (count % 100 == 0) {
                    try {
                        System.out.println("count: " + count);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        Thread thread = new Thread(run);
        thread.start();
        Thread.sleep(5000);
        thread.interrupt();
    }
}
