package magic;

import entity.Entity;

public class Effect {
    public enum Type { BURN, FREEZE, POISON, BLEED, REGEN, STRENGTH_BUFF }

    public Type type;
    public int duration;    // в кадрах
    public int power;

    public Effect(Type type, int duration, int power) {
        this.type = type;
        this.duration = duration;
        this.power = power;
    }

    public void apply(Entity e) {
        switch (type) {
            case BURN, POISON, BLEED -> e.takeDamage(power);
            case REGEN -> e.hp = Math.min(e.maxHp, e.hp + power);
            // баффы обрабатываются в Player
        }
        duration--;
    }

    public boolean isExpired() { return duration <= 0; }
}