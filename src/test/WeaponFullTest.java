package test;

import entity.Stats;
import item.Item;
import item.Weapon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeaponFullTest {

    private Weapon sword() { return new Weapon("Меч", Weapon.Type.SWORD, 10); }
    private Weapon bow()   { return new Weapon("Лук", Weapon.Type.BOW, 10); }
    private Weapon staff() { return new Weapon("Посох", Weapon.Type.STAFF, 10); }

    @Test public void swordType() { assertEquals(Weapon.Type.SWORD, sword().getType()); }
    @Test public void bowType() { assertEquals(Weapon.Type.BOW, bow().getType()); }
    @Test public void staffType() { assertEquals(Weapon.Type.STAFF, staff().getType()); }

    @Test public void swordScalesStrength() {
        assertEquals(Stats.ScalingStat.STRENGTH, sword().scaling);
    }
    @Test public void bowScalesDexterity() {
        assertEquals(Stats.ScalingStat.DEXTERITY, bow().scaling);
    }
    @Test public void staffScalesIntelligence() {
        assertEquals(Stats.ScalingStat.INTELLIGENCE, staff().scaling);
    }

    @Test public void swordDamage() {
        assertEquals(20, sword().getDamage(new Stats()));
    }
    @Test public void bowDamage() {
        assertEquals(20, bow().getDamage(new Stats()));
    }
    @Test public void staffDamage() {
        assertEquals(25, staff().getDamage(new Stats()));
    }

    @Test public void swordHighStrength() {
        Stats s = new Stats();
        s.setStrength(50);
        assertEquals(110, sword().getDamage(s));
    }

    @Test public void commonWeapon() {
        Weapon w = Weapon.withRarity("W", Weapon.Type.SWORD, 10, Item.Rarity.COMMON);
        assertEquals(10, w.baseDamage);
    }
    @Test public void rareWeapon() {
        Weapon w = Weapon.withRarity("W", Weapon.Type.SWORD, 10, Item.Rarity.RARE);
        assertEquals(15, w.baseDamage);
    }
    @Test public void epicWeapon() {
        Weapon w = Weapon.withRarity("W", Weapon.Type.SWORD, 10, Item.Rarity.EPIC);
        assertEquals(25, w.baseDamage);
    }
    @Test public void legendaryWeapon() {
        Weapon w = Weapon.withRarity("W", Weapon.Type.SWORD, 10, Item.Rarity.LEGENDARY);
        assertEquals(40, w.baseDamage);
    }

    @Test public void descriptionNotNull() { assertNotNull(sword().getDescription()); }
    @Test public void descriptionContainsDamage() {
        assertTrue(sword().getDescription().contains("10"));
    }
    @Test public void rarityColorNotNull() { assertNotNull(sword().getRarityColor()); }
}