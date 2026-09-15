package test;

import entity.Player;
import entity.Stats;
import item.Weapon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerFullTest {

    @Test public void initialLevel() { assertEquals(1, new Player().getLevel()); }
    @Test public void initialXp() { assertEquals(0, new Player().getXp()); }
    @Test public void initialGold() { assertEquals(0, new Player().getGold()); }
    @Test public void initialAlive() { assertTrue(new Player().alive); }
    @Test public void initialNotDead() { assertFalse(new Player().isDead()); }
    @Test public void initialClassName() {
        assertEquals("Новичок", new Player().getClassName());
    }
    @Test public void initialHp() {
        Player p = new Player();
        assertEquals(p.maxHp, p.hp);
    }
    @Test public void initialMana() {
        Player p = new Player();
        assertEquals(p.maxMana, p.mana);
    }

    @Test public void gainXpSmall() {
        Player p = new Player();
        p.gainXp(50);
        assertEquals(50, p.getXp());
        assertEquals(1, p.getLevel());
    }

    @Test public void gainXpLevelUp() {
        Player p = new Player();
        p.gainXp(150);
        assertEquals(2, p.getLevel());
        assertEquals(50, p.getXp());
    }

    @Test public void levelUpGivesStatPoints() {
        Player p = new Player();
        p.gainXp(150);
        assertEquals(3, p.getStatPoints());
    }

    @Test public void levelUpRestoresHp() {
        Player p = new Player();
        p.hp = 10;
        p.gainXp(150);
        assertEquals(p.maxHp, p.hp);
    }

    @Test public void spendStrength() {
        Player p = new Player();
        p.gainXp(150);
        p.spendPoint(Stats.ScalingStat.STRENGTH);
        assertEquals(6, p.getStats().getStrength());
    }

    @Test public void spendDexterity() {
        Player p = new Player();
        p.gainXp(150);
        p.spendPoint(Stats.ScalingStat.DEXTERITY);
        assertEquals(6, p.getStats().getDexterity());
    }

    @Test public void spendIntelligence() {
        Player p = new Player();
        p.gainXp(150);
        p.spendPoint(Stats.ScalingStat.INTELLIGENCE);
        assertEquals(6, p.getStats().getIntelligence());
    }

    @Test public void spendWithoutPoints() {
        Player p = new Player();
        assertFalse(p.spendPoint(Stats.ScalingStat.STRENGTH));
    }

    @Test public void spendVitality() {
        Player p = new Player();
        p.gainXp(150);
        p.spendVitality();
        assertEquals(6, p.getStats().getVitality());
    }

    @Test public void addGold() {
        Player p = new Player();
        p.addGold(100);
        assertEquals(100, p.getGold());
    }

    @Test public void goldCannotGoNegative() {
        Player p = new Player();
        p.addGold(50);
        p.addGold(-100);
        assertEquals(0, p.getGold());
    }

    @Test public void setGold() {
        Player p = new Player();
        p.setGold(500);
        assertEquals(500, p.getGold());
    }

    @Test public void death() {
        Player p = new Player();
        p.die();
        assertTrue(p.isDead());
    }

    @Test public void respawn() {
        Player p = new Player();
        p.die();
        p.respawn();
        assertFalse(p.isDead());
        assertEquals(p.maxHp, p.hp);
    }

    @Test public void takeDamage() {
        Player p = new Player();
        int hpBefore = p.hp;
        p.takeDamage(20);
        assertEquals(hpBefore - 20, p.hp);
    }

    @Test public void damageKills() {
        Player p = new Player();
        p.takeDamage(9999);
        assertEquals(0, p.hp);
        assertFalse(p.alive);
    }

    @Test public void equipWeapon() {
        Player p = new Player();
        Weapon w = new Weapon("Test", Weapon.Type.SWORD, 20);
        p.equipWeapon(w);
        assertEquals(w, p.getEquippedWeapon());
    }

    @Test public void defaultEquippedWeapon() {
        Player p = new Player();
        assertNotNull(p.getEquippedWeapon());
    }

    @Test public void spellsListExists() {
        assertNotNull(new Player().getSpells());
    }

    @Test public void inventoryExists() {
        assertNotNull(new Player().getInventory());
    }

    @Test public void effectsListExists() {
        assertNotNull(new Player().getEffects());
    }
}