package craft;

import entity.Player;
import game.Config;
import item.Item;
import item.Potion;
import item.Weapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftSystem {
    public static class Recipe {
        public String resultName;
        public Item result;
        public Map<String, Integer> ingredients = new HashMap<>();
        public int goldCost;

        public Recipe(Item result, int goldCost) {
            this.result = result;
            this.resultName = result.name;
            this.goldCost = goldCost;
        }

        public Recipe add(String name, int count) {
            ingredients.put(name, count);
            return this;
        }

        public boolean canCraft(Player p) {
            return p.getGold() >= goldCost;
        }
    }

    private List<Recipe> recipes = new ArrayList<>();

    public CraftSystem() {
        recipes.add(new Recipe(
                new Potion("Зелье здоровья", Potion.Kind.HEALTH, 30),
                Config.PRICE_POTION_HP
        ));

        recipes.add(new Recipe(
                new Potion("Зелье маны", Potion.Kind.MANA, 25),
                Config.PRICE_POTION_MANA
        ));

        recipes.add(new Recipe(
                Weapon.withRarity("Стальной меч", Weapon.Type.SWORD, 15, Item.Rarity.RARE),
                Config.PRICE_WEAPON_RARE
        ));

        recipes.add(new Recipe(
                Weapon.withRarity("Эпический лук", Weapon.Type.BOW, 25, Item.Rarity.EPIC),
                Config.PRICE_WEAPON_EPIC
        ));

        recipes.add(new Recipe(
                Weapon.withRarity("Легендарный посох", Weapon.Type.STAFF, 40, Item.Rarity.LEGENDARY),
                Config.PRICE_WEAPON_LEGENDARY
        ));
    }

    public List<Recipe> getRecipes() { return recipes; }
}