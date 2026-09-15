package world;

import java.util.Random;

public class DungeonGenerator {
    public static TileMap generate(int w, int h, long seed, Portal.Difficulty diff) {
        Random rnd = new Random(seed);
        int[][] map = new int[h][w];

        // Заполняем камнем
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                map[y][x] = 2;

        // Чем сложнее — тем больше комнат
        int roomCount = switch (diff) {
            case EASY -> 8;
            case MEDIUM -> 12;
            case HARD -> 16;
            case NIGHTMARE -> 20;
        };

        int[][] rooms = new int[roomCount][4];

        for (int i = 0; i < roomCount; i++) {
            int rw = 6 + rnd.nextInt(8);
            int rh = 6 + rnd.nextInt(8);
            int rx = 2 + rnd.nextInt(w - rw - 4);
            int ry = 2 + rnd.nextInt(h - rh - 4);

            rooms[i][0] = rx;
            rooms[i][1] = ry;
            rooms[i][2] = rw;
            rooms[i][3] = rh;

            for (int y = ry; y < ry + rh; y++) {
                for (int x = rx; x < rx + rw; x++) {
                    int roll = rnd.nextInt(100);
                    if (diff == Portal.Difficulty.EASY) {
                        map[y][x] = roll < 70 ? 1 : 0;
                    } else if (diff == Portal.Difficulty.MEDIUM) {
                        map[y][x] = roll < 85 ? 1 : 0;
                    } else {
                        map[y][x] = 1;
                    }
                }
            }
        }

        // Коридоры
        for (int i = 1; i < roomCount; i++) {
            int[] a = rooms[i - 1];
            int[] b = rooms[i];
            int ax = a[0] + a[2] / 2;
            int ay = a[1] + a[3] / 2;
            int bx = b[0] + b[2] / 2;
            int by = b[1] + b[3] / 2;

            int startX = Math.min(ax, bx);
            int endX = Math.max(ax, bx);
            for (int x = startX; x <= endX; x++) {
                if (map[ay][x] == 2) map[ay][x] = 1;
            }
            int startY = Math.min(ay, by);
            int endY = Math.max(ay, by);
            for (int y = startY; y <= endY; y++) {
                if (map[y][bx] == 2) map[y][bx] = 1;
            }
        }

        // В сложных — вода/лавы
        if (diff == Portal.Difficulty.HARD || diff == Portal.Difficulty.NIGHTMARE) {
            for (int i = 0; i < 4; i++) {
                int wx = 5 + rnd.nextInt(w - 10);
                int wy = 5 + rnd.nextInt(h - 10);
                int size = 3 + rnd.nextInt(3);
                for (int y = wy; y < wy + size; y++) {
                    for (int x = wx; x < wx + size; x++) {
                        if (y < h && x < w && map[y][x] == 1) map[y][x] = 3;
                    }
                }
            }
        }

        return new TileMap(map);
    }
}