package test;

import entity.Stats;
import item.Item;
import item.Weapon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeaponTest {

    @Test
    public void testSwordScaling() {
        Stats s = new Stats();
        Weapon w = new Weapon("Меч", Weapon.Type.SWORD, 10);
        // 10 + strength*2 = 10 + 10 = 20
        assertEquals(20, w.getDamage(s));
    }

    @Test
    public void testBowScaling() {
        Stats s = new Stats();
        Weapon w = new Weapon("Лук", Weapon.Type.BOW, 10);
        // 10 + dexterity*2 = 10 + 10 = 20
        assertEquals(20, w.getDamage(s));
    }

    @Test
    public void testStaffScaling() {
        Stats s = new Stats();
        Weapon w = new Weapon("Посох", Weapon.Type.STAFF, 10);
        // 10 + intelligence*3 = 10 + 15 = 25
        assertEquals(25, w.getDamage(s));
    }

    @Test
    public void testCommonRarity() {
        Weapon w = Weapon.withRarity("Обычный меч",
                Weapon.Type.SWORD, 10, Item.Rarity.COMMON);
        // 10 * 1.0 = 10
        assertEquals(10, w.baseDamage);
    }

    @Test
    public void testRareRarity() {
        Weapon w = Weapon.withRarity("Редкий меч",
                Weapon.Type.SWORD, 10, Item.Rarity.RARE);
        // 10 * 1.5 = 15
        assertEquals(15, w.baseDamage);
    }

    @Test
    public void testEpicRarity() {
        Weapon w = Weapon.withRarity("Эпический меч",
                Weapon.Type.SWORD, 10, Item.Rarity.EPIC);
        // 10 * 2.5 = 25
        assertEquals(25, w.baseDamage);
    }

    @Test
    public void testLegendaryRarity() {
        Weapon w = Weapon.withRarity("Легендарный меч",
                Weapon.Type.SWORD, 10, Item.Rarity.LEGENDARY);
        // 10 * 4.0 = 40
        assertEquals(40, w.baseDamage);
    }

    @Test
    public void testWeaponTypeGetter() {
        Weapon w = new Weapon("Меч", Weapon.Type.SWORD, 10);
        assertEquals(Weapon.Type.SWORD, w.getType());
    }

    @Test
    public void testDamageWithHighStrength() {
        Stats s = new Stats();
        s.addStrength(20);  // 5 + 20 = 25
        Weapon w = new Weapon("Меч", Weapon.Type.SWORD, 10);
        // 10 + 25*2 = 60
        assertEquals(60, w.getDamage(s));
    }
}