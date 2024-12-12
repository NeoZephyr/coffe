package algorithm.leetcode.greedy;

import java.util.*;

public class WalkingRobotSimulation {

    public static void main(String[] args) {
        System.out.println("");
    }

    public int robotSim(int[] commands, int[][] obstacles) {
        HashSet<String> set = new HashSet<>();

        for (int[] obstacle : obstacles) {
            set.add(String.format("%d,%d", obstacle[0], obstacle[1]));
        }

        int[][] dd = {
                {0, 1},
                {1, 0},
                {0, -1},
                {-1, 0}
        };

        int distance = 0;
        int direction = 0;
        int x = 0;
        int y = 0;

        for (int command : commands) {
            if (command == -1) {
                direction = (direction + 1) % 4;
                continue;
            } else if (command == -2) {
                direction = (direction + 3) % 4;
                continue;
            }

            int xx = x;
            int yy = y;

            for (int i = 0; i < command; i++) {
                xx = xx + dd[direction][0];
                yy = yy + dd[direction][1];

                if (set.contains(String.format("%d,%d", xx, yy))) {
                    break;
                }

                x = xx;
                y = yy;
            }

            int tmp = x * x + y * y;

            if (tmp > distance) {
                distance = tmp;
            }
        }

        return distance;
    }
}
