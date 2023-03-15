package com.pain.apple.lab.id;

public class IdGenerator {

    // 2021-01-01 00:00:00
    private static final long START_TIME = 1609430400000L;

    // 序列号占用的位数
    private static final long SEQUENCE_BIT = 12;

    // 机器标识占用的位数
    private static final long MACHINE_BIT = 5;

    // 数据中心占用的位数
    private static final long DATACENTER_BIT = 5;

    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

    private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

    private static final long MACHINE_LEFT = SEQUENCE_BIT;

    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    private static final long TIME_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long dataCenterId = 1;
    private long machineId = 1;
    private long sequence = 0L;
    private long lastTime = -1L;

    public IdGenerator() {}

    public IdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATACENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }

        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }

        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized long gen() {
        long currentTime = getCurrentTime();

        if (currentTime < lastTime) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (currentTime == lastTime) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            if (sequence == 0L) {
                currentTime = getNextMill();
            }
        } else {
            sequence = 0L;
        }

        lastTime = currentTime;

        return (currentTime - START_TIME) << TIME_LEFT
                | dataCenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextMill() {
        long currentTime = getCurrentTime();

        while (currentTime <= lastTime) {
            currentTime = getCurrentTime();
        }
        return currentTime;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
