package test;

import item.Potion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PotionTest {

    @Test
    public void testHealthPotion() {
        Potion p = new Potion("HP зелье", Potion.Kind.HEALTH, 30);
        assertEquals(Potion.Kind.HEALTH, p.kind);
        assertEquals(30, p.value);
        assertEquals("HP зелье", p.name);
    }

    @Test
    public void testManaPotion() {
        Potion p = new Potion("MP зелье", Potion.Kind.MANA, 25);
        assertEquals(Potion.Kind.MANA, p.kind);
        assertEquals(25, p.value);
    }

    @Test
    public void testBuffPotion() {
        Potion p = new Potion("Buff", Potion.Kind.BUFF, 50);
        assertEquals(Potion.Kind.BUFF, p.kind);
    }
}