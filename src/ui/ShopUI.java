package ui;

import entity.Player;
import game.FontCache;
import item.Item;
import shop.Shop;
import shop.ShopItem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ShopUI {
    public enum Mode { BUY, SELL }

    private Shop shop;
    private Mode mode = Mode.BUY;
    private int selected = 0;
    private boolean visible = false;
    private String message = "";
    private long messageUntil = 0;

    public void open(Shop shop) {
        this.shop = shop;
        this.visible = true;
        this.mode = Mode.BUY;
        this.selected = 0;
    }

    public void close() {
        visible = false;
    }

    public boolean isVisible() { return visible; }

    public void handleKey(int code, Player player) {
        if (!visible) return;

        List<?> items = getCurrentItems(player);
        int size = items.size();
        if (size == 0) size = 1;

        switch (code) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + size) % size;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % size;
            case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> {
                mode = (mode == Mode.BUY) ? Mode.SELL : Mode.BUY;
                selected = 0;
            }
            case KeyEvent.VK_ENTER -> {
                if (mode == Mode.BUY) {
                    buySelected(player);
                } else {
                    sellSelected(player);
                }
            }
            case KeyEvent.VK_ESCAPE -> close();
        }
    }

    private void buySelected(Player player) {
        List<ShopItem> items = shop.getItems();
        if (selected >= items.size()) return;
        ShopItem si = items.get(selected);

        if (!si.canBuy()) {
            showMessage("Нет в наличии");
            return;
        }
        if (player.getGold() < si.buyPrice) {
            showMessage("Не хватает золота");
            return;
        }
        shop.buy(player, si);
        showMessage("Куплено: " + si.item.name);
        game.SoundManager.play("pickup");
    }

    private void sellSelected(Player player) {
        List<Item> inv = player.getInventory().getItems();
        if (selected >= inv.size()) return;
        Item item = inv.get(selected);
        int price = getItemPrice(item);

        shop.sell(player, item, price);
        showMessage("Продано: " + item.name + " за " + price);
        game.SoundManager.play("pickup");
    }

    private int getItemPrice(Item item) {
        if (item instanceof item.Potion p) return p.value;
        if (item instanceof item.Weapon w) {
            return (int) (w.baseDamage * 10 * w.rarity.powerMultiplier);
        }
        return 10;
    }

    private List<?> getCurrentItems(Player player) {
        return mode == Mode.BUY ? shop.getItems() : player.getInventory().getItems();
    }

    private void showMessage(String msg) {
        this.message = msg;
        this.messageUntil = System.currentTimeMillis() + 2000;
    }

    public void draw(Graphics2D g2, int w, int h, Player player) {
        if (!visible) return;

        // Затемнение
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, w, h);

        int panelW = 900;
        int panelH = 600;
        int px = (w - panelW) / 2;
        int py = (h - panelH) / 2;

        // Панель
        g2.setColor(new Color(20, 15, 35));
        g2.fillRoundRect(px, py, panelW, panelH, 20, 20);
        g2.setColor(new Color(180, 150, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(px, py, panelW, panelH, 20, 20);

        // Заголовок
        g2.setColor(new Color(255, 220, 100));
        g2.setFont(FontCache.arialBold(32));
        String title = shop.name;
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, px + (panelW - tw) / 2, py + 45);

        // Золото
        g2.setFont(FontCache.arialBold(20));
        g2.setColor(new Color(255, 220, 60));
        g2.drawString("💰 " + player.getGold(), px + panelW - 180, py + 45);

        // Вкладки
        g2.setFont(FontCache.arialBold(18));
        int tabY = py + 75;

        g2.setColor(mode == Mode.BUY ? new Color(255, 220, 100) : new Color(120, 120, 120));
        g2.drawString("КУПИТЬ", px + 30, tabY);

        g2.setColor(mode == Mode.SELL ? new Color(255, 220, 100) : new Color(120, 120, 120));
        g2.drawString("ПРОДАТЬ", px + 180, tabY);

        g2.setColor(new Color(180, 150, 80));
        g2.drawLine(px + 20, tabY + 10, px + panelW - 20, tabY + 10);

        // Список
        List<?> items = getCurrentItems(player);
        int listY = tabY + 35;
        int rowH = 45;

        g2.setFont(FontCache.arial(15));

        for (int i = 0; i < items.size() && i < 10; i++) {
            int iy = listY + i * rowH;
            boolean sel = i == selected;

            // Фон
            if (sel) {
                g2.setColor(new Color(100, 80, 140, 200));
                g2.fillRoundRect(px + 20, iy - 25, panelW - 40, rowH - 5, 10, 10);
            }

            Object obj = items.get(i);

            if (obj instanceof ShopItem si) {
                // Цвет по редкости
                g2.setColor(si.item.getRarityColor());
                g2.setFont(FontCache.arialBold(15));
                g2.drawString(si.item.name, px + 40, iy);

                g2.setFont(FontCache.arial(12));
                g2.setColor(new Color(180, 180, 180));
                g2.drawString(si.item.getDescription(), px + 400, iy);

                // Цена
                g2.setColor(new Color(255, 220, 60));
                g2.setFont(FontCache.arialBold(14));
                g2.drawString(si.buyPrice + "💰", px + panelW - 100, iy);

                // Наличие
                if (!si.infinite) {
                    g2.setColor(si.stock > 0 ? Color.GREEN : Color.RED);
                    g2.setFont(FontCache.arial(11));
                    g2.drawString("x" + si.stock, px + panelW - 50, iy);
                }
            } else if (obj instanceof Item item) {
                g2.setColor(item.getRarityColor());
                g2.setFont(FontCache.arialBold(15));
                g2.drawString(item.name, px + 40, iy);

                g2.setFont(FontCache.arial(12));
                g2.setColor(new Color(180, 180, 180));
                g2.drawString(item.getDescription(), px + 400, iy);

                // Цена продажи
                g2.setColor(new Color(150, 255, 150));
                g2.setFont(FontCache.arialBold(14));
                g2.drawString(getItemPrice(item) + "💰", px + panelW - 100, iy);
            }
        }

        // Пустой инвентарь
        if (items.isEmpty()) {
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(FontCache.arial(16));
            String empty = mode == Mode.BUY ? "Нет товаров" : "Инвентарь пуст";
            int ew = g2.getFontMetrics().stringWidth(empty);
            g2.drawString(empty, px + (panelW - ew) / 2, py + panelH / 2);
        }

        // Сообщение
        if (System.currentTimeMillis() < messageUntil) {
            g2.setColor(new Color(255, 220, 100));
            g2.setFont(FontCache.arialBold(16));
            int mw = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, px + (panelW - mw) / 2, py + panelH - 60);
        }

        // Подсказка
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(FontCache.arial(13));
        String hint = "↑↓ — выбор | ←→ — вкладка | Enter — купить/продать | Esc — закрыть";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, px + (panelW - hw) / 2, py + panelH - 25);
    }
}