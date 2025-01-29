package algorithm.leetcode.bit;

public class Test {

    // 1 << 48 不符合预期，因为 1 是一个 int 类型，只有 32 位
    // 1L << 48
    // num & (1L << 48) 结果根据是否为 0 确定对应的位置是否位 1

    // 二进制的负数设计原因，主要是使得非负数与非负数、非负数与负数、负数与负数的加法逻辑，保持一致
    // 不需要额外根据不同情况跳转
}
