package shop;

import entity.Player;
import item.Item;
import item.Potion;
import item.Weapon;
import shop.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    public String name = "Магазин";
    private List<ShopItem> items = new ArrayList<>();

    public Shop(String name) {
        this.name = name;
    }

    public void addItem(Item item, int buyPrice, int sellPrice, int stock) {
        items.add(new ShopItem(item, buyPrice, sellPrice, stock));
    }

    public boolean buy(Player player, ShopItem shopItem) {
        if (!shopItem.canBuy()) return false;
        if (player.getGold() < shopItem.buyPrice) return false;

        player.addGold(-shopItem.buyPrice);
        player.getInventory().add(shopItem.item);
        shopItem.onBuy();
        return true;
    }

    public boolean sell(Player player, Item item, int price) {
        if (!player.getInventory().remove(item)) return false;
        player.addGold(price);
        return true;
    }

    public List<ShopItem> getItems() { return items; }

    public static Shop createDefaultShop() {
        Shop shop = new Shop("Торговец");

        // Зелья — бесконечные
        shop.addItem(new Potion("Зелье HP (малое)",
                Potion.Kind.HEALTH, 30), 50, 25, -1);
        shop.addItem(new Potion("Зелье HP (большое)",
                Potion.Kind.HEALTH, 80), 150, 75, -1);
        shop.addItem(new Potion("Зелье маны",
                Potion.Kind.MANA, 30), 60, 30, -1);

        // Оружие — ограничено
        shop.addItem(Weapon.withRarity("Железный меч",
                        Weapon.Type.SWORD, 12, Item.Rarity.COMMON),
                200, 100, 1);
        shop.addItem(Weapon.withRarity("Закалённый меч",
                        Weapon.Type.SWORD, 18, Item.Rarity.RARE),
                600, 300, 1);
        shop.addItem(Weapon.withRarity("Древний меч",
                        Weapon.Type.SWORD, 28, Item.Rarity.EPIC),
                1500, 750, 1);
        shop.addItem(Weapon.withRarity("Легендарный меч",
                        Weapon.Type.SWORD, 50, Item.Rarity.LEGENDARY),
                5000, 2500, 1);

        shop.addItem(Weapon.withRarity("Охотничий лук",
                        Weapon.Type.BOW, 15, Item.Rarity.COMMON),
                250, 125, 1);
        shop.addItem(Weapon.withRarity("Эльфийский лук",
                        Weapon.Type.BOW, 35, Item.Rarity.EPIC),
                2000, 1000, 1);

        shop.addItem(Weapon.withRarity("Ученический посох",
                        Weapon.Type.STAFF, 15, Item.Rarity.COMMON),
                250, 125, 1);
        shop.addItem(Weapon.withRarity("Посох архимага",
                        Weapon.Type.STAFF, 45, Item.Rarity.LEGENDARY),
                4500, 2250, 1);

        return shop;
    }
}