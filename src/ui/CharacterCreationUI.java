package ui;

import entity.Appearance;
import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CharacterCreationUI {
    public boolean done = false;
    public Appearance appearance = new Appearance();

    private int selectedRow = 0;

    private final String[] ROW_LABELS = {
            "Имя", "Кожа", "Волосы", "Рубашка", "Штаны", ""
    };

    public int skinSel = 0;
    public int hairSel = 0;
    public int shirtSel = 0;
    public int pantsSel = 0;

    // ===== СВОЙ класс Star =====
    private static class Star {
        double x, y;
        int size;
        double speed;
        double twinklePhase;
    }

    private List<Star> stars = null;
    private Random rnd = new Random();
    private long startTime = System.currentTimeMillis();

    public CharacterCreationUI() {
        appearance.skinColor = Appearance.SKIN_PRESETS[0];
        appearance.hairColor = Appearance.HAIR_PRESETS[0];
        appearance.shirtColor = Appearance.SHIRT_PRESETS[0];
        appearance.pantsColor = Appearance.PANTS_PRESETS[0];
    }

    private void initStars(int w, int h) {
        if (stars != null) return;
        stars = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            Star s = new Star();
            s.x = rnd.nextDouble() * w;
            s.y = rnd.nextDouble() * h;
            s.size = 1 + rnd.nextInt(3);
            s.speed = 0.02 + rnd.nextDouble() * 0.08;
            s.twinklePhase = rnd.nextDouble() * Math.PI * 2;
            stars.add(s);
        }
    }

    public void handleKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> selectedRow = Math.max(0, selectedRow - 1);
            case KeyEvent.VK_DOWN -> selectedRow = Math.min(5, selectedRow + 1);

            case KeyEvent.VK_LEFT -> changeLeft();
            case KeyEvent.VK_RIGHT -> changeRight();

            case KeyEvent.VK_ENTER -> {
                if (selectedRow == 5) done = true;
            }

            case KeyEvent.VK_BACK_SPACE -> {
                if (selectedRow == 0 && !appearance.name.isEmpty()) {
                    appearance.name = appearance.name.substring(0, appearance.name.length() - 1);
                }
            }

            default -> {
                if (selectedRow == 0) {
                    char c = keyToChar(keyCode);
                    if (c != 0 && appearance.name.length() < 16) {
                        appearance.name += c;
                    }
                }
            }
        }
    }

    private void changeLeft() {
        switch (selectedRow) {
            case 1 -> {
                skinSel = (skinSel - 1 + Appearance.SKIN_PRESETS.length) % Appearance.SKIN_PRESETS.length;
                appearance.skinColor = Appearance.SKIN_PRESETS[skinSel];
            }
            case 2 -> {
                hairSel = (hairSel - 1 + Appearance.HAIR_PRESETS.length) % Appearance.HAIR_PRESETS.length;
                appearance.hairColor = Appearance.HAIR_PRESETS[hairSel];
            }
            case 3 -> {
                shirtSel = (shirtSel - 1 + Appearance.SHIRT_PRESETS.length) % Appearance.SHIRT_PRESETS.length;
                appearance.shirtColor = Appearance.SHIRT_PRESETS[shirtSel];
            }
            case 4 -> {
                pantsSel = (pantsSel - 1 + Appearance.PANTS_PRESETS.length) % Appearance.PANTS_PRESETS.length;
                appearance.pantsColor = Appearance.PANTS_PRESETS[pantsSel];
            }
        }
    }

    private void changeRight() {
        switch (selectedRow) {
            case 1 -> {
                skinSel = (skinSel + 1) % Appearance.SKIN_PRESETS.length;
                appearance.skinColor = Appearance.SKIN_PRESETS[skinSel];
            }
            case 2 -> {
                hairSel = (hairSel + 1) % Appearance.HAIR_PRESETS.length;
                appearance.hairColor = Appearance.HAIR_PRESETS[hairSel];
            }
            case 3 -> {
                shirtSel = (shirtSel + 1) % Appearance.SHIRT_PRESETS.length;
                appearance.shirtColor = Appearance.SHIRT_PRESETS[shirtSel];
            }
            case 4 -> {
                pantsSel = (pantsSel + 1) % Appearance.PANTS_PRESETS.length;
                appearance.pantsColor = Appearance.PANTS_PRESETS[pantsSel];
            }
        }
    }

    private char keyToChar(int keyCode) {
        if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            return (char) ('A' + (keyCode - KeyEvent.VK_A));
        }
        if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) {
            return (char) ('0' + (keyCode - KeyEvent.VK_0));
        }
        if (keyCode == KeyEvent.VK_SPACE) return ' ';
        return 0;
    }

    public void draw(Graphics2D g2, int w, int h) {
        initStars(w, h);
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

        drawTitle(g2, w, time);
        drawMenu(g2, w, h, time);
        drawPreview(g2, w, h, time);

        g2.setFont(FontCache.arial(13));
        g2.setColor(new Color(150, 150, 150, 200));
        String hint = "↑↓ — выбор   ◄ ► — изменить   Enter — начать";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 25);
    }

    private void drawTitle(Graphics2D g2, int w, long time) {
        double pulse = Math.sin(time / 800.0) * 0.05 + 1.0;

        g2.setFont(FontCache.arialBold((int) (48 * pulse)));
        String title = "СОЗДАНИЕ ПЕРСОНАЖА";
        int tw = g2.getFontMetrics().stringWidth(title);
        int tx = (w - tw) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(title, tx + 3, 83);

        GradientPaint gp = new GradientPaint(
                tx, 40, new Color(255, 240, 150),
                tx, 90, new Color(220, 140, 40));
        g2.setPaint(gp);
        g2.drawString(title, tx, 80);

        g2.setColor(new Color(255, 220, 100, 100));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(w / 2 - 250, 100, w / 2 + 250, 100);
    }

    private void drawMenu(Graphics2D g2, int w, int h, long time) {
        int mx = 80;
        int my = 150;
        int rowW = 380;
        int rowH = 50;
        int gap = 10;

        for (int i = 0; i < ROW_LABELS.length; i++) {
            int ry = my + i * (rowH + gap);
            boolean sel = (i == selectedRow);

            if (i == 5) {
                drawStartButton(g2, mx, ry, rowW, rowH, sel, time);
                continue;
            }

            if (sel) {
                double pulse = Math.sin(time / 300.0) * 0.03 + 1.0;
                int rw = (int) (rowW * pulse);
                int rx = mx - (rw - rowW) / 2;

                GradientPaint bg = new GradientPaint(
                        rx, ry, new Color(100, 80, 140),
                        rx, ry + rowH, new Color(60, 45, 90));
                g2.setPaint(bg);
                g2.fillRoundRect(rx, ry, rw, rowH, 12, 12);

                g2.setColor(new Color(255, 220, 100));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(rx, ry, rw, rowH, 12, 12);

                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(rx + 4, ry + 4, rw - 8, rowH / 3, 8, 8);
            } else {
                g2.setColor(new Color(30, 25, 45, 200));
                g2.fillRoundRect(mx, ry, rowW, rowH, 12, 12);

                g2.setColor(new Color(70, 65, 90));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(mx, ry, rowW, rowH, 12, 12);
            }

            g2.setFont(FontCache.arialBold(sel ? 20 : 18));
            g2.setColor(sel ? Color.WHITE : new Color(180, 180, 180));

            String label = ROW_LABELS[i];
            if (i == 0) {
                String nameText = label + ": " + appearance.name
                        + (sel ? "_" : "");
                g2.drawString(nameText, mx + 20, ry + rowH / 2 + 7);
            } else {
                g2.drawString(label, mx + 20, ry + rowH / 2 + 7);
            }

            if (i >= 1 && i <= 4) {
                Color color = switch (i) {
                    case 1 -> appearance.skinColor;
                    case 2 -> appearance.hairColor;
                    case 3 -> appearance.shirtColor;
                    case 4 -> appearance.pantsColor;
                    default -> Color.WHITE;
                };

                int cx = mx + rowW - 130;
                int cy = ry + 10;
                int cw = 30;
                int ch = 30;

                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(cx + 2, cy + 2, cw, ch, 6, 6);

                g2.setColor(color);
                g2.fillRoundRect(cx, cy, cw, ch, 6, 6);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(cx, cy, cw, ch, 6, 6);

                g2.setFont(FontCache.arialBold(20));
                g2.setColor(sel ? new Color(255, 220, 100) : new Color(120, 120, 120));
                g2.drawString("◄", cx + 40, ry + rowH / 2 + 7);
                g2.drawString("►", cx + 60, ry + rowH / 2 + 7);
            }
        }
    }

    private void drawStartButton(Graphics2D g2, int x, int y, int w, int h,
                                 boolean selected, long time) {
        double pulse = selected ? Math.sin(time / 300.0) * 0.05 + 1.0 : 1.0;
        int rw = (int) (w * pulse);
        int rh = (int) (h * pulse);
        int rx = x - (rw - w) / 2;
        int ry = y - (rh - h) / 2;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(rx + 3, ry + 5, rw, rh, 14, 14);

        GradientPaint gp = new GradientPaint(
                rx, ry, selected ? new Color(120, 220, 120) : new Color(70, 160, 70),
                rx, ry + rh, selected ? new Color(60, 150, 60) : new Color(40, 110, 40));
        g2.setPaint(gp);
        g2.fillRoundRect(rx, ry, rw, rh, 14, 14);

        g2.setColor(selected ? new Color(200, 255, 200) : new Color(100, 180, 100));
        g2.setStroke(new BasicStroke(selected ? 3f : 2f));
        g2.drawRoundRect(rx, ry, rw, rh, 14, 14);

        if (selected) {
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRoundRect(rx + 4, ry + 4, rw - 8, rh / 3, 10, 10);
        }

        g2.setFont(FontCache.arialBold(20));
        g2.setColor(Color.WHITE);
        String text = "НАЧАТЬ ИГРУ (Enter)";
        int tw = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, rx + (rw - tw) / 2, ry + rh / 2 + 8);
    }

    private void drawPreview(Graphics2D g2, int w, int h, long time) {
        int px = w - 320;
        int py = h / 2;
        int panelW = 240;
        int panelH = 360;

        GradientPaint bg = new GradientPaint(
                px - panelW / 2, py - panelH / 2, new Color(30, 25, 50, 220),
                px - panelW / 2, py + panelH / 2, new Color(15, 10, 30, 240));
        g2.setPaint(bg);
        g2.fillRoundRect(px - panelW / 2, py - panelH / 2, panelW, panelH, 20, 20);

        g2.setColor(new Color(180, 150, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(px - panelW / 2, py - panelH / 2, panelW, panelH, 20, 20);

        g2.setFont(FontCache.arialBold(16));
        g2.setColor(new Color(255, 220, 100));
        String header = "ПРЕВЬЮ";
        int hw = g2.getFontMetrics().stringWidth(header);
        g2.drawString(header, px - hw / 2, py - panelH / 2 + 30);

        g2.setColor(new Color(180, 150, 80, 100));
        g2.drawLine(px - panelW / 2 + 20, py - panelH / 2 + 42,
                px + panelW / 2 - 20, py - panelH / 2 + 42);

        double bob = Math.sin(time / 500.0) * 3;
        int cx = px;
        int cy = (int) (py - 20 + bob);
        int scale = 3;

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(cx - 30, cy + 40, 60, 15);

        g2.setColor(appearance.pantsColor);
        g2.fillRect(cx - 8 * scale, cy + 6 * scale, 6 * scale, 10 * scale);
        g2.fillRect(cx + 2 * scale, cy + 6 * scale, 6 * scale, 10 * scale);

        g2.setColor(new Color(40, 25, 15));
        g2.fillRect(cx - 8 * scale, cy + 14 * scale, 6 * scale, 3 * scale);
        g2.fillRect(cx + 2 * scale, cy + 14 * scale, 6 * scale, 3 * scale);

        g2.setColor(appearance.shirtColor);
        g2.fillRect(cx - 9 * scale, cy - 8 * scale, 18 * scale, 16 * scale);

        g2.setColor(new Color(60, 40, 20));
        g2.fillRect(cx - 9 * scale, cy + 4 * scale, 18 * scale, 3 * scale);

        g2.setColor(appearance.skinColor);
        g2.fillRect(cx - 13 * scale, cy - 6 * scale, 5 * scale, 12 * scale);
        g2.fillRect(cx + 8 * scale, cy - 6 * scale, 5 * scale, 12 * scale);

        g2.setColor(appearance.skinColor);
        g2.fillOval(cx - 9 * scale, cy - 22 * scale, 18 * scale, 16 * scale);

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(150, 100, 70));
        g2.drawOval(cx - 9 * scale, cy - 22 * scale, 18 * scale, 16 * scale);

        g2.setColor(appearance.hairColor);
        g2.fillArc(cx - 9 * scale, cy - 24 * scale, 18 * scale, 14 * scale, 0, 180);

        g2.setColor(Color.WHITE);
        g2.fillOval(cx - 6 * scale, cy - 16 * scale, 4 * scale, 4 * scale);
        g2.fillOval(cx + 2 * scale, cy - 16 * scale, 4 * scale, 4 * scale);
        g2.setColor(Color.BLACK);
        g2.fillOval(cx - 5 * scale, cy - 15 * scale, 2 * scale, 2 * scale);
        g2.fillOval(cx + 3 * scale, cy - 15 * scale, 2 * scale, 2 * scale);

        g2.setColor(new Color(120, 50, 50));
        g2.drawLine(cx - 2 * scale, cy - 10 * scale,
                cx + 2 * scale, cy - 10 * scale);

        g2.setFont(FontCache.arialBold(22));
        g2.setColor(Color.WHITE);
        String name = appearance.name.isEmpty() ? "..." : appearance.name;
        int nw = g2.getFontMetrics().stringWidth(name);

        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(name, px - nw / 2 + 2, py + panelH / 2 - 25);

        g2.setColor(Color.WHITE);
        g2.drawString(name, px - nw / 2, py + panelH / 2 - 27);
    }

    public void reset() {
        selectedRow = 0;
    }
}