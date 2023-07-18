package algorithm.structure.sequence;

import algorithm.structure.utils.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;

public class BinarySearch {

    public static void main(String[] args) {
        // testSearch();
        // testSearchFirstEqual();
        // testSearchLastEqual();
        // testSearchFirstGreatOrEqual();
        testSearchLastLessOrEqual();
        System.out.println("Complete");
    }

    private static void testSearch() {
        BinarySearch binarySearch = new BinarySearch();
        boolean failed = false;

        for (int i = 0; i < 1000; ++i) {
            int[] data = ArrayUtils.genDistinctIntArray(1000, 0, 1000);
            Arrays.sort(data);

            for (int j = 0; j < data.length; ++j) {
                int target = data[j];
                // int pos = binarySearch.recursionSearch(data, target);
                int pos = binarySearch.nonRecursionSearch(data, target);

                if (pos != j) {
                    failed = true;
                    System.out.println("ERROR");
                    System.out.printf("data: %s\n", Arrays.toString(data));
                    System.out.printf("target: %d, actual position: %d, expect position: %d\n", target, pos, j);
                    break;
                }
            }

            if (failed) {
                break;
            }
        }
    }

    private static void testSearchFirstEqual() {
        BinarySearch binarySearch = new BinarySearch();
        boolean failed = false;

        for (int i = 0; i < 1000; ++i) {
            int[] data = ArrayUtils.genIntArray(1000, 0, 100);
            Arrays.sort(data);
            HashMap<Integer, Integer> dataToPos = new HashMap<>();

            for (int j = 0; j < data.length; ++j) {
                int target = data[j];
                if (!dataToPos.containsKey(target)) {
                    dataToPos.put(target, j);
                }

                int expectPos = dataToPos.get(target);
                int actualPos = binarySearch.searchFirstEqual(data, target);

                if (actualPos != expectPos) {
                    failed = true;
                    System.out.println("ERROR");
                    System.out.printf("data: %s\n", Arrays.toString(data));
                    System.out.printf("target: %d, actual position: %d, expect position: %d\n", target, actualPos, expectPos);
                    break;
                }
            }

            if (failed) {
                break;
            }
        }
    }

    private static void testSearchLastEqual() {
        BinarySearch binarySearch = new BinarySearch();
        boolean failed = false;

        for (int i = 0; i < 1000; ++i) {
            int[] data = ArrayUtils.genIntArray(1000, 0, 100);
            Arrays.sort(data);
            HashMap<Integer, Integer> dataToPos = new HashMap<>();

            for (int j = 0; j < data.length; ++j) {
                dataToPos.put(data[j], j);
            }

            for (int j = 0; j < data.length; ++j) {
                int target = data[j];
                int expectPos = dataToPos.get(target);
                int actualPos = binarySearch.searchLastEqual(data, target);

                if (actualPos != expectPos) {
                    failed = true;
                    System.out.println("ERROR");
                    System.out.printf("data: %s\n", Arrays.toString(data));
                    System.out.printf("target: %d, actual position: %d, expect position: %d\n", target, actualPos, expectPos);
                    break;
                }
            }

            if (failed) {
                break;
            }
        }
    }

    private static void testSearchFirstGreatOrEqual() {
        BinarySearch binarySearch = new BinarySearch();
        int[] constArr = {1, 1, 1, 2, 2, 2, 2, 4, 5, 10, 10, 10, 10, 10, 200, 300, 400, 400};

        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 1));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 3));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 5));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 9));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 10));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 399));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 400));
        System.out.println(binarySearch.searchFirstGreatOrEqual(constArr, 499));
    }

    private static void testSearchLastLessOrEqual() {
        BinarySearch binarySearch = new BinarySearch();
        int[] constArr = {1, 1, 1, 2, 2, 2, 2, 4, 5, 10, 10, 10, 10, 10, 200, 300, 400, 400};

        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 1));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 3));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 5));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 9));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 10));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 399));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 400));
        System.out.println(binarySearch.searchLastLessOrEqual(constArr, 499));
    }

    /**
     * 时间复杂度：
     * O(logn)
     *
     * 局限性：
     * 1. 依赖顺序表结构
     * 2. 只针对有序数据
     * 3. 数据量太小不适合二分查找：顺序遍历就足够了
     * 4. 数据量太大不适合二分查找：二分查找的底层依赖数组结构，而数组要求内存空间连续，对内存的要求比较苛刻
     *
     */
    public int nonRecursionSearch(int[] data, int target) {
        if (data == null || data.length == 0) {
            return -1;
        }

        int left = 0;
        int right = data.length - 1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (data[mid] == target) {
                return mid;
            } else if (data[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return -1;
    }

    public int recursionSearch(int[] data, int target) {
        if (data == null || data.length == 0) {
            return -1;
        }

        return search(data, 0, data.length - 1, target);
    }

    public int searchFirstEqual(int[] data, int target) {
        if (data == null || data.length == 0) {
            return -1;
        }

        int left = 0;
        int right = data.length - 1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (data[mid] > target) {
                right = mid - 1;
            } else if (data[mid] < target) {
                left = mid + 1;
            } else {
                if (mid == 0 || (data[mid - 1] != target)) {
                    return mid;
                }

                right = mid - 1;
            }
        }

        return -1;
    }

    public int searchLastEqual(int[] data, int target) {
        if (data == null || data.length == 0) {
            return -1;
        }

        int left = 0;
        int right = data.length - 1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (data[mid] > target) {
                right = mid - 1;
            } else if (data[mid] < target) {
                left = mid + 1;
            } else {
                if (mid == data.length - 1 || data[mid + 1] != target) {
                    return mid;
                }

                left = mid + 1;
            }
        }

        return -1;
    }

    public int searchFirstGreatOrEqual(int[] data, int target) {
        if (data == null || data.length == 0) {
            return -1;
        }

        int left = 0;
        int right = data.length - 1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (data[mid] >= target) {
                if (mid == 0 || data[mid - 1] < target) {
                    return mid;
                }

                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return -1;
    }

    public int searchLastLessOrEqual(int[] data, int target) {
        if (data == null || data.length == 0) {
            return -1;
        }

        int left = 0;
        int right = data.length - 1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (data[mid] <= target) {
                if (mid == data.length - 1 || data[mid + 1] > target) {
                    return mid;
                }

                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    private int search(int[] data, int left, int right, int target) {
        if (left > right) {
            return -1;
        }

        // 注意运算符的优先级问题
        int mid = left + ((right - left) >> 1);

        if (data[mid] == target) {
            return mid;
        } else if (data[mid] > target) {
            return search(data, left, mid - 1, target);
        } else {
            return search(data, mid + 1, right, target);
        }
    }
}
