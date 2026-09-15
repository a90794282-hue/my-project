package world;

import entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PortalManager {
    private List<Portal> portals = new ArrayList<>();
    private Random rnd = new Random();

    // ===== Текущая локация =====
    public boolean inDungeon = false;
    public Portal.Difficulty currentDifficulty = Portal.Difficulty.EASY;
    public Portal activePortal = null;

    // Точка возврата в мир (где игрок зашёл в подземелье)
    public double returnX, returnY;

    public void spawnPortalsInWorld(TileMap worldMap, int count) {
        portals.clear();
        Random rnd = new Random(worldMap.hashCode());

        for (int i = 0; i < count; i++) {
            // Пытаемся найти место на траве/земле
            for (int attempt = 0; attempt < 20; attempt++) {
                double px = 100 + rnd.nextInt(worldMap.getPixelWidth() - 200);
                double py = 100 + rnd.nextInt(worldMap.getPixelHeight() - 200);

                // Проверяем, что не на воде/камне
                if (worldMap.isSolid(px, py)) continue;

                // Выбираем сложность (случайно, но с шансами)
                Portal.Difficulty diff = pickDifficulty();

                portals.add(new Portal(px, py, diff, rnd.nextLong()));
                break;
            }
        }
    }

    private Portal.Difficulty pickDifficulty() {
        int roll = rnd.nextInt(100);
        if (roll < 40) return Portal.Difficulty.EASY;
        if (roll < 70) return Portal.Difficulty.MEDIUM;
        if (roll < 90) return Portal.Difficulty.HARD;
        return Portal.Difficulty.NIGHTMARE;
    }

    public Portal findPortalNear(Player player) {
        for (Portal p : portals) {
            if (p.isPlayerNear(player.x + player.width / 2.0,
                    player.y + player.height / 2.0)) {
                return p;
            }
        }
        return null;
    }

    public List<Portal> getPortals() { return portals; }
    public void clearPortals() { portals.clear(); }
}