package game;

import entity.*;
import world.*;

import java.awt.Graphics2D;
import java.util.List;

public class RenderLayer {

    private static final int CULL_PADDING = 100;

    public static void drawWorld(Graphics2D g2, int camX, int camY,
                                 TileMap map, BloodDecal blood,
                                 List<Portal> portals,
                                 List<Drop> drops,
                                 List<Enemy> enemies,
                                 Player player,
                                 List<DamageNumber> damageNumbers) {

        // 1. Карта (с чанками)
        if (map != null) {
            map.draw(g2, camX, camY, 1200, 900);
        }

        // 2. Кровь
        g2.translate(-camX, -camY);
        blood.draw(g2, 0, 0);
        g2.translate(camX, camY);

        // 3. Порталы
        for (Portal p : portals) {
            if (!isVisible(p.x, p.y, 100, camX, camY)) continue;
            g2.translate(-camX, -camY);
            p.draw(g2, 0, 0);
            g2.translate(camX, camY);
        }

        // 4. Дроп
        g2.translate(-camX, -camY);
        for (Drop d : drops) {
            if (!isVisible(d.x, d.y, 30, camX, camY)) continue;
            d.draw(g2, 0, 0);
        }
        g2.translate(camX, camY);

        // 5. Враги
        for (Enemy e : enemies) {
            if (!isVisible(e.x, e.y, e.width, camX, camY)) continue;
            g2.translate(-camX, -camY);
            e.draw(g2);
            g2.translate(camX, camY);
        }

        // 6. Игрок
        g2.translate(-camX, -camY);
        player.draw(g2);
        g2.translate(camX, camY);

        // 7. Числа урона
        g2.translate(-camX, -camY);
        for (DamageNumber dn : damageNumbers) dn.draw(g2, 0, 0);
        g2.translate(camX, camY);
    }

    private static boolean isVisible(double x, double y, int size,
                                     int camX, int camY) {
        return x + size >= camX - CULL_PADDING
                && x <= camX + 1200 + CULL_PADDING
                && y + size >= camY - CULL_PADDING
                && y <= camY + 900 + CULL_PADDING;
    }
}