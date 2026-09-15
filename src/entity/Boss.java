package entity;

import java.awt.*;

public class Boss extends Entity {
    public enum Phase { PHASE_1, PHASE_2, ENRAGED }

    public Phase phase = Phase.PHASE_1;
    public int damage = 40;
    public int xpReward = 500;
    private int attackTimer = 0;
    private Player target;

    public Boss(int x, int y, Player target) {
        this.x = x; this.y = y;
        this.target = target;
        this.maxHp = 1000;
        this.hp = maxHp;
        this.width = 64; this.height = 64;
    }

    @Override
    public void update() {
        if (!alive) return;

        // Смена фаз
        double hpPct = hp / (double) maxHp;
        if (hpPct < 0.3) phase = Phase.ENRAGED;
        else if (hpPct < 0.6) phase = Phase.PHASE_2;

        double dist = distanceTo(target);
        int speed = switch (phase) {
            case PHASE_1 -> 1;
            case PHASE_2 -> 2;
            case ENRAGED -> 3;
        };
        int dmg = switch (phase) {
            case PHASE_1 -> damage;
            case PHASE_2 -> damage * 2;
            case ENRAGED -> damage * 3;
        };

        if (dist > 60) {
            double dx = (target.x - x) / dist;
            double dy = (target.y - y) / dist;
            x += dx * speed;
            y += dy * speed;
        } else if (attackTimer <= 0) {
            target.takeDamage(dmg);
            attackTimer = 90 - (phase.ordinal() * 20);
        }
        if (attackTimer > 0) attackTimer--;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(switch (phase) {
            case PHASE_1 -> new Color(150, 0, 0);
            case PHASE_2 -> new Color(200, 50, 0);
            case ENRAGED -> new Color(255, 100, 0);
        });
        g.fillOval((int) x, (int) y, width, height);

        // HP bar сверху
        g.setColor(Color.BLACK);
        g.fillRect((int) x - 20, (int) y - 20, width + 40, 10);
        g.setColor(Color.RED);
        g.fillRect((int) x - 20, (int) y - 20, (int)((width + 40) * (hp / (double) maxHp)), 10);
    }
}