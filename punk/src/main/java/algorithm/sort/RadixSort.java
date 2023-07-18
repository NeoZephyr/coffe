package algorithm.sort;

public class RadixSort {
    /**
     * 假设有 10 万个手机号码需要从小到大进行排序。由于手机号码有 11 位，范围太大，不适合用桶排序、计数排序算法
     *
     * 操作步骤：
     * 假设要比较两个手机号码 a，b 的大小，如果在前面几位中，a 手机号码已经比 b 手机号码大了，那后面的几位就不用看了。先按照最后一位来
     * 排序手机号码，然后，再按照倒数第二位重新排序，以此类推，最后按照第一位重新排序。经过 11 次排序之后，手机号码就都有序了
     *
     * 这里按照每位来排序的排序算法要是稳定的，否则这个实现思路就是不正确的。因为如果是非稳定排序算法，那最后一次排序只会考虑最高位的大小
     * 顺序，完全不管其他位的大小关系，那么低位的排序就完全没有意义了
     *
     * 根据每一位来排序，可以用桶排序或者计数排序，时间复杂度可以做到 O(n)。如果要排序的数据有 k 位，那就需要 k 次桶排序或者计数排序，
     * 总的时间复杂度是 O(k * n)。当 k 不大的时候，所以时间复杂度就近似于 O(n)
     *
     * 实际上，有时候要排序的数据并不都是等长的，可以把所有的单词补齐到相同长度，位数不够的可以在后面补 0，因为根据 ASCII 值，所有字母都大于 0
     *
     * 基数排序条件：
     * 需要排序数据可以分割出独立的位来比较，而且位之间有递进的关系，如果 a 数据的高位比 b 数据大，那剩下的低位就不用比较了。除此之外，
     * 每一位的数据范围不能太大，要可以用线性排序算法来排序，否则，基数排序的时间复杂度就无法做到 O(n) 了
     */
}
