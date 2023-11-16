package algorithm.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class AlgoVisualizer {

    private Circle[] circles;
    private AlgoFrame frame;
    private boolean pending = false;

    public AlgoVisualizer(int sceneWidth, int sceneHeight, int n) {
        circles = new Circle[n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            int radius = rand.nextInt(Math.min(sceneWidth, sceneHeight) / 8);
            int x = rand.nextInt(sceneWidth - 2 * radius) + radius;
            int y = rand.nextInt(sceneHeight - 2 * radius) + radius;
            int vx = rand.nextInt((int) (1.0 / radius * 100) + 1) + 1;
            int vy = rand.nextInt((int) (1.0 / radius * 100) + 1) + 1;
            circles[i] = new Circle(x, y, radius, vx, vy, 0, sceneWidth, 0, sceneHeight);
        }

        EventQueue.invokeLater(() -> {
            frame = new AlgoFrame("Algorithm", sceneWidth, sceneHeight);
            frame.addKeyListener(new AlgoKeyListener());
            frame.addMouseListener(new AlgoMouseListener());

            new Thread(() -> run()).start();
        });
    }

    public void run() {
        while (true) {
            frame.render(circles);
            GraphUtils.pause(20);

            if (!pending) {
                for (Circle circle : circles) {
                    circle.move();
                }
            }
        }
    }

    private class AlgoKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyChar() == ' ') {
                pending = !pending;
            }
        }
    }

    private class AlgoMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            e.translatePoint(0, -(frame.getBounds().height - frame.getCanvasHeight()));

            for (Circle circle : circles) {
                if (circle.cover(e.getPoint())) {
                    circle.filled = !circle.filled;
                }
            }
        }
    }
}
