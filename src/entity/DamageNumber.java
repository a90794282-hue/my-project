package entity;

import game.FontCache;
import java.awt.Color;
import java.awt.Graphics2D;

public class DamageNumber {
    public double x, y;
    public String text;
    public Color color;
    public int life = 60;
    public boolean crit;

    private double vy = -2;

    public DamageNumber(double x, double y, int damage, boolean crit) {
        this.x = x + (Math.random() - 0.5) * 20;
        this.y = y;
        this.text = crit ? "-" + damage + "!" : "-" + damage;
        this.color = crit ? new Color(255, 220, 50) : new Color(255, 60, 60);
        this.crit = crit;

        if (crit) {
            this.vy = -3.5;
            this.life = 80;
        }
    }

    public DamageNumber(double x, double y, String text, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }

    public void update() {
        y += vy;
        vy *= 0.94;
        life--;
    }

    public boolean isExpired() { return life <= 0; }

    public void draw(Graphics2D g, int camX, int camY) {
        int px = (int) (x - camX);
        int py = (int) (y - camY);

        float alpha = Math.min(1f, life / 30f);
        float scale = crit ? 1f + (1f - alpha) * 0.3f : 1f;

        int size = (int) ((crit ? 26 : 16) * scale);
        g.setFont(FontCache.arialBold(size));

        // Тень (обводка снизу-справа)
        g.setColor(new Color(0, 0, 0, (int) (220 * alpha)));
        g.drawString(text, px + 2, py + 2);

        // Обводка по кругу
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                g.setColor(new Color(0, 0, 0, (int) (180 * alpha)));
                g.drawString(text, px + dx, py + dy);
            }
        }

        // Основной текст
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int) (255 * alpha)));
        g.drawString(text, px, py);
    }
}