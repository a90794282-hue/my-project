package world;

import java.awt.Color;

public enum BiomeType {
    FOREST(new Color(60, 130, 60), new Color(120, 90, 60), "Лес"),
    DESERT(new Color(230, 200, 130), new Color(200, 170, 100), "Пустыня"),
    SNOW(new Color(240, 240, 250), new Color(200, 200, 220), "Снег"),
    VOLCANO(new Color(80, 40, 30), new Color(60, 20, 10), "Вулкан"),
    SWAMP(new Color(50, 90, 70), new Color(70, 70, 50), "Болото");

    public final Color grassColor;
    public final Color dirtColor;
    public final String label;

    BiomeType(Color grass, Color dirt, String label) {
        this.grassColor = grass;
        this.dirtColor = dirt;
        this.label = label;
    }

    /**
     * Определяет биом по координате на основе синусоидальных волн.
     * Создаёт большие кластеры-биомы.
     */
    public static BiomeType fromPosition(int tileX, int tileY, long seed) {
        // Используем синусоиды для создания "пятен" биомов
        double n1 = Math.sin(tileX * 0.05 + seed * 0.001)
                * Math.cos(tileY * 0.05 + seed * 0.002);
        double n2 = Math.sin((tileX + tileY) * 0.03 + seed * 0.003);
        double noise = (n1 + n2) / 2.0;

        if (noise < -0.4) return FOREST;
        if (noise < -0.1) return SWAMP;
        if (noise < 0.2) return FOREST;
        if (noise < 0.5) return DESERT;
        return SNOW;
    }

    /**
     * Упрощённый — по квадранту (для теста).
     */
    public static BiomeType fromQuadrant(int tileX, int tileY) {
        int qx = tileX / 50;
        int qy = tileY / 50;
        int q = (qx + qy * 2) % 5;
        return switch (q) {
            case 0 -> FOREST;
            case 1 -> DESERT;
            case 2 -> SNOW;
            case 3 -> VOLCANO;
            default -> SWAMP;
        };
    }
}