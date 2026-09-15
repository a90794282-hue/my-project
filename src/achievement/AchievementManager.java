package achievement;

import entity.Player;
import ui.AchievementToast;

import java.util.ArrayList;
import java.util.List;

public class AchievementManager {

    public static AchievementToast globalToast = null;

    public static class Achievement {
        public String name;
        public String description;
        public String category;
        public boolean unlocked = false;
        public long unlockedTime = 0;
        public boolean secret = false;

        public Achievement(String name, String description, String category) {
            this.name = name;
            this.description = description;
            this.category = category;
        }

        public Achievement secret() {
            this.secret = true;
            return this;
        }
    }

    private List<Achievement> achievements = new ArrayList<>();
    private Player playerRef;
    private int enemiesKilled = 0;
    private int bossesKilled = 0;
    private int itemsCollected = 0;
    private int skillsUnlocked = 0;
    private int craftsMade = 0;
    private int deaths = 0;
    private int dungeonsCleared = 0;
    private int portalsEntered = 0;

    public AchievementManager() {
        // ===== УБИЙСТВА =====
        achievements.add(new Achievement("Первая кровь", "Убить 1 врага", "Бой"));
        achievements.add(new Achievement("Охотник", "Убить 10 врагов", "Бой"));
        achievements.add(new Achievement("Мясник", "Убить 50 врагов", "Бой"));
        achievements.add(new Achievement("Гроза монстров", "Убить 100 врагов", "Бой"));
        achievements.add(new Achievement("Легенда", "Убить 500 врагов", "Бой"));

        // ===== БОССЫ =====
        achievements.add(new Achievement("Босс-убийца", "Убить первого босса", "Боссы"));
        achievements.add(new Achievement("Охотник за головами", "Убить 5 боссов", "Боссы"));

        // ===== УРОВНИ =====
        achievements.add(new Achievement("Первый уровень", "Достичь 2 уровня", "Прогресс"));
        achievements.add(new Achievement("Опытный", "Достичь 5 уровня", "Прогресс"));
        achievements.add(new Achievement("Ветеран", "Достичь 10 уровня", "Прогресс"));
        achievements.add(new Achievement("Мастер", "Достичь 20 уровня", "Прогресс"));
        achievements.add(new Achievement("Легенда", "Достичь 30 уровня", "Прогресс").secret());

        // ===== ЗОЛОТО =====
        achievements.add(new Achievement("Копилка", "Накопить 100 золота", "Богатство"));
        achievements.add(new Achievement("Богач", "Накопить 1000 золота", "Богатство"));
        achievements.add(new Achievement("Миллионер", "Накопить 5000 золота", "Богатство"));

        // ===== ПРЕДМЕТЫ =====
        achievements.add(new Achievement("Коллекционер", "Собрать 10 предметов", "Инвентарь"));
        achievements.add(new Achievement("Скряга", "Собрать 20 предметов", "Инвентарь"));
        achievements.add(new Achievement("Барахольщик", "Собрать 50 предметов", "Инвентарь"));

        // ===== ПОДЗЕМЕЛЬЯ =====
        achievements.add(new Achievement("Первый портал", "Войти в подземелье", "Подземелья"));
        achievements.add(new Achievement("Исследователь", "Войти в 5 подземелий", "Подземелья"));
        achievements.add(new Achievement("Покоритель", "Войти в 20 подземелий", "Подземелья"));

        // ===== СКИЛЛЫ =====
        achievements.add(new Achievement("Ученик", "Изучить 1 скилл", "Скиллы"));
        achievements.add(new Achievement("Мастер скиллов", "Изучить 10 скиллов", "Скиллы"));
        achievements.add(new Achievement("Всесильный", "Изучить 20 скиллов", "Скиллы"));

        // ===== КРАФТ =====
        achievements.add(new Achievement("Ремесленник", "Создать первый предмет", "Крафт"));
        achievements.add(new Achievement("Кузнец", "Создать 10 предметов", "Крафт"));
        achievements.add(new Achievement("Мастер-крафтер", "Создать 50 предметов", "Крафт"));

        // ===== ВЫЖИВАНИЕ =====
        achievements.add(new Achievement("Первая смерть", "Умереть впервые", "Смерть"));
        achievements.add(new Achievement("Феникс", "Умереть 10 раз", "Смерть"));
        achievements.add(new Achievement("Бессмертный", "Прожить 10 уровней без смерти", "Смерть").secret());

        // ===== СКРЫТЫЕ =====
        achievements.add(new Achievement("Первый шаг", "Поговорить с NPC", "Скрытые").secret());
        achievements.add(new Achievement("Легенда мира", "Открыть все другие ачивки", "Скрытые").secret());
    }

