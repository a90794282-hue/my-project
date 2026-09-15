package ui;

import entity.Particle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class ParticleSystem {
    private static final int MAX_PARTICLES = 2000;

    private final Particle[] pool = new Particle[MAX_PARTICLES];
    private int activeCount = 0;

    private final Random rnd = new Random();
    private int camX, camY;

    public ParticleSystem() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            pool[i] = new Particle();
        }
    }

    /** Найти свободную частицу или перезаписать самую старую */
    private Particle allocate() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (!pool[i].active) return pool[i];
        }
        // Все заняты — перезаписываем случайную
        return pool[rnd.nextInt(MAX_PARTICLES)];
    }

    public void setCamera(int camX, int camY) {
        this.camX = camX;
        this.camY = camY;
    }

    public void update() {
        activeCount = 0;
        for (int i = 0; i < MAX_PARTICLES; i++) {
            Particle p = pool[i];
            if (p.active) {
                p.update();
                if (p.active) activeCount++;
            }
        }
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            Particle p = pool[i];
            if (p.active) p.draw(g, camX, camY);
        }
    }

    // ============ ЭФФЕКТЫ ============

    public void spawnHitSparks(double x, double y) {
        for (int i = 0; i < 12; i++) {
            double a = rnd.nextDouble() * Math.PI * 2;
            double speed = 2 + rnd.nextDouble() * 4;
            Particle p = allocate();
            p.init(x, y, Math.cos(a) * speed, Math.sin(a) * speed,
                    4 + rnd.nextInt(3), 25, new Color(255, 220, 80));
            p.gravity = 0.3;
            p.shrink = true;
        }
    }

    public void spawnBlood(double x, double y) {
        for (int i = 0; i < 15; i++) {
            double a = rnd.nextDouble() * Math.PI * 2;
            double speed = 1 + rnd.nextDouble() * 5;
            Particle p = allocate();
            p.init(x, y, Math.cos(a) * speed, Math.sin(a) * speed - 1,
                    3 + rnd.nextInt(4), 40, new Color(180, 20, 20));
            p.gravity = 0.4;
            p.shrink = true;
        }
    }

    public void spawnSmoke(double x, double y, Color color) {
        for (int i = 0; i < 15; i++) {
            double vx = (rnd.nextDouble() - 0.5) * 2;
            double vy = -1 - rnd.nextDouble() * 2;
            Particle p = allocate();
            p.init(x + rnd.nextInt(20) - 10, y + rnd.nextInt(20) - 10,
                    vx, vy, 6 + rnd.nextInt(8), 60, color);
            p.gravity = -0.05;
        }
    }

    public void spawnMagicBurst(double x, double y, Color color) {
        for (int i = 0; i < 25; i++) {
            double a = rnd.nextDouble() * Math.PI * 2;
            double speed = 1 + rnd.nextDouble() * 6;
            Particle p = allocate();
            p.init(x, y, Math.cos(a) * speed, Math.sin(a) * speed,
                    5, 35, color);
            p.gravity = 0;
            p.shrink = true;
        }
    }

    public void spawnRain(int screenW, int screenH) {
        for (int i = 0; i < 3; i++) {
            Particle p = allocate();
            p.init(rnd.nextInt(screenW), -10,
                    0, 15 + rnd.nextDouble() * 5,
                    2, 80, new Color(150, 180, 255));
            p.gravity = 0;
        }
    }

    public void spawnSnow(int screenW) {
        for (int i = 0; i < 2; i++) {
            Particle p = allocate();
            p.init(rnd.nextInt(screenW), -10,
                    (rnd.nextDouble() - 0.5) * 1.5, 1 + rnd.nextDouble() * 1.5,
                    3 + rnd.nextInt(3), 200, Color.WHITE);
            p.gravity = 0;
        }
    }

    public int size() { return activeCount; }
}