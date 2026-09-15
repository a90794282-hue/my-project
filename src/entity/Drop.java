package entity;

import item.Item;
import ui.Renderer;

import java.awt.Color;
import java.awt.Graphics2D;

public class Drop {
    public double x, y;
    public Item item;
    public int gold;
    public boolean isGold;

    private double vx, vy;
    private int life = 600;    // 10 секунд
    private int bob = 0;

    // Для золота
    public Drop(double x, double y, int gold) {
        this.x = x;
        this.y = y;
        this.gold = gold;
        this.isGold = true;
        this.vx = (Math.random() - 0.5) * 4;
        this.vy = -3 - Math.random() * 2;
    }

    // Для предмета
    public Drop(double x, double y, Item item) {
        this.x = x;
        this.y = y;
        this.item = item;
        this.isGold = false;
        this.vx = (Math.random() - 0.5) * 4;
        this.vy = -3 - Math.random() * 2;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.3;
        vx *= 0.95;

        if (vy > 0 && y > 0) {
            vy *= 0.5;
            if (Math.abs(vy) < 0.5) vy = 0;
        }

        life--;
        bob++;
    }

    public boolean isExpired() { return life <= 0; }

    public void draw(Graphics2D g, int camX, int camY) {
        int px = (int) (x - camX);
        int py = (int) (y - camY) + (int) (Math.sin(bob / 15.0) * 3);

        Renderer.enableQuality(g);

        if (isGold) {
            // Монета
            g.setColor(new Color(0, 0, 0, 80));
            g.fillOval(px - 6, py + 8, 12, 5);
            g.setColor(new Color(255, 220, 50));
            g.fillOval(px - 6, py - 6, 12, 12);
            g.setColor(new Color(255, 180, 0));
            g.drawOval(px - 6, py - 6, 12, 12);
            g.setColor(new Color(255, 255, 200));
            g.fillOval(px - 3, py - 4, 3, 3);
        } else {
            // Предмет
            g.setColor(new Color(0, 0, 0, 80));
            g.fillOval(px - 8, py + 8, 16, 5);

            Color c = item.getRarityColor();
            g.setColor(c);
            g.fillRect(px - 6, py - 6, 12, 12);

            g.setColor(c.brighter());
            g.drawRect(px - 6, py - 6, 12, 12);
        }
    }
}