package algorithm.structure.sequence;

public class Array<T> {
    public Object[] data;
    public int capacity;
    public int count;

    public Array(int capacity) {
        data = new Object[capacity];
        this.capacity = capacity;
        this.count = 0;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= count) {
            throw new RuntimeException("invalid index");
        }

        return (T) data[index];
    }

    public boolean insert(int index, T value) {
        if (count == capacity) {
            return false;
        }

        if (index < 0 || index > count) {
            return false;
        }

        int pos = count - 1;

        while (pos >= index) {
            data[pos + 1] = data[pos];
            pos--;
        }

        count++;
        data[index] = value;

        return true;
    }

    public boolean delete(int index) {
        if (count == 0) {
            return false;
        }

        if (index < 0 || index >= count) {
            return false;
        }

        int pos = index + 1;

        while (pos < count) {
            data[pos - 1] = data[pos];
        }

        count--;
        return true;
    }

    public void printAll() {
        for (int i = 0; i < count; ++i) {
            System.out.print(data[i] + " ");
        }

        System.out.println();
    }

    public static void main(String[] args) {
        Array<Integer> array = new Array<>(5);
        array.insert(0, 4);
        array.insert(0, 5);
        array.insert(1, 9);
        array.insert(1, 10);
        array.insert(2, 12);
        array.printAll();

        int[] arr = {4, 2, 3, 5, 9, 6};
        System.out.println(find1(arr, 6));
        System.out.println(find2(arr, 6));
        System.out.println(find1(arr, 7));
        System.out.println(find2(arr, 7));
    }

    static int find1(int[] arr, int key) {
        if (arr == null || arr.length == 0) {
            return -1;
        }

        int i = 0;

        // 这里有两个比较操作：i < n 和 a[i] == key
        while (i < arr.length) {
            if (arr[i] == key) {
                return i;
            }
            ++i;
        }

        return -1;
    }

    static int find2(int[] arr, int key) {
        if (arr == null || arr.length == 0) {
            return -1;
        }

        if (arr[arr.length - 1] == key) {
            return arr.length - 1;
        }

        int tmp = arr[arr.length - 1];
        arr[arr.length - 1] = key;

        int i = 0;

        // 少了 i < n 这个比较操作
        while (arr[i] != key) {
            ++i;
        }

        arr[arr.length - 1] = tmp;

        if (i == arr.length - 1) {
            return -1;
        } else {
            return i;
        }
    }

}
