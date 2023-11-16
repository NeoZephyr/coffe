package algorithm.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class AlgoFrame extends JFrame {

    private int canvasWidth;
    private int canvasHeight;
    private Circle[] circles;

    public AlgoFrame(String title) {
        this(title, 1024, 768);
    }

    public AlgoFrame(String title, int canvasWidth, int canvasHeight) {
        super(title);

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        Canvas canvas = new Canvas();
        setContentPane(canvas);
        pack();
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void render(Circle[] circles) {
        this.circles = circles;

        // 所有控件重新刷新一遍
        repaint();
    }

    class Canvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 抗锯齿
            RenderingHints hints = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Graphics2D g2d = (Graphics2D) g;
            g2d.addRenderingHints(hints);
            GraphUtils.setStrokeWidth(g2d, 5);
            GraphUtils.setColor(g2d, Color.BLUE);

            for (Circle circle : circles) {
                if (circle.filled) {
                    GraphUtils.fillCircle(g2d, circle.getX(), circle.getY(), circle.getR());
                } else {
                    GraphUtils.strokeCircle(g2d, circle.getX(), circle.getY(), circle.getR());
                }
            }
        }

        /**
         * 返回画布大小
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(canvasWidth, canvasHeight);
        }
    }
}
