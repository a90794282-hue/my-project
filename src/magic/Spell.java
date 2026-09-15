package magic;

import entity.Entity;
import entity.Player;

public abstract class Spell {
    public String name;
    public int manaCost;
    public int cooldown;
    public int currentCooldown = 0;

    public abstract void cast(Player caster, Entity target);

    public boolean canCast(Player caster) {
        return currentCooldown == 0 && caster.mana >= manaCost;
    }

    public void tick() { if (currentCooldown > 0) currentCooldown--; }
}