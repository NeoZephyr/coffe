package algorithm.leetcode.search;

import java.util.ArrayDeque;
import java.util.Queue;

public class NumberOfIslands {

    int[][] dd = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int row = grid.length;
        int col = grid[0].length;
        int count = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid[i][j] == '1') {
                    grid[i][j] = '0';
                    Queue<Point> queue = new ArrayDeque<>();
                    queue.offer(new Point(i, j));
                    bfs(grid, queue, row - 1, col - 1);
                    count++;
                }
            }
        }

        return count;
    }

    class Point {
        int row;
        int col;

        public Point(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private void bfs(char[][] grid, Queue<Point> queue, int row, int col) {
        while (!queue.isEmpty()) {
            Point point = queue.poll();

            for (int[] d : dd) {
                int i = point.row + d[0];
                int j = point.col + d[1];

                if (i < 0 || i > row || j < 0 || j > col) {
                    continue;
                }

                if (grid[i][j] != '0') {
                    grid[i][j] = '0';
                    queue.offer(new Point(i, j));
                }
            }
        }
    }

    public int numIslands1(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int row = grid.length;
        int col = grid[0].length;
        int count = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid[i][j] == '1') {
                    dfs(grid, new Point(i, j), row - 1, col - 1);
                    count++;
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, Point point, int row, int col) {
        // 防止重复遍历
        grid[point.row][point.col] = '0';

        for (int[] d : dd) {
            int i = point.row + d[0];
            int j = point.col + d[1];

            if (i < 0 || i > row || j < 0 || j > col) {
                continue;
            }

            if (grid[i][j] != '0') {
                dfs(grid, new Point(i, j), row, col);
            }
        }
    }
}
