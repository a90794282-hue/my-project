package test;

import entity.Entity;
import entity.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntityFullTest {

    @Test public void initialAlive() {
        Player p = new Player();
        assertTrue(p.alive);
    }

    @Test public void takeDamage() {
        Player p = new Player();
        int hpBefore = p.hp;
        p.takeDamage(20);
        assertEquals(hpBefore - 20, p.hp);
    }

    @Test public void hpCannotGoBelowZero() {
        Player p = new Player();
        p.takeDamage(9999);
        assertEquals(0, p.hp);
    }

    @Test public void deathOnZeroHp() {
        Player p = new Player();
        p.takeDamage(9999);
        assertFalse(p.alive);
    }

    @Test public void getBounds() {
        Player p = new Player();
        p.x = 100;
        p.y = 200;
        assertNotNull(p.getBounds());
        assertEquals(100, p.getBounds().x);
    }

    @Test public void distanceToSamePoint() {
        Player p1 = new Player();
        p1.x = 0; p1.y = 0;
        Player p2 = new Player();
        p2.x = 0; p2.y = 0;
        assertEquals(0.0, p1.distanceTo(p2), 0.001);
    }

    @Test public void distanceTo() {
        Player p1 = new Player();
        p1.x = 0; p1.y = 0;
        Player p2 = new Player();
        p2.x = 3; p2.y = 4;
        assertEquals(5.0, p1.distanceTo(p2), 0.001);
    }

    @Test public void distanceToDiagonal() {
        Player p1 = new Player();
        p1.x = 0; p1.y = 0;
        Player p2 = new Player();
        p2.x = 10; p2.y = 10;
        assertEquals(Math.sqrt(200), p1.distanceTo(p2), 0.001);
    }

    @Test public void distanceToLarge() {
        Player p1 = new Player();
        p1.x = 0; p1.y = 0;
        Player p2 = new Player();
        p2.x = 1000; p2.y = 2000;
        assertEquals(Math.hypot(1000, 2000), p1.distanceTo(p2), 0.001);
    }

    @Test public void defaultWidth() {
        Player p = new Player();
        assertEquals(64, p.width);
    }

    @Test public void defaultHeight() {
        Player p = new Player();
        assertEquals(64, p.height);
    }

    @Test public void damageReducesHp() {
        Player p = new Player();
        p.takeDamage(1);
        assertEquals(p.maxHp - 1, p.hp);
    }

    @Test public void multipleDamage() {
        Player p = new Player();
        p.takeDamage(10);
        p.takeDamage(20);
        p.takeDamage(30);
        assertEquals(p.maxHp - 60, p.hp);
    }
}