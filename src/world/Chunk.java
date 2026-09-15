package world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Chunk {
    public static final int SIZE = 16;   // 16×16 тайлов

    public int chunkX, chunkY;           // координаты чанка
    public BufferedImage rendered;       // готовый отрендеренный кеш
    public boolean dirty = true;         // нужно ли перерисовать

    public Chunk(int cx, int cy) {
        this.chunkX = cx;
        this.chunkY = cy;
    }

    /** Пиксельные координаты верхнего левого угла */
    public int getPixelX(int tileSize) { return chunkX * SIZE * tileSize; }
    public int getPixelY(int tileSize) { return chunkY * SIZE * tileSize; }
}