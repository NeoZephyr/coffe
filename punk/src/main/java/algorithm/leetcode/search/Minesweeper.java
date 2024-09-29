package algorithm.leetcode.search;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Minesweeper {

    int[][] dd = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}, {-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

    public char[][] updateBoard(char[][] board, int[] click) {
        int i = click[0];
        int j = click[1];

        if (board[i][j] == 'M') {
            board[i][j] = 'X';
            return board;
        }

        if (board[i][j] == 'E') {
            Queue<int[]> queue = new ArrayDeque<>();
            int count = probe(board, queue, i, j);

            if (count != 0) {
                board[i][j] = (char) ('0' + count);
            } else {
                board[i][j] = 'B';
                bfs(board, queue);
            }
        }

        return board;
    }

    public void bfs(char[][] board, Queue<int[]> queue) {
        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            int count = probe(board, queue, p[0], p[1]);

            if (count == 0) {
                board[p[0]][p[1]] = 'B';
            } else {
                board[p[0]][p[1]] = (char) ('0' + count);
            }
        }
    }

    public int probe(char[][] board, Queue<int[]> queue, int row, int col) {
        int count = 0;
        List<int[]> blocks = new ArrayList<>();

        for (int[] d : dd) {
            int i = row + d[0];
            int j = col + d[1];

            if (i < 0 || i > board.length - 1 || j < 0 || j > board[0].length - 1) {
                continue;
            }

            if (board[i][j] == 'M') {
                count++;
            } else if (board[i][j] == 'E') {
                blocks.add(new int[]{i, j});
            }
        }

        // 如果一个 没有相邻地雷 的空方块（'E'）被挖出，修改它为（'B'），并且所有和其相邻的 未挖出 方块都应该被递归地揭露
        if (count == 0) {
            for (int[] block : blocks) {
                board[block[0]][block[1]] = ' ';
                queue.offer(block);
            }
        }

        return count;
    }

    public char[][] updateBoard1(char[][] board, int[] click) {
        int i = click[0];
        int j = click[1];

        if (board[i][j] == 'M') {
            board[i][j] = 'X';
            return board;
        }

        // 不需要 visited 数组
        // dfs 访问过的点已经被标记成 B 和数字了，只有未访问过的点才是 E
        boolean[][] visited = new boolean[board.length][board[0].length];

        if (board[i][j] == 'E') {
            dfs(board, visited, i, j);
        }

        return board;
    }

    public void dfs(char[][] board, boolean[][] visited, int row, int col) {
        if (visited[row][col]) {
            return;
        }

        int count = 0;
        List<int[]> blocks = new ArrayList<>();
        visited[row][col] = true;

        for (int[] d : dd) {
            int i = row + d[0];
            int j = col + d[1];

            if (i < 0 || i > board.length - 1 || j < 0 || j > board[0].length - 1) {
                continue;
            }

            if (board[i][j] == 'M') {
                count++;
            } else if (board[i][j] == 'E') {
                blocks.add(new int[]{i, j});
            }
        }

        if (count != 0) {
            board[row][col] = (char) ('0' + count);
            return;
        }

        board[row][col] = 'B';

        // 如果一个 没有相邻地雷 的空方块（'E'）被挖出，修改它为（'B'），并且所有和其相邻的 未挖出 方块都应该被递归地揭露
        for (int[] block : blocks) {
            dfs(board, visited, block[0], block[1]);
        }
    }
}