    public void unlock(String name) {
        for (Achievement a : achievements) {
            if (a.name.equals(name) && !a.unlocked) {
                a.unlocked = true;
                a.unlockedTime = System.currentTimeMillis();
                System.out.println("🏆 " + a.name + " — " + a.description);
                game.SoundManager.play("levelup");
                if (globalToast != null) globalToast.show(a.name);
            }
        }
    }

    public void onEnemyKilled(boolean isBoss) {
        enemiesKilled++;
        if (isBoss) bossesKilled++;
        checkAll();
    }

    public void onItemCollected() {
        itemsCollected++;
        checkAll();
    }

    public void onSkillUnlocked() {
        skillsUnlocked++;
        checkAll();
    }

    public void onCraftMade() {
        craftsMade++;
        checkAll();
    }

    public void onDeath() {
        deaths++;
        checkAll();
    }

    public void onDungeonEntered() {
        portalsEntered++;
        checkAll();
    }

    public void onNPCSpoken() {
        unlock("Первый шаг");
    }

    public void check(Player player, int kills, int items,
                      boolean bossKilled, boolean enteredDungeon) {
        this.playerRef = player;
        this.enemiesKilled = Math.max(this.enemiesKilled, kills);
        this.itemsCollected = Math.max(this.itemsCollected, items);
        checkAll();
    }

    private void checkAll() {
        if (playerRef == null) return;

        // Убийства
        if (enemiesKilled >= 1) unlock("Первая кровь");
        if (enemiesKilled >= 10) unlock("Охотник");
        if (enemiesKilled >= 50) unlock("Мясник");
        if (enemiesKilled >= 100) unlock("Гроза монстров");
        if (enemiesKilled >= 500) unlock("Легенда");

        // Боссы
        if (bossesKilled >= 1) unlock("Босс-убийца");
        if (bossesKilled >= 5) unlock("Охотник за головами");

        // Уровни
        int lvl = playerRef.getLevel();
        if (lvl >= 2) unlock("Первый уровень");
        if (lvl >= 5) unlock("Опытный");
        if (lvl >= 10) unlock("Ветеран");
        if (lvl >= 20) unlock("Мастер");
        if (lvl >= 30) unlock("Легенда");

        // Золото
        int gold = playerRef.getGold();
        if (gold >= 100) unlock("Копилка");
        if (gold >= 1000) unlock("Богач");
        if (gold >= 5000) unlock("Миллионер");

        // Предметы
        if (itemsCollected >= 10) unlock("Коллекционер");
        if (itemsCollected >= 20) unlock("Скряга");
        if (itemsCollected >= 50) unlock("Барахольщик");

        // Подземелья
        if (portalsEntered >= 1) unlock("Первый портал");
        if (portalsEntered >= 5) unlock("Исследователь");
        if (portalsEntered >= 20) unlock("Покоритель");

        // Скиллы
        if (skillsUnlocked >= 1) unlock("Ученик");
        if (skillsUnlocked >= 10) unlock("Мастер скиллов");
        if (skillsUnlocked >= 20) unlock("Всесильный");

        // Крафт
        if (craftsMade >= 1) unlock("Ремесленник");
        if (craftsMade >= 10) unlock("Кузнец");
        if (craftsMade >= 50) unlock("Мастер-крафтер");

        // Смерть
        if (deaths >= 1) unlock("Первая смерть");
        if (deaths >= 10) unlock("Феникс");

        // Проверка "Легенда мира"
        int total = 0, unlocked = 0;
        for (Achievement a : achievements) {
            if (a.name.equals("Легенда мира")) continue;
            total++;
            if (a.unlocked) unlocked++;
        }
        if (unlocked >= total - 1) unlock("Легенда мира");
    }

    public List<Achievement> getAll() { return achievements; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getSkillsUnlocked() { return skillsUnlocked; }
    public int getCraftsMade() { return craftsMade; }
    public int getDeaths() { return deaths; }
}