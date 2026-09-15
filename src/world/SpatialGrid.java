package world;

import entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialGrid {
    private final int cellSize;
    private final Map<Long, List<Entity>> cells = new HashMap<>();

    public SpatialGrid(int cellSize) {
        this.cellSize = cellSize;
    }

    private long key(int cx, int cy) {
        return ((long) cx << 32) | (cy & 0xFFFFFFFFL);
    }

    public void clear() {
        cells.clear();
    }

    public void insert(Entity e) {
        int cx = (int) (e.x / cellSize);
        int cy = (int) (e.y / cellSize);
        long k = key(cx, cy);
        cells.computeIfAbsent(k, kk -> new ArrayList<>()).add(e);
    }

    /** Все сущности в радиусе от точки */
    public List<Entity> query(double x, double y, double radius) {
        List<Entity> result = new ArrayList<>();
        int minCx = (int) ((x - radius) / cellSize);
        int maxCx = (int) ((x + radius) / cellSize);
        int minCy = (int) ((y - radius) / cellSize);
        int maxCy = (int) ((y + radius) / cellSize);

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cy = minCy; cy <= maxCy; cy++) {
                List<Entity> list = cells.get(key(cx, cy));
                if (list != null) result.addAll(list);
            }
        }
        return result;
    }

    /** Ближайшая сущность в радиусе */
    public Entity queryNearest(double x, double y, double radius) {
        Entity best = null;
        double bestDist = radius * radius;

        int minCx = (int) ((x - radius) / cellSize);
        int maxCx = (int) ((x + radius) / cellSize);
        int minCy = (int) ((y - radius) / cellSize);
        int maxCy = (int) ((y + radius) / cellSize);

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cy = minCy; cy <= maxCy; cy++) {
                List<Entity> list = cells.get(key(cx, cy));
                if (list == null) continue;
                for (Entity e : list) {
                    double dx = e.x - x;
                    double dy = e.y - y;
                    double d = dx * dx + dy * dy;
                    if (d < bestDist) {
                        bestDist = d;
                        best = e;
                    }
                }
            }
        }
        return best;
    }
}