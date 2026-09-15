package world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TileMap {
    public int[][] tiles;
    public int tileSize = 32;

    private long worldSeed = 12345L;

    // ===== Чанки =====
    private Map<Long, Chunk> chunks = new HashMap<>();

    public TileMap(int[][] tiles) {
        this.tiles = tiles;
    }

    public void setWorldSeed(long seed) {
        this.worldSeed = seed;
        chunks.clear();   // сбрасываем кеш
    }

    // ============= ОТРИСОВКА =============
    public void draw(Graphics2D g, int camX, int camY, int screenW, int screenH) {
        // Определяем какие чанки видимы
        int chunkPixels = Chunk.SIZE * tileSize;

        int startChunkX = Math.max(0, camX / chunkPixels - 1);
        int startChunkY = Math.max(0, camY / chunkPixels - 1);
        int endChunkX = Math.min(getChunkCols(), (camX + screenW) / chunkPixels + 1);
        int endChunkY = Math.min(getChunkRows(), (camY + screenH) / chunkPixels + 1);

        for (int cy = startChunkY; cy < endChunkY; cy++) {
            for (int cx = startChunkX; cx < endChunkX; cx++) {
                Chunk chunk = getOrCreateChunk(cx, cy);
                if (chunk.rendered == null || chunk.dirty) {
                    renderChunk(chunk);
                }
                g.drawImage(chunk.rendered,
                        chunk.getPixelX(tileSize) - camX,
                        chunk.getPixelY(tileSize) - camY,
                        null);
            }
        }
    }

    /** Старый метод — для совместимости */
    public void draw(Graphics2D g, int camX, int camY) {
        draw(g, camX, camY, 1200, 900);
    }

    private Chunk getOrCreateChunk(int cx, int cy) {
        long key = ((long) cx << 32) | (cy & 0xFFFFFFFFL);
        Chunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(cx, cy);
            chunks.put(key, chunk);
        }
        return chunk;
    }

    private int getChunkCols() { return (tiles[0].length + Chunk.SIZE - 1) / Chunk.SIZE; }
    private int getChunkRows() { return (tiles.length + Chunk.SIZE - 1) / Chunk.SIZE; }

    /** Рендерит весь чанк в BufferedImage ОДИН РАЗ */
    private void renderChunk(Chunk chunk) {
        int size = Chunk.SIZE * tileSize;
        chunk.rendered = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = chunk.rendered.createGraphics();

        int startCol = chunk.chunkX * Chunk.SIZE;
        int startRow = chunk.chunkY * Chunk.SIZE;

        for (int row = 0; row < Chunk.SIZE; row++) {
            for (int col = 0; col < Chunk.SIZE; col++) {
                int mapRow = startRow + row;
                int mapCol = startCol + col;

                if (mapRow >= tiles.length || mapCol >= tiles[0].length) continue;

                int t = tiles[mapRow][mapCol];
                int px = col * tileSize;
                int py = row * tileSize;

                drawTile(g, px, py, mapCol, mapRow, t);

                // Тени от стен
                if (t == 0 || t == 1) {
                    if (mapRow > 0 && tiles[mapRow - 1][mapCol] == 2) {
                        g.setColor(new Color(0, 0, 0, 90));
                        g.fillRect(px, py, tileSize, 8);
                    }
                    if (mapCol > 0 && tiles[mapRow][mapCol - 1] == 2) {
                        g.setColor(new Color(0, 0, 0, 60));
                        g.fillRect(px, py, 6, tileSize);
                    }
                }
            }
        }

        g.dispose();
        chunk.dirty = false;
    }

    // ============= ОТРИСОВКА ТАЙЛА =============
    private void drawTile(Graphics2D g, int px, int py, int col, int row, int t) {
        Random rnd = new Random(worldSeed + col * 73856093L + row * 19349663L);

        switch (t) {
            case 0 -> drawGrassBiome(g, px, py, col, row, rnd);
            case 1 -> drawDirt(g, px, py, col, row, rnd);
            case 2 -> drawStone(g, px, py, col, row, rnd);
            case 3 -> drawWater(g, px, py, col, row);
            case 4 -> drawSand(g, px, py, col, row, rnd);
            case 5 -> drawTree(g, px, py, col, row);
            case 6 -> drawBush(g, px, py, col, row, rnd);
            case 7 -> drawFlowers(g, px, py, col, row, rnd);
            default -> {
                g.setColor(new Color(10, 10, 15));
                g.fillRect(px, py, tileSize, tileSize);
            }
        }
    }

    // ============= ТРАВА С БИОМОМ =============
    private void drawGrassBiome(Graphics2D g, int px, int py,
                                int col, int row, Random rnd) {
        BiomeType biome = BiomeType.fromPosition(col, row, worldSeed);

        // База
        g.setColor(biome.grassColor);
        g.fillRect(px, py, tileSize, tileSize);

        // Пятна темнее
        Color darker = biome.grassColor.darker();
        g.setColor(new Color(darker.getRed(), darker.getGreen(), darker.getBlue(), 60));
        for (int i = 0; i < 3; i++) {
            g.fillOval(px + rnd.nextInt(tileSize - 8),
                    py + rnd.nextInt(tileSize - 8), 8, 6);
        }

        // Уникальные детали
        switch (biome) {
            case FOREST -> {
                // Травинки
                g.setColor(biome.grassColor.brighter());
                for (int i = 0; i < 4; i++) {
                    int bx = px + rnd.nextInt(tileSize - 2);
                    int by = py + rnd.nextInt(tileSize - 6);
                    g.drawLine(bx, by, bx, by - 4);
                }
                // Цветок
                if (rnd.nextInt(12) == 0) {
                    int fx = px + 8 + rnd.nextInt(tileSize - 16);
                    int fy = py + 8 + rnd.nextInt(tileSize - 16);
                    g.setColor(new Color(255, 100, 150));
                    g.fillOval(fx, fy, 4, 4);
                    g.setColor(new Color(255, 230, 100));
                    g.fillOval(fx + 1, fy + 1, 2, 2);
                }
            }
            case DESERT -> {
                // Песчинки
                g.setColor(new Color(255, 230, 180, 150));
                for (int i = 0; i < 6; i++) {
                    g.fillRect(px + rnd.nextInt(tileSize - 3),
                            py + rnd.nextInt(tileSize - 3), 2, 2);
                }
                // Кактус
                if (rnd.nextInt(25) == 0) {
                    g.setColor(new Color(60, 120, 60));
                    g.fillRect(px + tileSize / 2 - 2, py + 10, 4, 20);
                    g.fillRect(px + tileSize / 2 - 6, py + 14, 4, 8);
                    g.fillRect(px + tileSize / 2 + 2, py + 18, 4, 8);
                }
            }
            case SNOW -> {
                // Снежинки
                g.setColor(new Color(255, 255, 255, 220));
                for (int i = 0; i < 8; i++) {
                    g.fillRect(px + rnd.nextInt(tileSize - 4),
                            py + rnd.nextInt(tileSize - 4), 2, 2);
                }
                // Льдинка
                if (rnd.nextInt(20) == 0) {
                    g.setColor(new Color(180, 220, 255));
                    g.fillRect(px + tileSize / 2, py + tileSize / 2, 6, 6);
                }
            }
            case VOLCANO -> {
                // Лавовые трещины
                g.setColor(new Color(255, 100, 0, 150));
                for (int i = 0; i < 2; i++) {
                    int lx = px + rnd.nextInt(tileSize);
                    int ly = py + rnd.nextInt(tileSize);
                    g.drawLine(lx, ly, lx + 8, ly + 4);
                }
                // Пепел
                g.setColor(new Color(40, 40, 40, 200));
                for (int i = 0; i < 4; i++) {
                    g.fillOval(px + rnd.nextInt(tileSize - 4),
                            py + rnd.nextInt(tileSize - 4), 3, 3);
                }
                // Огонь
                if (rnd.nextInt(40) == 0) {
                    g.setColor(new Color(255, 200, 0));
                    g.fillOval(px + tileSize / 2 - 3, py + tileSize / 2 - 6, 6, 10);
                }
            }
            case SWAMP -> {
                // Грязные пятна
                g.setColor(new Color(40, 70, 50, 180));
                for (int i = 0; i < 4; i++) {
                    g.fillOval(px + rnd.nextInt(tileSize - 10),
                            py + rnd.nextInt(tileSize - 10), 10, 6);
                }
                // Пузырь
                if (rnd.nextInt(15) == 0) {
                    g.setColor(new Color(120, 180, 120));
                    g.fillOval(px + tileSize / 2, py + tileSize / 2, 4, 4);
                }
            }
        }
    }

    // ============= ЗЕМЛЯ =============
    private void drawDirt(Graphics2D g, int px, int py, int col, int row, Random rnd) {
        g.setColor(new Color(140, 105, 70));
        g.fillRect(px, py, tileSize, tileSize);

        g.setColor(new Color(100, 70, 40, 120));
        for (int i = 0; i < 4; i++) {
            g.fillOval(px + rnd.nextInt(tileSize - 10),
                    py + rnd.nextInt(tileSize - 10), 6, 4);
        }
        g.setColor(new Color(180, 150, 110, 180));
        for (int i = 0; i < 5; i++) {
            g.fillRect(px + rnd.nextInt(tileSize - 4),
                    py + rnd.nextInt(tileSize - 4), 2, 2);
        }
        // Камушки
        if (rnd.nextInt(8) == 0) {
            int cx = px + 8 + rnd.nextInt(tileSize - 16);
            int cy = py + 8 + rnd.nextInt(tileSize - 16);
            g.setColor(new Color(90, 90, 100));
            g.fillOval(cx, cy, 5, 4);
            g.setColor(new Color(140, 140, 150));
            g.fillOval(cx + 1, cy, 2, 2);
        }
    }

    // ============= КАМЕНЬ (СТЕНЫ) =============
    private void drawStone(Graphics2D g, int px, int py, int col, int row, Random rnd) {
        // База
        g.setColor(new Color(75, 75, 90));
        g.fillRect(px, py, tileSize, tileSize);

        // Верхняя часть (светлая)
        g.setColor(new Color(105, 105, 120));
        g.fillRect(px, py, tileSize, 6);

        // Нижняя тень
        g.setColor(new Color(40, 40, 50));
        g.fillRect(px, py + tileSize - 5, tileSize, 5);

        // Кирпичи
        g.setColor(new Color(55, 55, 70));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(px, py + tileSize / 2, px + tileSize, py + tileSize / 2);

        // Швы со смещением
        boolean shift = (row + col) % 2 == 0;
        int sx = shift ? tileSize / 2 : 0;
        g.drawLine(px + sx, py, px + sx, py + tileSize / 2);
        g.drawLine(px + tileSize - sx, py + tileSize / 2,
                px + tileSize - sx, py + tileSize);

        // Трещины
        if (rnd.nextInt(6) == 0) {
            g.setColor(new Color(30, 30, 40, 200));
            int cx = px + 8 + rnd.nextInt(tileSize - 16);
            int cy = py + 8 + rnd.nextInt(tileSize - 16);
            g.drawLine(cx, cy, cx + 4, cy + 6);
            g.drawLine(cx + 4, cy + 6, cx + 2, cy + 10);
        }

        // Блик сверху
        g.setColor(new Color(255, 255, 255, 40));
        g.drawLine(px + 1, py + 1, px + tileSize - 2, py + 1);
    }

    // ============= ВОДА =============
    private void drawWater(Graphics2D g, int px, int py, int col, int row) {
        RadialGradientPaint water = new RadialGradientPaint(
                new Point(px + tileSize / 2, py + tileSize / 2), tileSize,
                new float[]{0f, 1f},
                new Color[]{new Color(60, 120, 220), new Color(30, 70, 160)});
        g.setPaint(water);
        g.fillRect(px, py, tileSize, tileSize);

        // Волны (статичные, но красивые)
        g.setColor(new Color(120, 180, 255, 150));
        g.fillRect(px, py + 8, tileSize, 2);
        g.fillRect(px, py + 18, tileSize, 2);
        g.fillRect(px, py + 26, tileSize, 2);

        // Блик
        g.setColor(new Color(255, 255, 255, 80));
        g.fillOval(px + 6, py + 6, 6, 3);
    }

    // ============= ПЕСОК =============
    private void drawSand(Graphics2D g, int px, int py, int col, int row, Random rnd) {
        g.setColor(new Color(230, 210, 150));
        g.fillRect(px, py, tileSize, tileSize);

        g.setColor(new Color(200, 180, 120, 150));
        for (int i = 0; i < 6; i++) {
            g.fillRect(px + rnd.nextInt(tileSize - 3),
                    py + rnd.nextInt(tileSize - 3), 2, 2);
        }
        // Ракушка
        if (rnd.nextInt(30) == 0) {
            g.setColor(new Color(255, 240, 220));
            g.fillOval(px + 12, py + 12, 6, 5);
        }
    }

    // ============= ДЕРЕВО =============
    private void drawTree(Graphics2D g, int px, int py, int col, int row) {
        // Трава под деревом
        g.setColor(new Color(50, 110, 50));
        g.fillRect(px, py, tileSize, tileSize);

        // Тень
        g.setColor(new Color(0, 0, 0, 100));
        g.fillOval(px + 4, py + tileSize - 10, tileSize - 8, 8);

        // Ствол
        g.setColor(new Color(90, 60, 35));
        g.fillRect(px + tileSize / 2 - 4, py + tileSize / 2, 8, tileSize / 2);

        // Крона (3 круга)
        g.setColor(new Color(30, 90, 30));
        g.fillOval(px + 2, py - 6, tileSize - 4, tileSize - 6);
        g.setColor(new Color(40, 120, 40));
        g.fillOval(px + 4, py - 2, tileSize - 8, tileSize - 10);
        g.setColor(new Color(60, 150, 60));
        g.fillOval(px + 8, py + 2, tileSize - 16, tileSize - 16);

        // Блик
        g.setColor(new Color(150, 220, 150, 120));
        g.fillOval(px + 8, py, 6, 6);
    }

    // ============= КУСТ =============
    private void drawBush(Graphics2D g, int px, int py, int col, int row, Random rnd) {
        g.setColor(new Color(60, 130, 60));
        g.fillRect(px, py, tileSize, tileSize);

        g.setColor(new Color(40, 100, 40));
        g.fillOval(px + 4, py + 8, tileSize - 8, tileSize - 12);
        g.setColor(new Color(60, 140, 60));
        g.fillOval(px + 8, py + 10, tileSize - 16, tileSize - 18);

        // Ягоды
        if (rnd.nextInt(4) == 0) {
            g.setColor(new Color(200, 40, 60));
            g.fillOval(px + 10, py + 14, 3, 3);
            g.fillOval(px + 18, py + 18, 3, 3);
        }
    }

    // ============= ЦВЕТЫ =============
    private void drawFlowers(Graphics2D g, int px, int py, int col, int row, Random rnd) {
        g.setColor(new Color(60, 140, 60));
        g.fillRect(px, py, tileSize, tileSize);

        for (int i = 0; i < 3; i++) {
            int fx = px + 4 + rnd.nextInt(tileSize - 8);
            int fy = py + 4 + rnd.nextInt(tileSize - 8);

            Color petal = switch (rnd.nextInt(4)) {
                case 0 -> new Color(255, 100, 150);
                case 1 -> new Color(255, 220, 80);
                case 2 -> new Color(150, 100, 255);
                default -> Color.WHITE;
            };
            g.setColor(petal);
            g.fillOval(fx, fy, 4, 4);
            g.setColor(new Color(255, 230, 100));
            g.fillOval(fx + 1, fy + 1, 2, 2);
        }
    }

    // ============= ФАКЕЛЫ =============
    public void drawTorches(Graphics2D g, int camX, int camY) {
        long time = System.currentTimeMillis();

        int startCol = Math.max(0, camX / tileSize - 1);
        int startRow = Math.max(0, camY / tileSize - 1);
        int endCol = Math.min(tiles[0].length, (camX + 1200) / tileSize + 1);
        int endRow = Math.min(tiles.length, (camY + 900) / tileSize + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                if (tiles[row][col] != 2) continue;

                boolean floorNearby =
                        (col > 0 && tiles[row][col - 1] != 2) ||
                                (col < tiles[0].length - 1 && tiles[row][col + 1] != 2) ||
                                (row > 0 && tiles[row - 1][col] != 2) ||
                                (row < tiles.length - 1 && tiles[row + 1][col] != 2);

                if (!floorNearby) continue;
                if ((row * 31 + col * 17) % 17 != 0) continue;

                int px = col * tileSize - camX;
                int py = row * tileSize - camY;

                double pulse = Math.sin(time / 200.0 + col) * 3;
                int flameSize = (int) (8 + pulse);

                for (int i = 4; i > 0; i--) {
                    g.setColor(new Color(255, 180, 50, 30 + i * 10));
                    g.fillOval(px + tileSize / 2 - flameSize - i * 4,
                            py + tileSize / 2 - flameSize - i * 4,
                            (flameSize + i * 4) * 2,
                            (flameSize + i * 4) * 2);
                }

                g.setColor(new Color(80, 50, 20));
                g.fillRect(px + tileSize / 2 - 2, py + tileSize / 2, 4, 10);

                g.setColor(new Color(255, 120, 0));
                g.fillOval(px + tileSize / 2 - 4, py + tileSize / 2 - 8, 8, 12);
                g.setColor(new Color(255, 220, 80));
                g.fillOval(px + tileSize / 2 - 2, py + tileSize / 2 - 6, 4, 8);
            }
        }
    }

    // ============= ГРАНИЦЫ =============
    public int getPixelWidth() { return tiles[0].length * tileSize; }
    public int getPixelHeight() { return tiles.length * tileSize; }

    public boolean isInside(double px, double py) {
        return px >= 0 && py >= 0 &&
                px < getPixelWidth() && py < getPixelHeight();
    }

    public boolean isSolid(double px, double py) {
        int col = (int) (px / tileSize);
        int row = (int) (py / tileSize);
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length) return true;
        return tiles[row][col] == 2
                || tiles[row][col] == 3
                || tiles[row][col] == 5;
    }

    public boolean collides(double x, double y, int w, int h) {
        return isSolid(x, y) ||
                isSolid(x + w, y) ||
                isSolid(x, y + h) ||
                isSolid(x + w, y + h);
    }

    // ============= СБРОС КЕША =============
    public void invalidateChunk(int cx, int cy) {
        long key = ((long) cx << 32) | (cy & 0xFFFFFFFFL);
        Chunk chunk = chunks.get(key);
        if (chunk != null) chunk.dirty = true;
    }

    public void invalidateAll() {
        for (Chunk c : chunks.values()) c.dirty = true;
    }
}