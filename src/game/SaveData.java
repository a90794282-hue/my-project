package game;

import entity.Appearance;
import item.Item;
import item.Potion;
import item.Weapon;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    // Игрок
    public String playerName = "Герой";
    public int playerLevel = 1;
    public int playerXp = 0;
    public int playerHp = 100;
    public int playerMaxHp = 100;
    public int playerMana = 50;
    public int playerMaxMana = 50;
    public int playerGold = 0;
    public int playerStatPoints = 0;
    public double playerX = 800, playerY = 800;
    public double spawnX = 800, spawnY = 800;

    // Статы
    public int strength = 5;
    public int dexterity = 5;
    public int intelligence = 5;
    public int vitality = 5;

    // Внешний вид
    public int skinColorRgb = 0xFFFFD2AA;
    public int hairColorRgb = 0xFF502814;
    public int shirtColorRgb = 0xFFB43232;
    public int pantsColorRgb = 0xFF322850;

    // Мир
    public long worldSeed = 0;
    public boolean inDungeon = false;

    // Инвентарь
    public List<ItemData> inventory = new ArrayList<>();

    // Скиллы
    public int skillPoints = 0;
    public List<String> unlockedSkills = new ArrayList<>();

    // Достижения
    public List<String> unlockedAchievements = new ArrayList<>();

    // Прогресс
    public int enemiesKilled = 0;
    public boolean bossKilled = false;
    public long playTimeMs = 0;

    // ===== Вспомогательный класс =====
    public static class ItemData {
        public String type;      // "weapon", "potion", "armor"
        public String name;
        public String rarity;
        public int baseDamage;
        public String weaponType;   // "SWORD", "BOW", "STAFF"
        public String potionKind;   // "HEALTH", "MANA"
        public int potionValue;
    }
}