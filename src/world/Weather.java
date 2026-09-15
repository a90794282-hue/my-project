package world;

import ui.ParticleSystem;

import java.awt.*;
import java.util.Random;

public class Weather {
    public enum Type { CLEAR, RAIN, SNOW, FOG }

    public Type current = Type.CLEAR;
    private Random rnd = new Random();
    private int changeTimer = 0;

    public void update(ParticleSystem particles, int screenW, int screenH) {
        // Смена погоды
        if (--changeTimer <= 0) {
            current = Type.values()[rnd.nextInt(Type.values().length)];
            changeTimer = 600 + rnd.nextInt(1200);
        }

        // Спавн частиц
        switch (current) {
            case RAIN -> particles.spawnRain(screenW, screenH);
            case SNOW -> particles.spawnSnow(screenW);
        }
    }

    public void applyOverlay(Graphics2D g, int w, int h) {
        switch (current) {
            case RAIN -> {
                g.setColor(new Color(60, 60, 100, 50));
                g.fillRect(0, 0, w, h);
            }
            case FOG -> {
                g.setColor(new Color(200, 200, 200, 90));
                g.fillRect(0, 0, w, h);
            }
        }
    }

    public double getSpeedModifier() {
        return current == Type.SNOW ? 0.6 : 1.0;
    }
}