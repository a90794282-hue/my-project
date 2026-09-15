package ui;

import entity.Enemy;
import entity.Player;
import world.TileMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class MiniMap {
    private static final int SIZE = 180;
    private static final int MARGIN = 20;
    private static final int SCALE = 3;

    public void draw(Graphics2D g, Player player, TileMap map,
                     List<Enemy> enemies, int screenW) {
        if (map == null) return;

        int px = screenW - SIZE - MARGIN;
        int py = MARGIN;

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(px - 4, py - 4, SIZE + 8, SIZE + 8, 12, 12);

        int centerX = px + SIZE / 2;
        int centerY = py + SIZE / 2;

        int startCol = (int) (player.x / map.tileSize) - (SIZE / SCALE) / 2;
        int startRow = (int) (player.y / map.tileSize) - (SIZE / SCALE) / 2;

        for (int row = 0; row < SIZE / SCALE; row++) {
            for (int col = 0; col < SIZE / SCALE; col++) {
                int mapRow = startRow + row;
                int mapCol = startCol + col;

                if (mapRow < 0 || mapRow >= map.tiles.length ||
                        mapCol < 0 || mapCol >= map.tiles[0].length) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(switch (map.tiles[mapRow][mapCol]) {
                        case 0 -> new Color(60, 130, 60);
                        case 1 -> new Color(120, 90, 60);
                        case 2 -> new Color(70, 70, 80);
                        case 3 -> new Color(50, 100, 200);
                        default -> Color.BLACK;
                    });
                }
                g.fillRect(px + col * SCALE, py + row * SCALE, SCALE, SCALE);
            }
        }

        g.setColor(Color.RED);
        g.fillOval(centerX - 3, centerY - 3, 6, 6);
        g.setColor(Color.WHITE);
        g.drawOval(centerX - 3, centerY - 3, 6, 6);

        for (Enemy e : enemies) {
            double dx = e.x - player.x;
            double dy = e.y - player.y;

            if (Math.abs(dx) > (SIZE / SCALE) * map.tileSize / 2) continue;
            if (Math.abs(dy) > (SIZE / SCALE) * map.tileSize / 2) continue;

            int ex = centerX + (int) (dx / map.tileSize * SCALE);
            int ey = centerY + (int) (dy / map.tileSize * SCALE);

            if (ex < px || ex > px + SIZE || ey < py || ey > py + SIZE) continue;

            if (e.isAggro()) {
                g.setColor(new Color(255, 80, 80));
            } else {
                g.setColor(new Color(200, 150, 50));
            }
            g.fillOval(ex - 2, ey - 2, 4, 4);
        }

        g.setColor(new Color(255, 255, 255, 120));
        g.drawRoundRect(px - 4, py - 4, SIZE + 8, SIZE + 8, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.drawString("X: " + (int) player.x + " Y: " + (int) player.y,
                px, py + SIZE + 20);
    }
}