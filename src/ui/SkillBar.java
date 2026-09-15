package ui;

import entity.Player;
import magic.Spell;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class SkillBar {
    private static final int SLOT = 56;
    private static final int PADDING = 6;

    public void draw(Graphics2D g, Player player, int screenW, int screenH) {
        Renderer.enableQuality(g);

        List<Spell> spells = player.getSpells();

        int count = 4;
        int totalW = count * SLOT + (count - 1) * PADDING;
        int startX = (screenW - totalW) / 2;
        int startY = screenH - SLOT - 20;

        // ===== Слот 1: Зелье HP (Q) =====
        drawSlot(g, startX, startY, 0, "Q", new Color(220, 60, 60), "HP", 0);

        // ===== Слот 2: Огненный шар (R) =====
        if (spells.size() > 0) {
            Spell s = spells.get(0);
            drawSlot(g, startX + SLOT + PADDING, startY, 1, "R",
                    new Color(255, 140, 0), s.name, s.currentCooldown);
        } else {
            drawEmptySlot(g, startX + SLOT + PADDING, startY, 1, "R");
        }

        // ===== Слот 3: Рывок (F) =====
        drawSlot(g, startX + (SLOT + PADDING) * 2, startY, 2, "F",
                new Color(80, 180, 255), "Рывок", 0);

        // ===== Слот 4: Свободно =====
        drawEmptySlot(g, startX + (SLOT + PADDING) * 3, startY, 3, "G");
    }

    private void drawSlot(Graphics2D g, int x, int y, int index,
                          String key, Color color, String label, int cooldown) {
        // Фон
        g.setColor(new Color(30, 25, 40, 220));
        g.fillRoundRect(x, y, SLOT, SLOT, 10, 10);

        // Цветной ореол
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        g.fillRoundRect(x + 3, y + 3, SLOT - 6, SLOT - 6, 8, 8);

        // Иконка
        g.setColor(color);
        g.fillOval(x + 12, y + 12, SLOT - 24, SLOT - 24);

        g.setColor(color.brighter());
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x + 12, y + 12, SLOT - 24, SLOT - 24);

        // Буква
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        int tw = g.getFontMetrics().stringWidth(label.substring(0, Math.min(2, label.length())));
        g.drawString(label.substring(0, Math.min(2, label.length())),
                x + SLOT / 2 - tw / 2, y + SLOT / 2 + 7);

        // Кулдаун
        if (cooldown > 0) {
            g.setColor(new Color(0, 0, 0, 180));
            int cdPercent = cooldown * 100 / 60;
            g.fillRoundRect(x + 3, y + 3, SLOT - 6, (SLOT - 6) * cdPercent / 100, 8, 8);
        }

        // Рамка
        g.setColor(new Color(200, 180, 100));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, SLOT, SLOT, 10, 10);

        // Клавиша сверху
        g.setColor(new Color(255, 220, 100));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        int kw = g.getFontMetrics().stringWidth(key);
        g.drawString(key, x + SLOT / 2 - kw / 2, y - 5);
    }

    private void drawEmptySlot(Graphics2D g, int x, int y, int index, String key) {
        g.setColor(new Color(30, 25, 40, 150));
        g.fillRoundRect(x, y, SLOT, SLOT, 10, 10);

        g.setColor(new Color(80, 70, 90));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, SLOT, SLOT, 10, 10);

        g.setColor(new Color(100, 90, 110));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        int kw = g.getFontMetrics().stringWidth(key);
        g.drawString(key, x + SLOT / 2 - kw / 2, y - 5);
    }
}