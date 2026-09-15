package game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Appearance;
import entity.Player;
import item.Item;
import item.Potion;
import item.Weapon;
import skill.SkillTree;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SaveManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String SAVE_DIR = "saves/";

    public static void save(Game game, int slot) {
        try {
            new File(SAVE_DIR).mkdirs();

            SaveData data = new SaveData();
            Player p = game.player;

            // Игрок
            data.playerName = p.getAppearance().name;
            data.playerLevel = p.getLevel();
            data.playerXp = p.getXp();
            data.playerHp = p.hp;
            data.playerMaxHp = p.maxHp;
            data.playerMana = p.mana;
            data.playerMaxMana = p.maxMana;
            data.playerGold = p.getGold();
            data.playerStatPoints = p.getStatPoints();
            data.playerX = p.x;
            data.playerY = p.y;
            data.spawnX = p.spawnX;
            data.spawnY = p.spawnY;

            // Статы
            data.strength = p.getStats().getStrength();
            data.dexterity = p.getStats().getDexterity();
            data.intelligence = p.getStats().getIntelligence();
            data.vitality = p.getStats().getVitality();

            // Внешний вид
            Appearance a = p.getAppearance();
            data.skinColorRgb = a.skinColor.getRGB();
            data.hairColorRgb = a.hairColor.getRGB();
            data.shirtColorRgb = a.shirtColor.getRGB();
            data.pantsColorRgb = a.pantsColor.getRGB();

            // Мир
            data.worldSeed = game.getWorldSeed();
            data.inDungeon = game.state.isInDungeon();

            // Инвентарь
            for (Item item : p.getInventory().getItems()) {
                SaveData.ItemData id = new SaveData.ItemData();
                id.name = item.name;
                id.rarity = item.rarity.name();
                if (item instanceof Weapon w) {
                    id.type = "weapon";
                    id.baseDamage = w.baseDamage;
                    id.weaponType = w.getType().name();
                } else if (item instanceof Potion pot) {
                    id.type = "potion";
                    id.potionKind = pot.kind.name();
                    id.potionValue = pot.value;
                }
                data.inventory.add(id);
            }

            // Скиллы
            data.skillPoints = game.skillTree.getSkillPoints();
            for (SkillTree.Skill s : game.skillTree.getAllSkills()) {
                if (s.unlocked) data.unlockedSkills.add(s.name);
            }

            // Достижения
            for (var ach : game.achievements.getAll()) {
                if (ach.unlocked) data.unlockedAchievements.add(ach.name);
            }

            // Прогресс
            data.enemiesKilled = game.getEnemiesKilled();
            data.bossKilled = game.isBossKilled();

            // Запись
            File file = new File(SAVE_DIR + "slot" + slot + ".json");
            try (FileWriter w = new FileWriter(file)) {
                gson.toJson(data, w);
            }

            game.showToast("Игра сохранена в слот " + slot);
        } catch (Exception e) {
            e.printStackTrace();
            game.showToast("Ошибка сохранения: " + e.getMessage());
        }
    }

    public static void load(Game game, int slot) {
        try {
            File file = new File(SAVE_DIR + "slot" + slot + ".json");
            if (!file.exists()) {
                game.showToast("Слот " + slot + " пуст");
                return;
            }

            SaveData data;
            try (FileReader r = new FileReader(file)) {
                data = gson.fromJson(r, SaveData.class);
            }

            Player p = game.player;

            // Игрок
            p.getAppearance().name = data.playerName;
            p.x = data.playerX;
            p.y = data.playerY;
            p.spawnX = data.spawnX;
            p.spawnY = data.spawnY;
            p.hp = data.playerHp;
            p.mana = data.playerMana;
            p.setGold(data.playerGold);
            p.setLevel(data.playerLevel, data.playerXp, data.playerStatPoints);

            // Статы
            p.getStats().setStrength(data.strength);
            p.getStats().setDexterity(data.dexterity);
            p.getStats().setIntelligence(data.intelligence);
            p.getStats().setVitality(data.vitality);

            // Внешний вид
            Appearance a = p.getAppearance();
            a.skinColor = new Color(data.skinColorRgb, true);
            a.hairColor = new Color(data.hairColorRgb, true);
            a.shirtColor = new Color(data.shirtColorRgb, true);
            a.pantsColor = new Color(data.pantsColorRgb, true);

            // Мир
            game.setWorldSeed(data.worldSeed);
            game.state.setLocation(data.inDungeon
                    ? GameState.Location.DUNGEON
                    : GameState.Location.WORLD);

            // Инвентарь
            p.getInventory().getItems().clear();
            for (SaveData.ItemData id : data.inventory) {
                Item item = null;
                if ("weapon".equals(id.type)) {
                    Weapon w = new Weapon(id.name,
                            Weapon.Type.valueOf(id.weaponType), id.baseDamage);
                    w.rarity = Item.Rarity.valueOf(id.rarity);
                    item = w;
                } else if ("potion".equals(id.type)) {
                    Potion pot = new Potion(id.name,
                            Potion.Kind.valueOf(id.potionKind), id.potionValue);
                    item = pot;
                }
                if (item != null) p.getInventory().add(item);
            }

            // Скиллы
            game.skillTree.setSkillPoints(data.skillPoints);
            for (SkillTree.Skill s : game.skillTree.getAllSkills()) {
                if (data.unlockedSkills.contains(s.name)) {
                    s.unlocked = true;
                }
            }

            // Достижения
            for (var ach : game.achievements.getAll()) {
                if (data.unlockedAchievements.contains(ach.name)) {
                    ach.unlocked = true;
                }
            }

            // Прогресс
            game.setEnemiesKilled(data.enemiesKilled);
            game.setBossKilled(data.bossKilled);

            game.loadWorldFromSeed(data.worldSeed);
            game.showToast("Загружено из слота " + slot);
        } catch (Exception e) {
            e.printStackTrace();
            game.showToast("Ошибка загрузки: " + e.getMessage());
        }
    }

    public static boolean hasSave(int slot) {
        return new File(SAVE_DIR + "slot" + slot + ".json").exists();
    }
}