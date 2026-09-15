package ui;

import java.awt.*;
import java.util.Random;

public class ParallaxBackground {
    private Star[] stars;
    private int width, height;

    private static class Star {
        double x, y;
        int size;
        double speed; // множитель скорости параллакса
    }

    public ParallaxBackground(int width, int height) {
        this.width = width;
        this.height = height;
        Random rnd = new Random();
        stars = new Star[80];
        for (int i = 0; i < stars.length; i++) {
            Star s = new Star();
            s.x = rnd.nextInt(width * 3);
            s.y = rnd.nextInt(height);
            s.size = 1 + rnd.nextInt(3);
            s.speed = 0.1 + rnd.nextDouble() * 0.3;
            stars[i] = s;
        }
    }

    public void draw(Graphics2D g, double camX, double camY) {
        // Слой 1: дальний — градиент
        Renderer.drawGradient(g, width, height,
                new Color(20, 25, 45), new Color(8, 8, 15));

        // Слой 2: звёзды (медленный параллакс)
        for (Star s : stars) {
            int px = (int) (s.x - camX * s.speed) % (width * 3);
            int py = (int) (s.y - camY * s.speed) % height;
            if (px < 0) px += width * 3;
            if (py < 0) py += height;

            int alpha = 100 + (int) (s.speed * 300);
            if (alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillOval(px, py, s.size, s.size);
        }
    }
}