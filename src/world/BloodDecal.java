package world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BloodDecal {
    public static class Splat {
        public double x, y;
        public int size;
        public int alpha;
        public long createdAt;

        public Splat(double x, double y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.alpha = 180;
            this.createdAt = System.currentTimeMillis();
        }
    }

    private List<Splat> splats = new ArrayList<>();
    private Random rnd = new Random();

    public void addBlood(double x, double y) {
        for (int i = 0; i < 5; i++) {
            double ox = (rnd.nextDouble() - 0.5) * 40;
            double oy = (rnd.nextDouble() - 0.5) * 40;
            splats.add(new Splat(x + ox, y + oy, 6 + rnd.nextInt(10)));
        }

        // Брызги дальше
        for (int i = 0; i < 3; i++) {
            double angle = rnd.nextDouble() * Math.PI * 2;
            double dist = 30 + rnd.nextDouble() * 40;
            double sx = x + Math.cos(angle) * dist;
            double sy = y + Math.sin(angle) * dist;
            splats.add(new Splat(sx, sy, 3 + rnd.nextInt(5)));
        }

        while (splats.size() > 100) splats.remove(0);
    }

    public void draw(Graphics2D g, int camX, int camY) {
        long now = System.currentTimeMillis();
        for (Splat s : splats) {
            long age = now - s.createdAt;
            float alpha = Math.max(0, 1f - age / 60000f) * (s.alpha / 255f);

            g.setColor(new Color(120, 0, 0, (int) (alpha * 200)));
            g.fillOval((int) (s.x - camX), (int) (s.y - camY), s.size, s.size);
        }
    }

    public int size() { return splats.size(); }
}