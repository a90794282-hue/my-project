package item;

import java.awt.Color;

public abstract class Item {
    public enum Rarity {
        COMMON("Обычный", new Color(180, 180, 180), 1.0),
        RARE("Редкий", new Color(80, 160, 255), 1.5),
        EPIC("Эпический", new Color(200, 80, 255), 2.5),
        LEGENDARY("Легендарный", new Color(255, 180, 0), 4.0);

        public final String label;
        public final Color color;
        public final double powerMultiplier;

        Rarity(String label, Color color, double powerMultiplier) {
            this.label = label;
            this.color = color;
            this.powerMultiplier = powerMultiplier;
        }
    }

    public String name;
    public Rarity rarity = Rarity.COMMON;

    public abstract Color getRarityColor();
    public abstract String getDescription();

    public String getFullName() {
        if (rarity == Rarity.COMMON) return name;
        return "[" + rarity.label + "] " + name;
    }
}