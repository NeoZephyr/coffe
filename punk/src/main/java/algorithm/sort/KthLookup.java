package algorithm.sort;

public class KthLookup {
    public int lookup(int[] data, int k) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("data is empty");
        }

        if (k <= 0 || k > data.length) {
            throw new RuntimeException("k is invalid");
        }

        int p;
        int start = 0;
        int end = data.length - 1;

        while (true) {
            p = partition(data, start, end);

            if ((p + 1) == k) {
                break;
            }

            if ((p + 1) < k) {
                start = p + 1;
            } else {
                end = p - 1;
            }
        }

        return data[p];
    }

    private int partition(int[] data, int start, int end) {
        int pivotValue = data[end];

        int i = start;
        int j = start;

        while (j < end) {
            if (data[j] < pivotValue) {
                int tmp = data[i];
                data[i] = data[j];
                data[j] = tmp;

                ++i;
            }

            ++j;
        }

        data[j] = data[i];
        data[i] = pivotValue;

        return i;
    }
}
