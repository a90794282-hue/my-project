package entity;

import java.awt.*;

public abstract class Entity {
    public double x, y;
    public int width = 32, height = 32;
    public int hp, maxHp;
    public boolean alive = true;

    public abstract void update();
    public abstract void draw(Graphics2D g);

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public double distanceTo(Entity other) {
        return Math.hypot(x - other.x, y - other.y);
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) { hp = 0; alive = false; }
    }
}