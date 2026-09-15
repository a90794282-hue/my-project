package game;

import entity.BossPhase;
import entity.Enemy;
import entity.Player;

import java.util.List;

public class BossController {
    private Enemy boss;
    private long lastSummonTime = 0;

    public void setBoss(Enemy boss) {
        this.boss = boss;
    }

    public void update(List<Enemy> enemies, Player player) {
        if (boss == null || !boss.alive) return;

        double hpPercent = boss.hp / (double) boss.maxHp;
        BossPhase.Phase phase = BossPhase.getPhase(hpPercent);

        // Призыв миньонов в фазе 2+
        if (phase != BossPhase.Phase.PHASE_1) {
            long now = System.currentTimeMillis();
            if (now - lastSummonTime > 7000) {
                lastSummonTime = now;
                summonMinions(enemies, player);
            }
        }
    }

    private void summonMinions(List<Enemy> enemies, Player player) {
        int count = 2 + (int)(Math.random() * 2);
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = 80 + Math.random() * 40;
            int ex = (int)(boss.x + Math.cos(angle) * dist);
            int ey = (int)(boss.y + Math.sin(angle) * dist);

            Enemy minion = new Enemy(Enemy.Type.SKELETON, ex, ey,
                    player, player.getLevel());
            minion.makeElite();
            minion.setAggro(true);
            enemies.add(minion);
        }
    }

    public boolean hasBoss() {
        return boss != null && boss.alive;
    }
}