package world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;

public class Portal {

    public enum Difficulty {
        EASY(1.0, 1.0, 1.0, 1, new Color(80, 160, 255)),       // синий
        MEDIUM(1.6, 1.5, 1.8, 5, new Color(80, 220, 100)),     // зелёный
        HARD(2.5, 2.2, 3.0, 10, new Color(255, 220, 80)),      // жёлтый
        NIGHTMARE(4.0, 3.5, 6.0, 20, new Color(255, 60, 60));  // красный

        public final double hpMult;
        public final double dmgMult;
        public final double xpMult;
        public final int minLevel;
        public final Color color;

        Difficulty(double hpMult, double dmgMult, double xpMult,
                   int minLevel, Color color) {
            this.hpMult = hpMult;
            this.dmgMult = dmgMult;
            this.xpMult = xpMult;
            this.minLevel = minLevel;
            this.color = color;
        }

        public String getLabel() {
            return switch (this) {
                case EASY -> "Легко";
                case MEDIUM -> "Средне";
                case HARD -> "Сложно";
                case NIGHTMARE -> "Кошмар";
            };
        }
    }

    public double x, y;
    public Difficulty difficulty;
    public int size = 64;

    // Куда ведёт портал
    public int dungeonWidth = 40;
    public int dungeonHeight = 40;
    public long dungeonSeed;

    public Portal(double x, double y, Difficulty difficulty, long seed) {
        this.x = x;
        this.y = y;
        this.difficulty = difficulty;
        this.dungeonSeed = seed;
    }

    public void draw(Graphics2D g, int camX, int camY) {
        int px = (int) (x - camX);
        int py = (int) (y - camY);
        int cx = px + size / 2;
        int cy = py + size / 2;

        long time = System.currentTimeMillis();
        double pulse = Math.sin(time / 250.0) * 0.15 + 1.0;

        // ===== Большое свечение вокруг =====
        int glowRadius = (int) (size * 1.6 * pulse);
        for (int i = 8; i > 0; i--) {
            int alpha = 25 + i * 3;
            g.setColor(new Color(difficulty.color.getRed(),
                    difficulty.color.getGreen(),
                    difficulty.color.getBlue(), alpha));
            int r = glowRadius + i * 8;
            g.fillOval(cx - r / 2, cy - r / 2, r, r);
        }

        // ===== Врата (каменная арка) =====
        g.setColor(new Color(60, 60, 75));
        g.fillRoundRect(px - 4, py + 8, size + 8, size - 8, 20, 20);

        g.setColor(new Color(90, 90, 105));
        g.fillRoundRect(px, py + 12, size, size - 16, 16, 16);

        // ===== Внутренний вихрь =====
        RadialGradientPaint portal = new RadialGradientPaint(
                new Point(cx, cy),
                size / 2f,
                new float[]{0f, 0.5f, 1f},
                new Color[]{
                        Color.WHITE,
                        difficulty.color.brighter(),
                        difficulty.color.darker().darker()
                }
        );
        g.setPaint(portal);
        g.fillOval(px + 6, py + 18, size - 12, size - 24);

        // ===== Вращение «лучей» =====
        g.setStroke(new BasicStroke(2f));
        double angle = time / 500.0;
        for (int i = 0; i < 6; i++) {
            double a = angle + i * Math.PI / 3;
            int r1 = 10;
            int r2 = 18;
            int x1 = cx + (int) (Math.cos(a) * r1);
            int y1 = cy + (int) (Math.sin(a) * r1);
            int x2 = cx + (int) (Math.cos(a) * r2);
            int y2 = cy + (int) (Math.sin(a) * r2);
            g.setColor(new Color(255, 255, 255, 180));
            g.drawLine(x1, y1, x2, y2);
        }

        // ===== Искры =====
        for (int i = 0; i < 3; i++) {
            double sparkAngle = (time / 300.0 + i * 2.1) % (Math.PI * 2);
            double sparkDist = 20 + (time / 50 + i * 10) % 25;
            int sx = cx + (int) (Math.cos(sparkAngle) * sparkDist);
            int sy = cy + (int) (Math.sin(sparkAngle) * sparkDist);
            int alpha = (int) (255 * (1 - sparkDist / 45.0));
            if (alpha < 0) alpha = 0;
            g.setColor(new Color(difficulty.color.getRed(),
                    difficulty.color.getGreen(),
                    difficulty.color.getBlue(), alpha));
            g.fillOval(sx - 2, sy - 2, 4, 4);
        }

        // ===== Подпись над порталом =====
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        String label = difficulty.getLabel();
        int tw = g.getFontMetrics().stringWidth(label);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(cx - tw / 2 - 6, py - 22, tw + 12, 18, 8, 8);
        g.setColor(difficulty.color.brighter());
        g.drawString(label, cx - tw / 2, py - 8);
    }

    public boolean isPlayerNear(double px, double py) {
        double dx = px - (x + size / 2);
        double dy = py - (y + size / 2);
        return Math.sqrt(dx * dx + dy * dy) < 60;
    }

    public boolean canEnter(int playerLevel) {
        return playerLevel >= difficulty.minLevel;
    }
}