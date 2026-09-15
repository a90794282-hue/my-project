package test;

import entity.Entity;
import entity.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    public void testInitialHp() {
        Player p = new Player();
        assertEquals(p.maxHp, p.hp);
        assertTrue(p.alive);
    }

    @Test
    public void testTakeDamage() {
        Player p = new Player();
        p.takeDamage(20);
        assertEquals(p.maxHp - 20, p.hp);
    }

    @Test
    public void testHpCannotBeNegative() {
        Player p = new Player();
        p.takeDamage(10000);
        assertEquals(0, p.hp);
        assertFalse(p.alive);
    }

    @Test
    public void testGetBounds() {
        Player p = new Player();
        p.x = 100;
        p.y = 200;
        assertNotNull(p.getBounds());
    }

    @Test
    public void testDistanceTo() {
        Player p1 = new Player();
        p1.x = 0;
        p1.y = 0;

        Player p2 = new Player();
        p2.x = 3;
        p2.y = 4;

        assertEquals(5.0, p1.distanceTo(p2), 0.01);
    }
}