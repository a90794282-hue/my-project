package test;

import item.Potion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PotionFullTest {

    @Test public void healthPotion() {
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        assertEquals(Potion.Kind.HEALTH, p.kind);
        assertEquals(30, p.value);
    }

    @Test public void manaPotion() {
        Potion p = new Potion("MP", Potion.Kind.MANA, 25);
        assertEquals(Potion.Kind.MANA, p.kind);
    }

    @Test public void buffPotion() {
        Potion p = new Potion("Buff", Potion.Kind.BUFF, 50);
        assertEquals(Potion.Kind.BUFF, p.kind);
    }

    @Test public void potionName() {
        Potion p = new Potion("Зелье HP", Potion.Kind.HEALTH, 30);
        assertEquals("Зелье HP", p.name);
    }

    @Test public void colorNotNull() {
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        assertNotNull(p.getRarityColor());
    }

    @Test public void healthDescription() {
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        assertTrue(p.getDescription().contains("30"));
    }

    @Test public void threeKinds() {
        assertEquals(3, Potion.Kind.values().length);
    }

    @Test public void zeroValue() {
        Potion p = new Potion("Empty", Potion.Kind.HEALTH, 0);
        assertEquals(0, p.value);
    }

    @Test public void largeValue() {
        Potion p = new Potion("Big", Potion.Kind.HEALTH, 9999);
        assertEquals(9999, p.value);
    }

    @Test public void allKindsHaveColors() {
        for (Potion.Kind k : Potion.Kind.values()) {
            Potion p = new Potion("X", k, 10);
            assertNotNull(p.getRarityColor());
        }
    }
}