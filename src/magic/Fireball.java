package magic;

import entity.*;

public class Fireball extends Spell {
    public Fireball() {
        name = "Огненный шар";
        manaCost = 15;
        cooldown = 60;
    }

    @Override
    public void cast(Player caster, Entity target) {
        if (!canCast(caster)) return;
        caster.mana -= manaCost;
        currentCooldown = cooldown;
        int dmg = caster.getStats().getSpellDamage() + 20;
        target.takeDamage(dmg);
    }
}