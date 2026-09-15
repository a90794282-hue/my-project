package ui;

import craft.CraftSystem;
import entity.Player;
import game.FontCache;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

public class CraftUI {
    private int selected = 0;
    private List<CraftSystem.Recipe> recipes;

    public void open(CraftSystem cs) {
        this.recipes = cs.getRecipes();
        this.selected = 0;
    }

    public void handleKey(int code, Player player) {
        if (recipes == null || recipes.isEmpty()) return;
        int size = recipes.size();
        switch (code) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + size) % size;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % size;
            case KeyEvent.VK_ENTER -> {
                CraftSystem.Recipe r = recipes.get(selected);
                if (r.canCraft(player)) {
                    player.addGold(-r.goldCost);
                    player.getInventory().add(r.result);
                    game.SoundManager.play("levelup");
                }
            }
        }
    }

    public void draw(Graphics2D g2, int w, int h, Player player) {
        g2.setColor(new Color(15, 15, 25, 240));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 220, 100));
        g2.setFont(FontCache.arialBold(48));
        String title = "КРАФТ";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, 80);

        g2.setFont(FontCache.arialBold(16));
        g2.setColor(new Color(255, 220, 60));
        g2.drawString("Золото: " + player.getGold(), w - 200, 80);

        int startY = 150;
        for (int i = 0; i < recipes.size(); i++) {
            CraftSystem.Recipe r = recipes.get(i);
            int y = startY + i * 60;
            boolean sel = i == selected;
            if (sel) {
                g2.setColor(new Color(100, 80, 140, 200));
                g2.fillRoundRect(100, y - 30, w - 200, 50, 12, 12);
            }
            g2.setColor(sel ? Color.WHITE : new Color(200, 200, 200));
            g2.setFont(FontCache.arialBold(18));
            g2.drawString(r.resultName, 120, y);

            g2.setFont(FontCache.arial(14));
            g2.setColor(r.canCraft(player)
                    ? new Color(150, 255, 150)
                    : new Color(255, 100, 100));
            g2.drawString(r.goldCost + " золота", w - 300, y);
        }

        g2.setColor(new Color(150, 150, 150));
        g2.setFont(FontCache.arial(14));
        String hint = "↑↓ — выбор | Enter — создать | Esc — закрыть";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 40);
    }
}