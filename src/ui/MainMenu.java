package ui;

import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainMenu {
    public enum Action { NONE, NEW_GAME, LOAD_GAME, SETTINGS, CREDITS, QUIT }

    private int selected = 0;
    private int hoveredIndex = -1;
    private final String[] items = {"Новая игра", "Загрузить", "Настройки", "Авторы", "Выход"};

    // ===== Звёзды =====
    private static class Star {
        double x, y;
        int size;
        double speed;
        double twinklePhase;
    }

    private List<Star> stars = new ArrayList<>();
    private Random rnd = new Random();

    // ===== Листья =====
    private static class Leaf {
        double x, y;
        double vx, vy;
        double rotation;
        double rotSpeed;
        int size;
        Color color;
    }

    private List<Leaf> leaves = new ArrayList<>();

    private BufferedImage previewSprite = null;
    private long startTime = System.currentTimeMillis();

    public MainMenu() {
    }

    private void initIfNeeded(int w, int h) {
        if (!stars.isEmpty()) return;

        for (int i = 0; i < 100; i++) {
            Star s = new Star();
            s.x = rnd.nextDouble() * w;
            s.y = rnd.nextDouble() * h;
            s.size = 1 + rnd.nextInt(3);
            s.speed = 0.02 + rnd.nextDouble() * 0.08;
            s.twinklePhase = rnd.nextDouble() * Math.PI * 2;
            stars.add(s);
        }

        for (int i = 0; i < 25; i++) {
            Leaf l = new Leaf();
            l.x = rnd.nextDouble() * w;
            l.y = rnd.nextDouble() * h;
            l.vx = (rnd.nextDouble() - 0.5) * 0.5;
            l.vy = 0.3 + rnd.nextDouble() * 0.7;
            l.rotation = rnd.nextDouble() * Math.PI * 2;
            l.rotSpeed = (rnd.nextDouble() - 0.5) * 0.05;
            l.size = 4 + rnd.nextInt(5);
            l.color = new Color(
                    80 + rnd.nextInt(80),
                    140 + rnd.nextInt(60),
                    60 + rnd.nextInt(50),
                    180 + rnd.nextInt(75)
            );
            leaves.add(l);
        }

        previewSprite = SpriteLoader.load("/sprites/player/walk_down_1.png");
        if (previewSprite == null) previewSprite = SpriteLoader.findAny("/sprites/player");
    }

    // ===== КЛАВИАТУРА =====
    public Action handleKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + items.length) % items.length;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % items.length;
            case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                return executeAction(selected);
            }
        }
        return Action.NONE;
    }

    public Action executeAction(int index) {
        return switch (index) {
            case 0 -> Action.NEW_GAME;
            case 1 -> Action.LOAD_GAME;
            case 2 -> Action.SETTINGS;
            case 3 -> Action.CREDITS;
            case 4 -> Action.QUIT;
            default -> Action.NONE;
        };
    }

    // ===== МЫШЬ =====
    public void updateHover(int mouseX, int mouseY, int w, int h) {
        hoveredIndex = -1;

        int buttonW = 320;
        int buttonH = 56;
        int startY = h / 2 + 20;
        int gap = 70;

        for (int i = 0; i < items.length; i++) {
            int bx = (w - buttonW) / 2;
            int by = startY + i * gap;

            if (mouseX >= bx && mouseX <= bx + buttonW
                    && mouseY >= by && mouseY <= by + buttonH) {
                hoveredIndex = i;
                selected = i;
                return;
            }
        }
    }

    public Action handleClick(int mouseX, int mouseY, int w, int h) {
        int buttonW = 320;
        int buttonH = 56;
        int startY = h / 2 + 20;
        int gap = 70;

        for (int i = 0; i < items.length; i++) {
            int bx = (w - buttonW) / 2;
            int by = startY + i * gap;

            if (mouseX >= bx && mouseX <= bx + buttonW
                    && mouseY >= by && mouseY <= by + buttonH) {
                selected = i;
                return executeAction(i);
            }
        }
        return Action.NONE;
    }

    // ===== ОБНОВЛЕНИЕ =====
    public void update(int w, int h) {
        for (Leaf l : leaves) {
            l.x += l.vx;
            l.y += l.vy;
            l.rotation += l.rotSpeed;
            l.vx += (rnd.nextDouble() - 0.5) * 0.05;
            l.vx = Math.max(-1, Math.min(1, l.vx));
            if (l.y > h + 20) {
                l.y = -20;
                l.x = rnd.nextDouble() * w;
            }
            if (l.x < -20) l.x = w + 20;
            if (l.x > w + 20) l.x = -20;
        }
    }

    // ===== ОТРИСОВКА =====
    public void draw(Graphics2D g2, int w, int h) {
        initIfNeeded(w, h);

        long time = System.currentTimeMillis();
        double t = (time - startTime) / 1000.0;

        GradientPaint sky = new GradientPaint(
                0, 0, new Color(20, 25, 50),
                0, h, new Color(8, 10, 20));
        g2.setPaint(sky);
        g2.fillRect(0, 0, w, h);

        for (Star s : stars) {
            double sx = (s.x - t * s.speed * 100) % w;
            if (sx < 0) sx += w;

            double twinkle = (Math.sin(time / 500.0 + s.twinklePhase) + 1) / 2;
            int alpha = (int) (100 + twinkle * 155);

            g2.setColor(new Color(255, 255, 255, alpha));
            g2.fillOval((int) sx, (int) s.y, s.size, s.size);
        }

        RadialGradientPaint fog = new RadialGradientPaint(
                new Point(w / 2, h),
                h * 0.6f,
                new float[]{0f, 1f},
                new Color[]{new Color(80, 100, 140, 60),
                        new Color(0, 0, 0, 0)});
        g2.setPaint(fog);
        g2.fillRect(0, h / 2, w, h / 2);

        for (Leaf l : leaves) {
            g2.setColor(l.color);
            java.awt.geom.AffineTransform old = g2.getTransform();
            g2.rotate(l.rotation, l.x, l.y);
            g2.fillOval((int) l.x, (int) l.y, l.size, l.size / 2);
            g2.setTransform(old);
        }

        drawTitle(g2, h, w, time);
        drawPreview(g2, w, h, time);
        drawButtons(g2, w, h, time);

        g2.setFont(FontCache.arial(11));
        g2.setColor(new Color(150, 150, 150, 180));
        g2.drawString("v1.0.0 — Java RPG", 15, h - 15);
        g2.drawString("© 2026", w - 80, h - 15);

        g2.setFont(FontCache.arial(12));
        g2.setColor(new Color(150, 150, 150, 200));
        String hint = "↑↓ или мышь — выбор   Enter или ЛКМ — подтвердить";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 20);
    }

    private void drawTitle(Graphics2D g2, int h, int w, long time) {
        int titleY = h / 4;
        double pulse = Math.sin(time / 800.0) * 0.05 + 1.0;

        g2.setFont(FontCache.arialBold(90));
        String title = "JAVA RPG";
        int tw = g2.getFontMetrics().stringWidth(title);
        int tx = (w - tw) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(title, tx + 4, titleY + 4);

        GradientPaint titleGradient = new GradientPaint(
                tx, titleY - 60, new Color(255, 240, 150),
                tx, titleY + 10, new Color(220, 140, 40));

        java.awt.Font oldFont = g2.getFont();
        g2.setFont(oldFont.deriveFont((float) (90 * pulse)));
        g2.setPaint(titleGradient);
        g2.drawString(title, tx, titleY);
        g2.setFont(oldFont);

        g2.setFont(FontCache.arialItalic(20));
        g2.setColor(new Color(200, 200, 220, 200));
        String sub = "Приключение ждёт тебя";
        int sw = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (w - sw) / 2, titleY + 45);
    }

    private void drawPreview(Graphics2D g2, int w, int h, long time) {
        if (previewSprite == null) return;

        int px = w - 200;
        int py = h / 2 - 50;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(px - 80, py + 60, 160, 30, 15, 15);
        g2.setColor(new Color(100, 80, 50, 200));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(px - 80, py + 60, 160, 30, 15, 15);

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillOval(px - 40, py + 60, 80, 12);

        double bob = Math.sin(time / 500.0) * 5;

        Renderer.enablePixelArt(g2);
        int spriteW = 128;
        int spriteH = 128;
        int sx = px - spriteW / 2;
        int sy = (int) (py - spriteH + bob);

        g2.drawImage(previewSprite, sx, sy, spriteW, spriteH, null);
    }

    private void drawButtons(Graphics2D g2, int w, int h, long time) {
        int buttonW = 320;
        int buttonH = 56;
        int startY = h / 2 + 20;
        int gap = 70;

        g2.setFont(FontCache.arialBold(22));

        for (int i = 0; i < items.length; i++) {
            int bx = (w - buttonW) / 2;
            int by = startY + i * gap;
            boolean sel = (i == selected) || (i == hoveredIndex);

            double pulse = sel ? Math.sin(time / 300.0) * 0.05 + 1.0 : 1.0;
            int realW = (int) (buttonW * pulse);
            int realH = (int) (buttonH * pulse);
            int rx = (w - realW) / 2;
            int ry = (int) (by - (realH - buttonH) / 2.0);

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(rx + 3, ry + 5, realW, realH, 14, 14);

            if (sel) {
                GradientPaint gp = new GradientPaint(
                        rx, ry, new Color(120, 90, 160),
                        rx, ry + realH, new Color(70, 50, 100));
                g2.setPaint(gp);
            } else {
                g2.setColor(new Color(40, 35, 55, 220));
            }
            g2.fillRoundRect(rx, ry, realW, realH, 14, 14);

            if (sel) {
                g2.setColor(new Color(255, 220, 100));
                g2.setStroke(new BasicStroke(3f));
            } else {
                g2.setColor(new Color(80, 80, 100));
                g2.setStroke(new BasicStroke(2f));
            }
            g2.drawRoundRect(rx, ry, realW, realH, 14, 14);

            if (sel) {
                g2.setColor(new Color(255, 220, 100));
                g2.setFont(FontCache.arialBold(28));
                g2.drawString("▶", rx - 35, ry + realH / 2 + 10);
            }

            g2.setFont(FontCache.arialBold(sel ? 24 : 22));
            g2.setColor(sel ? Color.WHITE : new Color(180, 180, 180));

            String text = items[i];

            if (sel) {
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(rx + 4, ry + 4, realW - 8, realH / 3, 10, 10);
                g2.setColor(Color.WHITE);
            }

            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, rx + (realW - tw) / 2, ry + realH / 2 + 8);
        }
    }

    public void reset() {
        selected = 0;
        hoveredIndex = -1;
    }

    public int getSelected() { return selected; }
    public int getHoveredIndex() { return hoveredIndex; }
    public String[] getItems() { return items; }
}