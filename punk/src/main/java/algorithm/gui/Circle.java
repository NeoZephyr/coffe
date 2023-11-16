package algorithm.gui;

import lombok.Data;

import java.awt.*;

@Data
public class Circle {
    private int x;
    private int y;
    private int r;

    private int vx;
    private int vy;

    private int lx;
    private int hx;

    private int lh;
    private int hh;

    public boolean filled = false;

    public Circle(int x, int y, int r, int vx, int vy, int lx, int hx, int lh, int hh) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.vx = vx;
        this.vy = vy;
        this.lx = lx;
        this.lh = lh;
        this.hx = hx;
        this.hh = hh;
    }

    public void move() {
        x += vx;
        y += vy;

        if (x <= lx + r) {
            x = lx + r;
            vx = -vx;
        }
        if (x >= hx - r) {
            x = hx - r;
            vx = -vx;
        }
        if (y <= lh + r) {
            y = lh + r;
            vy = -vy;
        }
        if (y >= hh - r) {
            y = hh - r;
            vy = -vy;
        }
    }

    public boolean cover(Point p) {
        return (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y) <= r * r;
    }
}
