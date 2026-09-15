package ui;

import entity.Player;
import item.Item;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class InventoryUI {
    private static final int COLS = 6;
    private static final int ROWS = 4;
    private static final int SLOT = 64;
    private static final int PADDING = 8;

    public void draw(Graphics2D g, Player player, int screenW, int screenH) {
        Renderer.enableQuality(g);

        // Затемнение
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenW, screenH);

        // Размеры панели
        int panelW = COLS * SLOT + PADDING * (COLS + 1);
        int panelH = ROWS * SLOT + PADDING * (ROWS + 1) + 60;

        int px = (screenW - panelW) / 2;
        int py = (screenH - panelH) / 2;

        // Панель
        g.setColor(new Color(30, 25, 40));
        g.fillRoundRect(px, py, panelW, panelH, 16, 16);

        g.setColor(new Color(180, 150, 80));
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(px, py, panelW, panelH, 16, 16);

        // Заголовок
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(new Color(255, 220, 100));
        g.drawString("Инвентарь", px + 20, py + 35);

        // Золото
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(255, 215, 0));
        String goldText = "Золото: " + player.getGold();
        int gw = g.getFontMetrics().stringWidth(goldText);
        g.drawString(goldText, px + panelW - gw - 20, py + 35);

        // Слоты
        List<Item> items = player.getInventory().getItems();

        int startX = px + PADDING;
        int startY = py + 60 + PADDING;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int sx = startX + col * (SLOT + PADDING);
                int sy = startY + row * (SLOT + PADDING);

                // Фон слота
                g.setColor(new Color(50, 45, 60));
                g.fillRoundRect(sx, sy, SLOT, SLOT, 8, 8);

                g.setColor(new Color(100, 90, 120));
                g.drawRoundRect(sx, sy, SLOT, SLOT, 8, 8);

                int idx = row * COLS + col;
                if (idx < items.size()) {
                    Item item = items.get(idx);

                    // Цветной фон по редкости
                    g.setColor(new Color(item.getRarityColor().getRed(),
                            item.getRarityColor().getGreen(),
                            item.getRarityColor().getBlue(), 40));
                    g.fillRoundRect(sx, sy, SLOT, SLOT, 8, 8);

                    // Иконка предмета (квадрат)
                    g.setColor(item.getRarityColor());
                    g.fillRect(sx + 16, sy + 16, 32, 32);
                    g.setColor(item.getRarityColor().brighter());
                    g.drawRect(sx + 16, sy + 16, 32, 32);

                    // Название мелко
                    g.setFont(new Font("Arial", Font.PLAIN, 9));
                    g.setColor(Color.WHITE);
                    String name = item.name;
                    if (name.length() > 10) name = name.substring(0, 9) + "…";
                    g.drawString(name, sx + 4, sy + SLOT - 4);
                }
            }
        }

        // Подсказка
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String hint = "I — закрыть | Предметов: " + items.size() + " / 24";
        int hw = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, px + (panelW - hw) / 2, py + panelH - 15);
    }
}