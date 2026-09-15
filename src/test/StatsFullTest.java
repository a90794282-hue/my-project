package test;

import entity.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatsFullTest {

    private Stats stats;

    @BeforeEach
    public void setUp() {
        stats = new Stats();
    }

    @Test public void defaultStrength() { assertEquals(5, stats.getStrength()); }
    @Test public void defaultDexterity() { assertEquals(5, stats.getDexterity()); }
    @Test public void defaultIntelligence() { assertEquals(5, stats.getIntelligence()); }
    @Test public void defaultVitality() { assertEquals(5, stats.getVitality()); }

    @Test public void maxHpFormula() { assertEquals(110, stats.getMaxHp()); }
    @Test public void maxManaFormula() { assertEquals(60, stats.getMaxMana()); }
    @Test public void meleeDamage() { assertEquals(10, stats.getMeleeDamage()); }
    @Test public void rangedDamage() { assertEquals(10, stats.getRangedDamage()); }
    @Test public void spellDamage() { assertEquals(15, stats.getSpellDamage()); }
    @Test public void critChance() { assertEquals(7, stats.getCritChance()); }
    @Test public void critDamage() { assertEquals(155, stats.getCritDamage()); }
    @Test public void dodgeChance() { assertEquals(1, stats.getDodgeChance()); }
    @Test public void manaRegen() { assertEquals(1, stats.getManaRegen()); }
    @Test public void hpRegen() { assertEquals(1, stats.getHpRegen()); }
    @Test public void moveSpeed() { assertEquals(4, stats.getMoveSpeed()); }
    @Test public void attackSpeed() { assertEquals(29, stats.getAttackSpeed()); }

    @Test public void fireResist() { assertEquals(0, stats.getFireResist()); }
    @Test public void iceResist() { assertEquals(0, stats.getIceResist()); }
    @Test public void poisonResist() { assertEquals(0, stats.getPoisonResist()); }

    @Test public void setStrength() {
        stats.setStrength(20);
        assertEquals(20, stats.getStrength());
    }
    @Test public void setDexterity() {
        stats.setDexterity(15);
        assertEquals(15, stats.getDexterity());
    }
    @Test public void setIntelligence() {
        stats.setIntelligence(30);
        assertEquals(30, stats.getIntelligence());
    }
    @Test public void setVitality() {
        stats.setVitality(12);
        assertEquals(12, stats.getVitality());
    }

    @Test public void addStrength() {
        stats.addStrength(10);
        assertEquals(15, stats.getStrength());
    }
    @Test public void addDexterity() {
        stats.addDexterity(3);
        assertEquals(8, stats.getDexterity());
    }
    @Test public void addIntelligence() {
        stats.addIntelligence(7);
        assertEquals(12, stats.getIntelligence());
    }
    @Test public void addVitality() {
        stats.addVitality(5);
        assertEquals(10, stats.getVitality());
    }

    @Test public void classNovice() {
        assertEquals("Новичок", stats.getClassName());
    }
    @Test public void classKnight() {
        stats.addStrength(10);
        assertEquals("Рыцарь", stats.getClassName());
    }
    @Test public void classArcher() {
        stats.addDexterity(10);
        assertEquals("Лучник", stats.getClassName());
    }
    @Test public void classMage() {
        stats.addIntelligence(10);
        assertEquals("Маг", stats.getClassName());
    }
    @Test public void classTank() {
        stats.addVitality(10);
        assertEquals("Танк", stats.getClassName());
    }
    @Test public void classMageKnight() {
        stats.addStrength(10);
        stats.addIntelligence(10);
        assertEquals("Маг-рыцарь", stats.getClassName());
    }
    @Test public void classMagicArcher() {
        stats.addDexterity(10);
        stats.addIntelligence(10);
        assertEquals("Магический лучник", stats.getClassName());
    }
    @Test public void classPaladinHunter() {
        stats.addStrength(10);
        stats.addDexterity(10);
        assertEquals("Паладин-охотник", stats.getClassName());
    }
    @Test public void classArchmageWarrior() {
        stats.addStrength(10);
        stats.addDexterity(10);
        stats.addIntelligence(10);
        assertEquals("Архимаг-воин", stats.getClassName());
    }
    @Test public void classDarkMage() {
        stats.addVitality(10);
        stats.addIntelligence(10);
        assertEquals("Тёмный маг", stats.getClassName());
    }

    @Test public void addNegativeStrength() {
        stats.addStrength(-3);
        assertEquals(2, stats.getStrength());
    }
    @Test public void maxValues() {
        stats.setStrength(1000);
        stats.setVitality(1000);
        assertTrue(stats.getMaxHp() > 10000);
    }
}