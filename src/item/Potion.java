package item;

import java.awt.Color;

public class Potion extends Item {
    public enum Kind { HEALTH, MANA, BUFF }
    public Kind kind;
    public int value;

    public Potion(String name, Kind kind, int value) {
        this.name = name;
        this.kind = kind;
        this.value = value;
        this.rarity = Rarity.COMMON;
    }

    @Override
    public Color getRarityColor() {
        return switch (kind) {
            case HEALTH -> new Color(255, 80, 80);
            case MANA -> new Color(80, 140, 255);
            case BUFF -> new Color(255, 220, 80);
        };
    }

    @Override
    public String getDescription() {
        return switch (kind) {
            case HEALTH -> "Восстанавливает " + value + " HP";
            case MANA -> "Восстанавливает " + value + " маны";
            case BUFF -> "Даёт бафф";
        };
    }
}