package game;

import entity.Enemy;
import entity.Player;
import world.TileMap;

import java.util.List;
import java.util.Random;

public class EnemySpawner {
    private int spawnTimer = 0;
    private int spawnInterval;
    private int maxEnemies;
    private Random rnd = new Random();

    private static final int MIN_DIST_FROM_PLAYER = 500;
    private static final int MAX_DIST_FROM_PLAYER = 900;
    private static final int SAFE_MARGIN = 64;

    public EnemySpawner(int spawnInterval, int maxEnemies) {
        this.spawnInterval = spawnInterval;
        this.maxEnemies = maxEnemies;
    }

    public void update(List<Enemy> enemies, Player player, TileMap map) {
        if (enemies.size() >= maxEnemies) return;
        if (++spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnEnemy(enemies, player, map);
        }
    }

    private void spawnEnemy(List<Enemy> enemies, Player player, TileMap map) {
        if (map == null) return;

        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = rnd.nextDouble() * Math.PI * 2;
            double dist = MIN_DIST_FROM_PLAYER +
                    rnd.nextDouble() * (MAX_DIST_FROM_PLAYER - MIN_DIST_FROM_PLAYER);

            double spawnX = player.x + Math.cos(angle) * dist;
            double spawnY = player.y + Math.sin(angle) * dist;

            if (spawnX < SAFE_MARGIN || spawnY < SAFE_MARGIN) continue;
            if (spawnX > map.getPixelWidth() - SAFE_MARGIN) continue;
            if (spawnY > map.getPixelHeight() - SAFE_MARGIN) continue;
            if (map.isSolid(spawnX, spawnY)) continue;

            Enemy e = new Enemy(pickType(), (int) spawnX, (int) spawnY,
                    player, player.getLevel());

            // 15% шанс элитного
            if (rnd.nextDouble() < 0.15) {
                e.makeElite();
            }

            enemies.add(e);
            return;
        }
    }

    private Enemy.Type pickType() {
        int roll = rnd.nextInt(100);
        if (roll < 40) return Enemy.Type.SLIME;
        if (roll < 70) return Enemy.Type.GOBLIN;
        if (roll < 90) return Enemy.Type.SKELETON;
        return Enemy.Type.ORC;
    }
}