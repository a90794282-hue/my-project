package shop;

import item.Item;

public class ShopItem {
    public Item item;
    public int buyPrice;
    public int sellPrice;
    public int stock;
    public boolean infinite;

    public ShopItem(Item item, int buyPrice, int sellPrice, int stock) {
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.infinite = (stock < 0);
    }

    public boolean canBuy() {
        return infinite || stock > 0;
    }

    public void onBuy() {
        if (!infinite) stock--;
    }
}