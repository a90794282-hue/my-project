package ui;

import entity.Player;
import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class HUD {
    public void draw(Graphics2D g, Player p, int screenW) {
        Renderer.enableSmoothUI(g);

        // Левая панель: HP / MP / XP
        drawLeftPanel(g, p);

        // Панель характеристик (справа от левой панели)
        drawStatsPanel(g, p);

        // Очки прокачки
        if (p.getStatPoints() > 0) {
            drawStatPoints(g, p);
        }
    }

    // ============= ЛЕВАЯ ПАНЕЛЬ =============
    private void drawLeftPanel(Graphics2D g, Player p) {
        int px = 20, py = 20;
        int panelW = 260, panelH = 100;

        // Фон панели
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRoundRect(px - 5, py - 5, panelW + 10, panelH + 10, 14, 14);
        g.setColor(new Color(180, 150, 80, 180));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px - 5, py - 5, panelW + 10, panelH + 10, 14, 14);

        // HP
        drawBar(g, px, py, panelW, 22, "HP",
                p.hp, p.maxHp,
                new Color(180, 30, 30), new Color(255, 80, 80));

        // MP
        drawBar(g, px, py + 30, panelW, 18, "MP",
                p.mana, p.maxMana,
                new Color(30, 60, 180), new Color(80, 140, 255));

        // XP
        drawBar(g, px, py + 58, panelW, 12, "XP",
                p.getXp(), p.getXpToNext(),
                new Color(150, 120, 20), new Color(255, 220, 60));

        // Уровень и класс
        g.setFont(FontCache.arialBold(16));
        g.setColor(Color.WHITE);
        String title = "Ур. " + p.getLevel() + " — " + p.getClassName();
        g.drawString(title, px, py + 92);
    }

    private void drawBar(Graphics2D g, int x, int y, int w, int h,
                         String label, double current, double max,
                         Color dark, Color light) {
        // Иконка-подпись
        g.setFont(FontCache.arialBold(11));
        g.setColor(Color.WHITE);
        g.drawString(label, x, y + 14);

        int barX = x + 30;
        int barW = w - 30;

        // Фон бара
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(barX, y, barW, h, 8, 8);

        double pct = Math.max(0, Math.min(1, current / max));

        // Градиент
        java.awt.GradientPaint gp = new java.awt.GradientPaint(
                barX, y, light,
                barX, y + h, dark);
        g.setPaint(gp);
        g.fillRoundRect(barX + 1, y + 1, (int) ((barW - 2) * pct), h - 2, 6, 6);

        // Блик
        g.setColor(new Color(255, 255, 255, 80));
        g.fillRoundRect(barX + 3, y + 2,
                Math.max(0, (int) ((barW - 2) * pct) - 4),
                (h - 2) / 3, 4, 4);

        // Рамка
        g.setColor(new Color(255, 255, 255, 130));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(barX, y, barW, h, 8, 8);

        // Числа
        g.setFont(FontCache.arial(10));
        g.setColor(new Color(255, 255, 255, 220));
        String nums = (int) current + "/" + (int) max;
        int nw = g.getFontMetrics().stringWidth(nums);
        g.drawString(nums, barX + barW - nw - 6, y + h - 5);
    }

    // ============= ПАНЕЛЬ ХАРАКТЕРИСТИК =============
    // Сдвинута правее, чтобы не перекрывать левую панель
    private void drawStatsPanel(Graphics2D g, Player p) {
        int px = 320;   // ⬅️ сдвинуто вправо (было 260)
        int py = 15;
        int pw = 200;
        int ph = 140;

        // Фон с градиентом
        java.awt.GradientPaint bg = new java.awt.GradientPaint(
                px, py, new Color(30, 25, 50, 210),
                px, py + ph, new Color(15, 10, 30, 230));
        g.setPaint(bg);
        g.fillRoundRect(px, py, pw, ph, 14, 14);

        // Обводка
        g.setColor(new Color(100, 100, 160, 200));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px, py, pw, ph, 14, 14);

        // Заголовок
        g.setFont(FontCache.arialBold(13));
        g.setColor(new Color(255, 220, 100));
        g.drawString("ХАРАКТЕРИСТИКИ", px + 12, py + 22);

        // Разделитель
        g.setColor(new Color(100, 100, 160, 100));
        g.drawLine(px + 10, py + 28, px + pw - 10, py + 28);

        // Статы
        int y = py + 50;
        drawStat(g, px + 12, y, "Сила",         p.getStats().getStrength(),     new Color(255, 100, 100)); y += 18;
        drawStat(g, px + 12, y, "Ловкость",     p.getStats().getDexterity(),    new Color(100, 255, 100)); y += 18;
        drawStat(g, px + 12, y, "Интеллект",    p.getStats().getIntelligence(), new Color(100, 150, 255)); y += 18;
        drawStat(g, px + 12, y, "Выносливость", p.getStats().getVitality(),     new Color(255, 200, 100)); y += 22;

        // Крит
        g.setFont(FontCache.arial(11));
        g.setColor(new Color(255, 220, 100));
        g.drawString("Крит: " + p.getStats().getCritChance() + "%",
                px + 12, py + ph - 8);
    }

    private void drawStat(Graphics2D g, int x, int y, String label, int value, Color color) {
        g.setColor(new Color(200, 200, 220));
        g.setFont(FontCache.arial(12));
        g.drawString(label + ":", x, y);

        g.setColor(color);
        g.setFont(FontCache.arialBold(13));
        g.drawString(String.valueOf(value), x + 120, y);
    }

    // ============= ОЧКИ ПРОКАЧКИ =============
    private void drawStatPoints(Graphics2D g, Player p) {
        // Под левой панелью
        int x = 20, y = 145;
        int w = 260, h = 28;

        // Пульсация
        long time = System.currentTimeMillis();
        double pulse = Math.sin(time / 300.0) * 0.15 + 1.0;

        // Фон
        g.setColor(new Color(255, 220, 60, (int) (200 * pulse)));
        g.fillRoundRect(x, y, w, h, 10, 10);

        // Текст
        g.setColor(Color.BLACK);
        g.setFont(FontCache.arialBold(12));
        String text = "Очки: " + p.getStatPoints()
                + "  [1-Сила 2-Ловк 3-Инт 4-Вын]";
        int tw = g.getFontMetrics().stringWidth(text);
        g.drawString(text, x + (w - tw) / 2, y + 19);
    }
}