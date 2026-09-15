package item;

import entity.Stats;

import java.awt.Color;

public class Weapon extends Item {
    public enum Type { SWORD, BOW, STAFF }

    public Type type;
    public int baseDamage;
    public Stats.ScalingStat scaling;

    public Weapon(String name, Type type, int baseDamage) {
        this.name = name;
        this.type = type;
        this.baseDamage = baseDamage;
        this.rarity = Rarity.COMMON;

        switch (type) {
            case SWORD -> scaling = Stats.ScalingStat.STRENGTH;
            case BOW   -> scaling = Stats.ScalingStat.DEXTERITY;
            case STAFF -> scaling = Stats.ScalingStat.INTELLIGENCE;
        }
    }

    public static Weapon withRarity(String name, Type type, int baseDamage,
                                    Rarity rarity) {
        Weapon w = new Weapon(name, type,
                (int) (baseDamage * rarity.powerMultiplier));
        w.rarity = rarity;
        return w;
    }

    public int getDamage(Stats stats) {
        int bonus = switch (scaling) {
            case STRENGTH     -> stats.getMeleeDamage();
            case DEXTERITY    -> stats.getRangedDamage();
            case INTELLIGENCE -> stats.getSpellDamage();
        };
        return baseDamage + bonus;
    }

    public Type getType() { return type; }

    @Override
    public Color getRarityColor() { return rarity.color; }

    @Override
    public String getDescription() {
        return "Урон: " + baseDamage + " (" + scaling + ")";
    }
}