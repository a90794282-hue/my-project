package test;

import entity.Player;
import entity.Stats;
import item.Weapon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testInitialPlayer() {
        Player p = new Player();
        assertEquals(1, p.getLevel());
        assertEquals(0, p.getXp());
        assertEquals(0, p.getGold());
        assertEquals(110, p.maxHp);
        assertEquals(60, p.maxMana);
    }

    @Test
    public void testGainXp() {
        Player p = new Player();
        p.gainXp(50);
        assertEquals(50, p.getXp());
        assertEquals(1, p.getLevel());
    }

    @Test
    public void testLevelUp() {
        Player p = new Player();
        p.gainXp(150);  // больше 100 → уровень 2
        assertEquals(2, p.getLevel());
        assertEquals(50, p.getXp());  // 150 - 100 = 50
        assertEquals(3, p.getStatPoints());
    }

    @Test
    public void testSpendPoint() {
        Player p = new Player();
        p.gainXp(150);
        p.spendPoint(Stats.ScalingStat.STRENGTH);
        assertEquals(6, p.getStats().getStrength());
        assertEquals(2, p.getStatPoints());
    }

    @Test
    public void testAddGold() {
        Player p = new Player();
        p.addGold(100);
        assertEquals(100, p.getGold());
    }

    @Test
    public void testGoldCannotBeNegative() {
        Player p = new Player();
        p.addGold(50);
        p.addGold(-100);
        assertEquals(0, p.getGold());
    }

    @Test
    public void testDeath() {
        Player p = new Player();
        p.hp = 0;
        p.die();
        assertTrue(p.isDead());
    }

    @Test
    public void testRespawn() {
        Player p = new Player();
        p.hp = 10;
        p.die();
        p.respawn();
        assertFalse(p.isDead());
        assertEquals(p.maxHp, p.hp);
    }

    @Test
    public void testClassName() {
        Player p = new Player();
        assertEquals("Новичок", p.getClassName());
    }

    @Test
    public void testEquipWeapon() {
        Player p = new Player();
        Weapon sword = new Weapon("Стальной меч", Weapon.Type.SWORD, 15);
        p.equipWeapon(sword);
        assertEquals(sword, p.getEquippedWeapon());
    }
}