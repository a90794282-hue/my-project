package test;

import achievement.AchievementManager;
import entity.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AchievementManagerTest {

    @Test
    public void testInitialState() {
        AchievementManager am = new AchievementManager();
        assertEquals(32, am.getAll().size());
    }

    @Test
    public void testUnlock() {
        AchievementManager am = new AchievementManager();
        am.unlock("Первая кровь");

        AchievementManager.Achievement a = am.getAll().get(0);
        assertTrue(a.unlocked);
    }

    @Test
    public void testCannotUnlockTwice() {
        AchievementManager am = new AchievementManager();
        am.unlock("Первая кровь");
        long firstTime = am.getAll().get(0).unlockedTime;

        try { Thread.sleep(10); } catch (Exception ignored) {}
        am.unlock("Первая кровь");

        assertEquals(firstTime, am.getAll().get(0).unlockedTime);
    }

    @Test
    public void testEnemyKillTracking() {
        AchievementManager am = new AchievementManager();
        am.onEnemyKilled(false);
        am.onEnemyKilled(false);
        am.onEnemyKilled(false);
        assertEquals(3, am.getEnemiesKilled());
    }

    @Test
    public void testDeathTracking() {
        AchievementManager am = new AchievementManager();
        am.onDeath();
        am.onDeath();
        assertEquals(2, am.getDeaths());
    }

    @Test
    public void testCraftTracking() {
        AchievementManager am = new AchievementManager();
        am.onCraftMade();
        assertEquals(1, am.getCraftsMade());
    }
}