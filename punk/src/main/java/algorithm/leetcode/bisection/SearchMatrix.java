package algorithm.leetcode.bisection;

public class SearchMatrix {

    public static void main(String[] args) {
        System.out.println(new SearchMatrix().searchMatrix(new int[][]{{1,3,5,7}, {10,11,16,20}, {23,30,34,60}}, 13));
        System.out.println(new SearchMatrix().searchMatrix(new int[][]{{1,3,5,7}, {10,11,16,20}, {23,30,34,60}}, 3));
    }

    // 还可以用排除法，从右上角开始，不断排除行、列，缩小范围

    public boolean searchMatrix(int[][] matrix, int target) {
        int row = matrix.length;
        int col = matrix[0].length;
        int low = 0;
        int high = row * col - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int r = mid / col;
            int c = mid % col;

            if (matrix[r][c] == target) {
                return true;
            }

            if (matrix[r][c] > target) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return false;

        // 4 [1][1]
        // 5 [1][2]
        // 3 [1][0]
        // 3
    }
}
