package world;

import java.awt.Color;
import java.awt.Graphics2D;

public class TimeOfDay {
    private double time = 0;  // 0..1 (0 = полночь, 0.5 = полдень)
    private double daySpeed;

    public TimeOfDay() {
        this.daySpeed = 1.0 / (game.Config.DAY_LENGTH_SECONDS * game.Config.FPS);
    }

    public void update() {
        if (!game.Config.TIME_CYCLE_ENABLED) return;
        time += daySpeed;
        if (time > 1) time -= 1;
    }

    /** 0.0 = ночь, 1.0 = день */
    public double getDayFactor() {
        // Синусоида: 0..1..0
        return (Math.sin(time * Math.PI * 2 - Math.PI / 2) + 1) / 2.0;
    }

    public boolean isNight() {
        return getDayFactor() < 0.3;
    }

    /** Наложение затемнения */
    public void applyOverlay(Graphics2D g, int w, int h) {
        double factor = getDayFactor();
        if (factor >= 0.6) return;

        int alpha = (int) ((0.6 - factor) * 400);
        if (alpha > 200) alpha = 200;
        if (alpha < 0) return;

        // Ночь — синеватый оттенок
        g.setColor(new Color(20, 20, 60, alpha));
        g.fillRect(0, 0, w, h);
    }

    /** Цвет неба для параллакса */
    public Color getSkyColor() {
        double f = getDayFactor();
        if (f > 0.7) return new Color(135, 206, 235);  // день — голубое
        if (f > 0.4) return new Color(255, 140, 60);   // закат — оранжевое
        return new Color(15, 15, 45);                   // ночь — тёмное
    }

    public String getTimeString() {
        int hours = (int) (time * 24);
        int minutes = (int) ((time * 24 - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public double getTime() { return time; }
    public void setTime(double t) { this.time = t; }
}