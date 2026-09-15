package ui;

import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class AchievementToast {
    private static class Toast {
        String text;
        long until;
    }

    private List<Toast> toasts = new ArrayList<>();

    public void show(String text) {
        Toast t = new Toast();
        t.text = text;
        t.until = System.currentTimeMillis() + 3000;
        toasts.add(t);
    }

    public void update() {
        toasts.removeIf(t -> System.currentTimeMillis() > t.until);
    }

    public void draw(Graphics2D g2, int screenW) {
        int y = 100;
        for (Toast t : toasts) {
            long remaining = t.until - System.currentTimeMillis();
            if (remaining <= 0) continue;

            // ⬇️ ЗАЩИТА от выхода за пределы
            float alpha = remaining / 500f;
            if (alpha > 1f) alpha = 1f;
            if (alpha < 0f) alpha = 0f;

            int alphaInt = (int) (alpha * 255);
            if (alphaInt < 0) alphaInt = 0;
            if (alphaInt > 255) alphaInt = 255;

            int w = 350;
            int h = 50;
            int x = screenW - w - 20;

            // Фон
            g2.setColor(new Color(0, 0, 0, (int)(220 * alpha)));
            g2.fillRoundRect(x, y, w, h, 12, 12);

            // Рамка
            g2.setColor(new Color(255, 220, 100, alphaInt));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, w, h, 12, 12);

            // Иконка
            g2.setFont(FontCache.arialBold(14));
            g2.setColor(new Color(255, 220, 100, alphaInt));
            g2.drawString("🏆", x + 15, y + 32);

            // Текст
            g2.setFont(FontCache.arialBold(13));
            g2.setColor(new Color(255, 255, 255, alphaInt));
            g2.drawString(t.text, x + 45, y + 32);

            y += 60;
        }
    }
}