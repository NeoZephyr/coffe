package wheel;

import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Ticker {
    private Integer interval;

    private static final int RT_BITS = 8;
    private static final int NT_BITS = 6;

    private static final int RT_SIZE = 1 << 8;
    private static final int NT_SIZE = 1 << 6;

    private static final int RT_MASK = RT_SIZE - 1;
    private static final int NT_MASK = NT_SIZE - 1;

    private long jiffy;
    private volatile boolean stop;

    Wheel w1 = new Wheel(1, RT_SIZE);
    Wheel w2 = new Wheel(2, NT_SIZE);
    Wheel w3 = new Wheel(3, NT_SIZE);
    Wheel w4 = new Wheel(4, NT_SIZE);
    Wheel w5 = new Wheel(5, NT_SIZE);

    public void start() {

        while (!stop) {
            // select for 1 us
            runTick();
        }
    }

    public void runTick() {
        long jiffy = getJiffy();

        while (jiffy >= this.jiffy) {

            do {
                int index = (int) (jiffy & RT_MASK);

                if (index != 0) {
                    break;
                }

                index = (int) ((jiffy >> RT_BITS) & NT_MASK);

                if (index != 0) {
                    cascadeTick(w2, index);
                    break;
                }

                index = (int) ((jiffy >> (RT_BITS + NT_BITS)) & NT_MASK);

                if (index != 0) {
                    cascadeTick(w3, index);
                    break;
                }

                index = (int) ((jiffy >> (RT_BITS + NT_BITS * 2)) & NT_MASK);

                if (index != 0) {
                    cascadeTick(w4, index);
                    break;
                }

                index = (int) ((jiffy >> (RT_BITS + NT_BITS * 3)) & NT_MASK);

                if (index != 0) {
                    cascadeTick(w5, index);
                    break;
                }
            } while (false);

            LinkedList<Timer> timers = w1.slots[1];
            for (Timer timer : timers) {
                if (timer.period != 0) {
                    timer.expire = this.jiffy + timer.period;
                    addTimer(timer);
                }

                timer.runner.run();
            }

            this.jiffy++;
        }
    }

    public void cascadeTick(Wheel w, int index) {
        LinkedList<Timer> timers = w.slots[index];
        w.slots[index] = new LinkedList<>();

        for (Timer timer : timers) {
            // 重新加入
            addTimer(timer);
        }
    }

    public void addTimer(Timer timer) {
        long expire = timer.expire;
        long delta = expire - jiffy;
        int index;
        LinkedList slot;

        if (delta < 0) { // 过去时间
            index = (int) (jiffy & RT_MASK);
            slot = w1.slots[index];
        } else if (delta < RT_SIZE) { // 8
            index = (int) (expire & RT_MASK);
            slot = w1.slots[index];
        } else if (delta < (1 << (RT_BITS + NT_BITS))) { // 8 + 6
            index = (int) ((expire >> RT_BITS) & NT_MASK);
            slot = w2.slots[index];
        } else if (delta < (1 << (RT_BITS + 2 * NT_BITS))) { // 8 + 6 + 6
            index = (int) ((expire >> (RT_BITS + NT_BITS)) & NT_MASK);
            slot = w3.slots[index];
        } else if (delta < (1 << (RT_BITS + 3 * NT_BITS))) { // 8 + 6 + 6 + 6
            index = (int) ((expire >> (RT_BITS + 2 * NT_BITS)) & NT_MASK);
            slot = w4.slots[index];
        } else {
            index = (int) (expire >> (RT_BITS + 3 * NT_BITS) & NT_MASK);
            slot = w5.slots[index];
        }

        slot.addLast(timer);
    }

    public long getJiffy() {
        return 0;
    }

//    void SleepMilliseconds(uint32 uMs)
//    {
//        struct timeval tv;
//        tv.tv_sec = 0;
//        tv.tv_usec = uMs * 1000;  // tv.tv_usec 单位是微秒
//        select(0, NULL, NULL, NULL, &tv);
//    }


//    static uint32 GetJiffies(void)
//    {
//        struct timespec ts;  // 精确到纳秒（10 的 -9 次方秒）
//        // 使用 clock_gettime 函数时，有些系统需连接 rt 库，加 -lrt 参数，有些不需要连接 rt 库
//        clock_gettime(CLOCK_MONOTONIC, &ts);  // 获取时间。其中，CLOCK_MONOTONIC 表示从系统启动这一刻起开始计时,不受系统时间被用户改变的影响
//        return (ts.tv_sec * 1000 + ts.tv_nsec / 1000000);  // 返回毫秒时间
//    }

    public static void main(String[] args) {
        log.error("hello");
    }
}