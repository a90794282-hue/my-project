package ui;

import game.FontCache;
import world.TimeOfDay;

import java.awt.Color;
import java.awt.Graphics2D;

public class TimeOfDayUI {
    public void draw(Graphics2D g2, TimeOfDay time, int screenW) {
        int px = screenW - 120;
        int py = 20;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(px, py, 100, 40, 10, 10);

        g2.setColor(new Color(180, 150, 80));
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawRoundRect(px, py, 100, 40, 10, 10);

        g2.setFont(FontCache.arialBold(16));
        g2.setColor(Color.WHITE);
        String str = time.getTimeString();
        int tw = g2.getFontMetrics().stringWidth(str);
        g2.drawString(str, px + (100 - tw) / 2, py + 26);

        // Иконка
        String icon = time.isNight() ? "🌙" : "☀";
        g2.setFont(FontCache.arial(18));
        g2.drawString(icon, px + 10, py + 28);
    }
}