package algorithm.acm.math;

public class FiboDiv {

    // f(0) = 7
    // f(1) = 11
    // f(n) = f(n-1) + f(n-2)
    // f(n) 能否被 3 整除

    // f(n) % 3 = (f(n-1) + f(n-2)) % 3
    // f(n) % 3 = f(n-1) % 3 + f(n-2) % 3
    // 规律
    // 7, 11, 18, 29, 47, 76, 123, 199
    // 1,  2,  0,  2,  2,  1,   0,   1, (1, 2) 开始循环

    // 理论上说，相邻的两项，有 3 * 3 中可能性，则在 10 组内，必然出现重复
    public boolean div (int n) {
        return false;
    }
}
