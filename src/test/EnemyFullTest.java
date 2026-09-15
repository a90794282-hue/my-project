package test;

import entity.Enemy;
import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyFullTest {

    private Player player;
    private Enemy slime;
    private Enemy goblin;
    private Enemy orc;
    private Enemy skeleton;

    @BeforeEach
    public void setUp() {
        player = new Player();
        slime = new Enemy(Enemy.Type.SLIME, 0, 0, player, 1);
        goblin = new Enemy(Enemy.Type.GOBLIN, 0, 0, player, 1);
        orc = new Enemy(Enemy.Type.ORC, 0, 0, player, 1);
        skeleton = new Enemy(Enemy.Type.SKELETON, 0, 0, player, 1);
    }

    // ===== Базовые статы =====
    @Test public void slimeHp() { assertEquals(30, slime.maxHp); }
    @Test public void goblinHp() { assertEquals(50, goblin.maxHp); }
    @Test public void orcHp() { assertEquals(100, orc.maxHp); }
    @Test public void skeletonHp() { assertEquals(70, skeleton.maxHp); }

    @Test public void slimeDamage() { assertEquals(5, slime.damage); }
    @Test public void goblinDamage() { assertEquals(10, goblin.damage); }
    @Test public void orcDamage() { assertEquals(20, orc.damage); }
    @Test public void skeletonDamage() { assertEquals(15, skeleton.damage); }

    @Test public void slimeXp() { assertEquals(20, slime.xpReward); }
    @Test public void goblinXp() { assertEquals(40, goblin.xpReward); }
    @Test public void orcXp() { assertEquals(80, orc.xpReward); }
    @Test public void skeletonXp() { assertEquals(60, skeleton.xpReward); }

    // ===== Скейлинг =====
    @Test public void levelScaling() {
        Enemy e1 = new Enemy(Enemy.Type.SLIME, 0, 0, player, 1);
        Enemy e2 = new Enemy(Enemy.Type.SLIME, 0, 0, player, 10);
        assertTrue(e2.maxHp > e1.maxHp);
    }

    @Test public void levelScalingCaps() {
        Enemy e1 = new Enemy(Enemy.Type.SLIME, 0, 0, player, 50);
        Enemy e2 = new Enemy(Enemy.Type.SLIME, 0, 0, player, 100);
        assertEquals(e1.maxHp, e2.maxHp);  // максимум x5
    }

    @Test public void xpScalesWithLevel() {
        Enemy e1 = new Enemy(Enemy.Type.SLIME, 0, 0, player, 1);
        Enemy e2 = new Enemy(Enemy.Type.SLIME, 0, 0, player, 5);
        assertTrue(e2.xpReward > e1.xpReward);
    }

    // ===== Типы =====
    @Test public void slimeType() { assertEquals(Enemy.Type.SLIME, slime.type); }
    @Test public void goblinType() { assertEquals(Enemy.Type.GOBLIN, goblin.type); }
    @Test public void orcType() { assertEquals(Enemy.Type.ORC, orc.type); }
    @Test public void skeletonType() { assertEquals(Enemy.Type.SKELETON, skeleton.type); }

    // ===== Элиты =====
    @Test public void makeElite() {
        int hpBefore = slime.maxHp;
        slime.makeElite();
        assertTrue(slime.isElite());
        assertEquals(hpBefore * 3, slime.maxHp);
    }

    @Test public void makeEliteBoostsDamage() {
        int dmgBefore = slime.damage;
        slime.makeElite();
        assertEquals(dmgBefore * 2, slime.damage);
    }

    @Test public void makeEliteBoostsXp() {
        int xpBefore = slime.xpReward;
        slime.makeElite();
        assertEquals(xpBefore * 3, slime.xpReward);
    }

    @Test public void makeEliteBoostsSpeed() {
        int speedBefore = slime.speed;
        slime.makeElite();
        assertEquals(speedBefore + 1, slime.speed);
    }

    // ===== Боссы =====
    @Test public void makeBoss() {
        int hpBefore = orc.maxHp;
        orc.makeBoss();
        assertTrue(orc.isBoss());
        assertEquals(hpBefore * 10, orc.maxHp);
    }

    @Test public void makeBossBoostsDamage() {
        int dmgBefore = orc.damage;
        orc.makeBoss();
        assertEquals(dmgBefore * 3, orc.damage);
    }

    @Test public void makeBossBoostsXp() {
        int xpBefore = orc.xpReward;
        orc.makeBoss();
        assertEquals(xpBefore * 10, orc.xpReward);
    }

    @Test public void bossIsElite() {
        orc.makeBoss();
        assertTrue(orc.isElite());
    }

    // ===== Агро =====
    @Test public void initialNotAggro() { assertFalse(slime.isAggro()); }

    @Test public void setAggro() {
        slime.setAggro(true);
        assertTrue(slime.isAggro());
    }

    @Test public void setAggroFalse() {
        slime.setAggro(true);
        slime.setAggro(false);
        assertFalse(slime.isAggro());
    }

    // ===== Урон =====
    @Test public void takeDamage() {
        slime.takeDamage(10);
        assertEquals(20, slime.hp);
    }

    @Test public void dyingOnDeath() {
        slime.takeDamage(1000);
        assertFalse(slime.alive);
        assertTrue(slime.isDying());
    }

    @Test public void cannotTakeDamageAfterDeath() {
        slime.takeDamage(1000);
        slime.takeDamage(1000);  // не должно падать
        assertFalse(slime.alive);
    }

    // ===== Стены =====
    @Test public void isInsideWallNoMap() {
        assertFalse(slime.isInsideWall(0, 0));
    }

    // ===== Update =====
    @Test public void updateNotCrash() {
        slime.update();
        assertNotNull(slime);
    }

    @Test public void updateDying() {
        slime.takeDamage(1000);
        for (int i = 0; i < 40; i++) slime.update();
        assertEquals(0, slime.getDeathTimer() + 0);  // без исключений
    }
}