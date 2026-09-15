package world;

import java.util.Random;

public class WorldGenerator {

    public static TileMap generate(int w, int h, long seed) {
        Random rnd = new Random(seed);
        int[][] map = new int[h][w];

        // 1. База — трава
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                map[y][x] = 0;

        // 2. Граница мира — скалы
        int border = 3;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x < border || y < border ||
                        x >= w - border || y >= h - border) {
                    map[y][x] = 2;
                }
            }
        }

        // 3. Озёра
        int lakeCount = 5 + rnd.nextInt(4);
        for (int i = 0; i < lakeCount; i++) {
            int cx = 8 + rnd.nextInt(w - 16);
            int cy = 8 + rnd.nextInt(h - 16);
            int radius = 3 + rnd.nextInt(4);

            for (int y = cy - radius - 2; y <= cy + radius + 2; y++) {
                for (int x = cx - radius - 2; x <= cx + radius + 2; x++) {
                    if (y < border || x < border || y >= h - border || x >= w - border) continue;
                    double dx = x - cx;
                    double dy = y - cy;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < radius) map[y][x] = 3;
                    else if (dist < radius + 1.5 && map[y][x] == 0) map[y][x] = 4;
                }
            }
        }

        // 4. Лес
        int forestCount = 4 + rnd.nextInt(3);
        for (int i = 0; i < forestCount; i++) {
            int cx = 10 + rnd.nextInt(w - 20);
            int cy = 10 + rnd.nextInt(h - 20);
            int radius = 5 + rnd.nextInt(6);

            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int x = cx - radius; x <= cx + radius; x++) {
                    if (y < border || x < border || y >= h - border || x >= w - border) continue;
                    if (map[y][x] != 0) continue;
                    double dx = x - cx;
                    double dy = y - cy;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < radius) {
                        double density = 1 - (dist / radius);
                        if (rnd.nextDouble() < density * 0.4) map[y][x] = 5;
                    }
                }
            }
        }

        // 5. Кусты и цветы
        for (int y = border; y < h - border; y++) {
            for (int x = border; x < w - border; x++) {
                if (map[y][x] != 0) continue;
                double roll = rnd.nextDouble();
                if (roll < 0.02) map[y][x] = 6;
                else if (roll < 0.05) map[y][x] = 7;
            }
        }

        // 6. Тропинки
        int pathCount = 3 + rnd.nextInt(3);
        for (int i = 0; i < pathCount; i++) {
            int x = border + rnd.nextInt(w - border * 2);
            int y = border + rnd.nextInt(h - border * 2);
            int length = 30 + rnd.nextInt(50);
            int dirX = rnd.nextBoolean() ? 1 : -1;
            int dirY = rnd.nextBoolean() ? 1 : -1;

            for (int step = 0; step < length; step++) {
                if (x < border || y < border || x >= w - border || y >= h - border) break;
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        int px = x + dx;
                        int py = y + dy;
                        if (px < 0 || py < 0 || px >= w || py >= h) continue;
                        if (map[py][px] == 0 || map[py][px] == 6 || map[py][px] == 7)
                            map[py][px] = 1;
                    }
                }
                if (rnd.nextInt(4) == 0) dirX = -dirX;
                if (rnd.nextInt(4) == 0) dirY = -dirY;
                x += dirX;
                y += dirY;
            }
        }

        TileMap tm = new TileMap(map);
        tm.setWorldSeed(seed);
        return tm;
    }
}