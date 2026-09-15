package ui;

import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class PauseMenu {
    public enum Action { NONE, RESUME, SETTINGS, MAIN_MENU, QUIT }

    private int selected = 0;
    private int hoveredIndex = -1;
    private final String[] items = {"Продолжить", "Настройки", "В главное меню", "Выход"};

    // ===== КЛАВИАТУРА =====
    public Action handleKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + items.length) % items.length;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % items.length;
            case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                return executeAction(selected);
            }
            case KeyEvent.VK_ESCAPE -> { return Action.RESUME; }
        }
        return Action.NONE;
    }

    public Action executeAction(int i) {
        return switch (i) {
            case 0 -> Action.RESUME;
            case 1 -> Action.SETTINGS;
            case 2 -> Action.MAIN_MENU;
            case 3 -> Action.QUIT;
            default -> Action.NONE;
        };
    }

    // ===== МЫШЬ =====
    public void updateHover(int mx, int my, int w, int h) {
        hoveredIndex = -1;

        int buttonW = 400;
        int buttonH = 44;
        int baseY = h / 2 - 60;
        int gap = 55;

        for (int i = 0; i < items.length; i++) {
            int x = (w - buttonW) / 2;
            int y = baseY + i * gap - 30;

            if (mx >= x && mx <= x + buttonW && my >= y && my <= y + buttonH) {
                hoveredIndex = i;
                selected = i;
                return;
            }
        }
    }

    public Action handleClick(int mx, int my, int w, int h) {
        int buttonW = 400;
        int buttonH = 44;
        int baseY = h / 2 - 60;
        int gap = 55;

        for (int i = 0; i < items.length; i++) {
            int x = (w - buttonW) / 2;
            int y = baseY + i * gap - 30;

            if (mx >= x && mx <= x + buttonW && my >= y && my <= y + buttonH) {
                selected = i;
                return executeAction(i);
            }
        }
        return Action.NONE;
    }

    // ===== ОТРИСОВКА =====
    public void draw(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 220, 100));
        g2.setFont(FontCache.arialBold(48));
        String title = "ПАУЗА";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, h / 2 - 150);

        g2.setFont(FontCache.arialBold(24));
        for (int i = 0; i < items.length; i++) {
            int y = h / 2 - 60 + i * 55;
            boolean sel = (i == selected) || (i == hoveredIndex);

            int buttonW = 400;
            int buttonH = 44;
            int x = (w - buttonW) / 2;

            if (sel) {
                g2.setColor(new Color(100, 80, 140, 200));
                g2.fillRoundRect(x, y - 30, buttonW, buttonH, 12, 12);
                g2.setColor(new Color(255, 220, 100));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x, y - 30, buttonW, buttonH, 12, 12);
            }

            g2.setColor(sel ? Color.WHITE : new Color(180, 180, 180));
            int iw = g2.getFontMetrics().stringWidth(items[i]);
            g2.drawString(items[i], (w - iw) / 2, y);
        }

        g2.setColor(new Color(150, 150, 150));
        g2.setFont(FontCache.arial(14));
        String hint = "↑↓ или мышь — выбор   Enter или ЛКМ — выбрать   Esc — продолжить";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 60);
    }

    public void reset() {
        selected = 0;
        hoveredIndex = -1;
    }
}