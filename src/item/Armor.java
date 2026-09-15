package item;

import java.awt.Color;

public class Armor extends Item {
    public enum Slot { HELMET, CHEST, LEGS, BOOTS, RING }

    public Slot slot;
    public int defense;

    public Armor(String name, Slot slot, int defense) {
        this.name = name;
        this.slot = slot;
        this.defense = defense;
        this.rarity = Rarity.COMMON;
    }

    @Override
    public Color getRarityColor() {
        return new Color(150, 150, 150);
    }

    @Override
    public String getDescription() {
        return "+" + defense + " защиты (" + slot + ")";
    }
}