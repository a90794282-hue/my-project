package entity;

import java.awt.Color;
import java.awt.Graphics2D;

public class Particle {
    public double x, y;
    public double vx, vy;
    public double gravity = 0.1;

    public int life;
    public int maxLife;

    public int size;
    public int r, g, b;
    public boolean fadeOut = true;
    public boolean shrink = false;
    public boolean active = false;

    public Particle() {
    }

    public void init(double x, double y, double vx, double vy,
                     int size, int life, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.life = life;
        this.maxLife = life;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.gravity = 0.1;
        this.fadeOut = true;
        this.shrink = false;
        this.active = true;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += gravity;
        vx *= 0.98;
        life--;
        if (life <= 0) active = false;
    }

    public void draw(Graphics2D g, int camX, int camY) {
        if (!active) return;

        int alpha = fadeOut ? (int) (255.0 * life / maxLife) : 255;
        int s = shrink ? Math.max(1, (int) (size * ((double) life / maxLife))) : size;

        g.setColor(new Color(r, this.g, b, alpha));
        g.fillRect((int) (x - camX) - s / 2,
                (int) (y - camY) - s / 2, s, s);
    }
}