package test;

import entity.Player;
import org.junit.jupiter.api.Test;
import quest.QuestSystem;
import static org.junit.jupiter.api.Assertions.*;

public class QuestSystemTest {

    @Test
    public void testInitialQuests() {
        QuestSystem qs = new QuestSystem();
        assertEquals(4, qs.getAll().size());
    }

    @Test
    public void testAddQuest() {
        QuestSystem qs = new QuestSystem();
        qs.addQuest("Тест", "Описание", 5, 100, 50);
        assertEquals(5, qs.getAll().size());

        QuestSystem.Quest q = qs.getAll().get(4);
        assertEquals("Тест", q.title);
        assertEquals(5, q.target);
    }

    @Test
    public void testEnemyKillProgress() {
        QuestSystem qs = new QuestSystem();
        qs.onEnemyKilled();
        qs.onEnemyKilled();
        qs.onEnemyKilled();

        QuestSystem.Quest first = qs.getAll().get(0);
        assertEquals(3, first.progress);
    }

    @Test
    public void testQuestCompleted() {
        QuestSystem qs = new QuestSystem();
        for (int i = 0; i < 5; i++) qs.onEnemyKilled();

        QuestSystem.Quest first = qs.getAll().get(0);
        assertTrue(first.completed);
    }

    @Test
    public void testClaimReward() {
        QuestSystem qs = new QuestSystem();
        Player p = new Player();

        for (int i = 0; i < 5; i++) qs.onEnemyKilled();
        qs.claimCompleted(p);

        // Первый квест: 100 gold, 50 xp
        assertEquals(100, p.getGold());
        assertEquals(50, p.getXp());
    }

    @Test
    public void testCannotClaimTwice() {
        QuestSystem qs = new QuestSystem();
        Player p = new Player();

        for (int i = 0; i < 5; i++) qs.onEnemyKilled();
        qs.claimCompleted(p);
        qs.claimCompleted(p);

        assertEquals(100, p.getGold());  // не 200
    }
}