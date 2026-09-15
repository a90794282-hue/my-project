package test;

import entity.Stats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class StatsTest {

    private Stats stats;

    @BeforeEach
    public void setUp() {
        stats = new Stats();
    }

    @Test
    public void testDefaultStats() {
        assertEquals(5, stats.getStrength());
        assertEquals(5, stats.getDexterity());
        assertEquals(5, stats.getIntelligence());
        assertEquals(5, stats.getVitality());
    }

    @Test
    public void testMaxHpFormula() {
        // 50 + vitality*10 + strength*2 = 50 + 50 + 10 = 110
        assertEquals(110, stats.getMaxHp());
    }

    @Test
    public void testMaxManaFormula() {
        // 20 + intelligence*8 = 20 + 40 = 60
        assertEquals(60, stats.getMaxMana());
    }

    @Test
    public void testMeleeDamage() {
        // strength*2 = 10
        assertEquals(10, stats.getMeleeDamage());
    }

    @Test
    public void testRangedDamage() {
        assertEquals(10, stats.getRangedDamage());
    }

    @Test
    public void testSpellDamage() {
        // intelligence*3 = 15
        assertEquals(15, stats.getSpellDamage());
    }

    @Test
    public void testCritChance() {
        // 5 + dexterity/2 = 5 + 2 = 7
        assertEquals(7, stats.getCritChance());
    }

    @Test
    public void testAddStrength() {
        stats.addStrength(5);
        assertEquals(10, stats.getStrength());
    }

    @Test
    public void testClassNameNovice() {
        assertEquals("Новичок", stats.getClassName());
    }

    @Test
    public void testClassNameKnight() {
        stats.addStrength(10);
        assertEquals("Рыцарь", stats.getClassName());
    }

    @Test
    public void testClassNameMageKnight() {
        stats.addStrength(10);
        stats.addIntelligence(10);
        assertEquals("Маг-рыцарь", stats.getClassName());
    }

    @Test
    public void testClassNameMage() {
        stats.addIntelligence(10);
        assertEquals("Маг", stats.getClassName());
    }

    @Test
    public void testClassNameArcher() {
        stats.addDexterity(10);
        assertEquals("Лучник", stats.getClassName());
    }

    @Test
    public void testMoveSpeed() {
        // 4 + dexterity/20 = 4 + 0 = 4
        assertEquals(4, stats.getMoveSpeed());
    }

    @Test
    public void testAttackSpeed() {
        // 30 - dexterity/5 = 30 - 1 = 29
        assertEquals(29, stats.getAttackSpeed());
    }
}