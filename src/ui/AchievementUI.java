package ui;

import achievement.AchievementManager;
import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AchievementUI {
    public void draw(Graphics2D g2, int w, int h, AchievementManager am) {
        g2.setColor(new Color(15, 15, 25, 240));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 220, 100));
        g2.setFont(FontCache.arialBold(42));
        String title = "ДОСТИЖЕНИЯ";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, 60);

        List<AchievementManager.Achievement> all = am.getAll();
        int unlocked = 0;
        for (AchievementManager.Achievement a : all) if (a.unlocked) unlocked++;

        g2.setFont(FontCache.arialBold(16));
        g2.setColor(new Color(150, 220, 255));
        String count = unlocked + " / " + all.size();
        int cw = g2.getFontMetrics().stringWidth(count);
        g2.drawString(count, (w - cw) / 2, 90);

        // Прогресс-бар
        int barW = 400;
        int barH = 12;
        int barX = (w - barW) / 2;
        int barY = 105;
        g2.setColor(new Color(40, 40, 60));
        g2.fillRoundRect(barX, barY, barW, barH, 6, 6);
        g2.setColor(new Color(255, 220, 60));
        g2.fillRoundRect(barX, barY, (int)(barW * (unlocked / (double)all.size())), barH, 6, 6);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(barX, barY, barW, barH, 6, 6);

        // Группировка по категориям
        Map<String, List<AchievementManager.Achievement>> byCategory = new LinkedHashMap<>();
        for (AchievementManager.Achievement a : all) {
            byCategory.computeIfAbsent(a.category, k -> new ArrayList<>()).add(a);
        }

        int startY = 145;
        int colX = 80;
        int colWidth = (w - 160) / 2;

        int col = 0;
        int y = startY;

        for (Map.Entry<String, List<AchievementManager.Achievement>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<AchievementManager.Achievement> list = entry.getValue();

            // Заголовок категории
            int px = colX + col * (colWidth + 20);
            g2.setFont(FontCache.arialBold(18));
            g2.setColor(new Color(255, 220, 100));
            g2.drawString(category, px, y);
            y += 25;

            // Список
            for (AchievementManager.Achievement a : list) {
                if (y > h - 50) {
                    col++;
                    y = startY;
                    px = colX + col * (colWidth + 20);
                }

                if (a.secret && !a.unlocked) {
                    g2.setColor(new Color(80, 80, 80));
                    g2.setFont(FontCache.arialBold(13));
                    g2.drawString("? ???", px, y);
                    g2.setColor(new Color(120, 120, 120));
                    g2.setFont(FontCache.arial(11));
                    g2.drawString("Секретное достижение", px + 10, y + 14);
                } else if (a.unlocked) {
                    g2.setColor(new Color(80, 220, 80));
                    g2.setFont(FontCache.arialBold(13));
                    g2.drawString("✓ " + a.name, px, y);
                    g2.setColor(new Color(180, 220, 180));
                    g2.setFont(FontCache.arial(11));
                    g2.drawString(a.description, px + 10, y + 14);
                } else {
                    g2.setColor(new Color(150, 150, 150));
                    g2.setFont(FontCache.arialBold(13));
                    g2.drawString("○ " + a.name, px, y);
                    g2.setColor(new Color(120, 120, 120));
                    g2.setFont(FontCache.arial(11));
                    g2.drawString(a.description, px + 10, y + 14);
                }

                y += 30;
            }

            y += 10;
        }

        g2.setColor(new Color(150, 150, 150));
        g2.setFont(FontCache.arial(14));
        String hint = "Esc — закрыть";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 20);
    }
}