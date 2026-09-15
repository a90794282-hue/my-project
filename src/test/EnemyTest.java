package test;

import entity.Enemy;
import entity.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    public void testSlimeStats() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.SLIME, 0, 0, p, 1);
        assertEquals(30, e.maxHp);
        assertEquals(20, e.xpReward);
    }

    @Test
    public void testGoblinStats() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.GOBLIN, 0, 0, p, 1);
        assertEquals(50, e.maxHp);
        assertEquals(40, e.xpReward);
    }

    @Test
    public void testOrcStats() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.ORC, 0, 0, p, 1);
        assertEquals(100, e.maxHp);
        assertEquals(80, e.xpReward);
    }

    @Test
    public void testSkeletonStats() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.SKELETON, 0, 0, p, 1);
        assertEquals(70, e.maxHp);
        assertEquals(60, e.xpReward);
    }

    @Test
    public void testScaling() {
        Player p = new Player();
        Enemy e1 = new Enemy(Enemy.Type.SLIME, 0, 0, p, 1);
        Enemy e2 = new Enemy(Enemy.Type.SLIME, 0, 0, p, 5);
        assertTrue(e2.maxHp > e1.maxHp);
    }

    @Test
    public void testMakeElite() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.SLIME, 0, 0, p, 1);
        int before = e.maxHp;
        e.makeElite();
        assertTrue(e.isElite());
        assertEquals(before * 3, e.maxHp);
    }

    @Test
    public void testMakeBoss() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.SLIME, 0, 0, p, 1);
        int before = e.maxHp;
        e.makeBoss();
        assertTrue(e.isBoss());
        assertEquals(before * 10, e.maxHp);
    }

    @Test
    public void testTakeDamage() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.SLIME, 0, 0, p, 1);
        e.takeDamage(20);
        assertEquals(10, e.hp);
    }

    @Test
    public void testDeath() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.SLIME, 0, 0, p, 1);
        e.takeDamage(1000);
        assertFalse(e.alive);
        assertTrue(e.isDying());
    }

    @Test
    public void testAggroInitial() {
        Player p = new Player();
        Enemy e = new Enemy(Enemy.Type.GOBLIN, 100, 100, p, 1);
        assertFalse(e.isAggro());
    }
}