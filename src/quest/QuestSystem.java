package quest;

import entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestSystem {
    public static class Quest {
        public String title;
        public String description;
        public int target;
        public int progress = 0;
        public boolean completed = false;
        public boolean claimed = false;
        public int rewardGold;
        public int rewardXp;

        public Quest(String title, String desc, int target, int gold, int xp) {
            this.title = title;
            this.description = desc;
            this.target = target;
            this.rewardGold = gold;
            this.rewardXp = xp;
        }

        public void progress(int amount) {
            if (completed) return;
            progress = Math.min(target, progress + amount);
            if (progress >= target) completed = true;
        }

        public String progressText() {
            return progress + " / " + target;
        }
    }

    private List<Quest> quests = new ArrayList<>();

    public QuestSystem() {
        quests.add(new Quest("Первая кровь",
                "Убить 5 врагов", 5, 100, 50));
        quests.add(new Quest("Охотник",
                "Убить 20 врагов", 20, 500, 200));
        quests.add(new Quest("Богач",
                "Накопить 500 золота", 500, 0, 100));
        quests.add(new Quest("Ветеран",
                "Достичь 5 уровня", 5, 0, 300));
    }

    public void addQuest(String title, String desc, int target, int gold, int xp) {
        quests.add(new Quest(title, desc, target, gold, xp));
    }

    public void onEnemyKilled() {
        quests.get(0).progress(1);
        quests.get(1).progress(1);
    }

    public void update(Player player) {
        quests.get(2).progress(player.getGold());
        quests.get(3).progress(player.getLevel());
    }

    public void claimCompleted(Player player) {
        for (Quest q : quests) {
            if (q.completed && !q.claimed) {
                q.claimed = true;
                player.addGold(q.rewardGold);
                player.gainXp(q.rewardXp);
                game.SoundManager.play("levelup");
            }
        }
    }

    public List<Quest> getAll() { return quests; }
}