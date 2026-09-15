package game;

import entity.Drop;
import entity.Enemy;
import entity.Player;
import item.Item;
import item.Potion;
import item.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootManager {
    private List<Drop> drops = new ArrayList<>();
    private Random rnd = new Random();

    public void spawnLoot(Enemy enemy) {
        double x = enemy.x + enemy.width / 2.0;
        double y = enemy.y + enemy.height / 2.0;

        // ===== Золото (с бонусом от уровня) =====
        int baseGold = switch (enemy.type) {
            case SLIME -> 3 + rnd.nextInt(5);
            case GOBLIN -> 8 + rnd.nextInt(10);
            case SKELETON -> 12 + rnd.nextInt(15);
            case ORC -> 20 + rnd.nextInt(25);
        };

        // Элитные дают x3
        if (enemy.isElite()) baseGold *= 3;
        drops.add(new Drop(x, y, baseGold));

        // ===== Предметы =====
        // Элитные гарантированно дают предмет
        boolean dropItem = enemy.isElite() || rnd.nextDouble() < 0.3;

        if (dropItem) {
            Item item = generateRandomItem(enemy);
            drops.add(new Drop(x + rnd.nextInt(20) - 10,
                    y + rnd.nextInt(20) - 10, item));
        }
    }

    private Item generateRandomItem(Enemy enemy) {
        // Шанс редкости зависит от типа врага
        Item.Rarity rarity = rollRarity(enemy);

        int roll = rnd.nextInt(100);
        if (roll < 45) {
            // Зелья
            int amount = (int) (30 * rarity.powerMultiplier);
            Potion p = new Potion(
                    rarity == Item.Rarity.COMMON
                            ? (rnd.nextBoolean() ? "Зелье HP" : "Зелье маны")
                            : "Большое зелье " + rarity.label.toLowerCase(),
                    rnd.nextBoolean() ? Potion.Kind.HEALTH : Potion.Kind.MANA,
                    amount);
            p.rarity = rarity;
            return p;
        } else {
            // Оружие
            String[] names = {"Кинжал", "Меч", "Топор", "Лук", "Посох",
                    "Клинок", "Копьё", "Молот"};
            Weapon.Type[] types = {
                    Weapon.Type.SWORD, Weapon.Type.SWORD, Weapon.Type.SWORD,
                    Weapon.Type.BOW, Weapon.Type.STAFF,
                    Weapon.Type.SWORD, Weapon.Type.SWORD, Weapon.Type.SWORD
            };
            int i = rnd.nextInt(names.length);
            String prefix = switch (rarity) {
                case COMMON -> "";
                case RARE -> "Закалённый ";
                case EPIC -> "Древний ";
                case LEGENDARY -> "Легендарный ";
            };
            return Weapon.withRarity(prefix + names[i], types[i],
                    5 + rnd.nextInt(10), rarity);
        }
    }

    private Item.Rarity rollRarity(Enemy enemy) {
        int roll = rnd.nextInt(100);

        // Элитные и боссы — лучше шансы
        int bonus = enemy.isElite() ? 30 : 0;
        if (enemy.isBoss()) bonus = 60;

        if (roll < 55 - bonus) return Item.Rarity.COMMON;
        if (roll < 85 - bonus / 2) return Item.Rarity.RARE;
        if (roll < 97 - bonus / 4) return Item.Rarity.EPIC;
        return Item.Rarity.LEGENDARY;
    }

    public void update(Player player) {
        List<Drop> toRemove = new ArrayList<>();

        for (Drop d : drops) {
            d.update();

            double dist = Math.hypot(player.x + player.width / 2.0 - d.x,
                    player.y + player.height / 2.0 - d.y);
            if (dist < 40) {
                if (d.isGold) {
                    player.addGold(d.gold);
                    SoundManager.play("pickup");
                    toRemove.add(d);
                } else if (d.item != null) {
                    if (player.getInventory().add(d.item)) {
                        SoundManager.play("pickup");
                        toRemove.add(d);
                    }
                }
            } else if (d.isExpired()) {
                toRemove.add(d);
            }
        }

        drops.removeAll(toRemove);
    }

    public List<Drop> getDrops() { return drops; }
}