package ui;

import game.ColorCache;
import game.FontCache;
import game.FrameCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Renderer {

    private static final BasicStroke STROKE_1 = new BasicStroke(1f);
    private static final BasicStroke STROKE_2 = new BasicStroke(2f);
    private static final BasicStroke STROKE_3 = new BasicStroke(3f);

    public static BasicStroke stroke1() { return STROKE_1; }
    public static BasicStroke stroke2() { return STROKE_2; }
    public static BasicStroke stroke3() { return STROKE_3; }

    private static final Color HP_HIGH = ColorCache.rgb(60, 200, 60);
    private static final Color HP_MID = ColorCache.rgb(230, 200, 60);
    private static final Color HP_LOW = ColorCache.rgb(230, 60, 60);

    public static void enableQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }

    public static void enablePixelArt(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    public static void enableSmoothUI(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    // ============= ТЕНИ =============
    public static void drawShadow(Graphics2D g, double x, double y, int w, int h) {
        for (int i = 6; i > 0; i--) {
            int alpha = 15 + i * 8;
            g.setColor(ColorCache.blackA(alpha));
            g.fillOval((int) (x - i), (int) (y + h * 0.85),
                    w + i * 2, h / 2 + i);
        }
        g.setColor(ColorCache.blackA(80));
        g.fillOval((int) x, (int) (y + h * 0.9), w, h / 3);
    }

    // ============= СВЕЧЕНИЕ =============
    public static void drawGlow(Graphics2D g, double x, double y, int radius, Color color) {
        int r = color.getRed();
        int gg = color.getGreen();
        int b = color.getBlue();
        for (int i = 8; i > 0; i--) {
            int alpha = 15 + i * 4;
            g.setColor(new Color(r, gg, b, alpha));
            int rr = radius + i * 4;
            g.fillOval((int) (x - rr / 2.0), (int) (y - rr / 2.0), rr, rr);
        }
    }

    // ============= HP BAR =============
    public static void drawHealthBar(Graphics2D g, int x, int y, int w, int h,
                                     double current, double max) {
        g.setColor(ColorCache.blackA(150));
        g.fillRoundRect(x - 2, y - 2, w + 4, h + 4, 6, 6);

        g.setColor(new Color(40, 0, 0));
        g.fillRoundRect(x, y, w, h, 4, 4);

        double pct = Math.max(0, Math.min(1, current / max));
        Color bar = pct > 0.6 ? HP_HIGH : pct > 0.3 ? HP_MID : HP_LOW;

        GradientPaint gp = new GradientPaint(
                x, y, bar.brighter(),
                x, y + h, bar.darker()
        );
        g.setPaint(gp);
        g.fillRoundRect(x, y, (int) (w * pct), h, 4, 4);

        g.setColor(ColorCache.whiteA(80));
        g.fillRoundRect(x + 2, y + 1, Math.max(0, (int) (w * pct) - 4), h / 3, 2, 2);

        g.setColor(ColorCache.whiteA(120));
        g.setStroke(STROKE_1);
        g.drawRoundRect(x, y, w, h, 4, 4);
    }

    // ============= MANA BAR =============
    public static void drawManaBar(Graphics2D g, int x, int y, int w, int h,
                                   double current, double max) {
        g.setColor(ColorCache.blackA(150));
        g.fillRoundRect(x - 2, y - 2, w + 4, h + 4, 6, 6);

        g.setColor(new Color(0, 20, 60));
        g.fillRoundRect(x, y, w, h, 4, 4);

        double pct = Math.max(0, Math.min(1, current / max));
        Color bar = new Color(80, 140, 255);
        GradientPaint gp = new GradientPaint(
                x, y, bar.brighter(), x, y + h, bar.darker());
        g.setPaint(gp);
        g.fillRoundRect(x, y, (int) (w * pct), h, 4, 4);

        g.setColor(ColorCache.whiteA(80));
        g.fillRoundRect(x + 2, y + 1, Math.max(0, (int) (w * pct) - 4), h / 3, 2, 2);

        g.setColor(ColorCache.whiteA(120));
        g.drawRoundRect(x, y, w, h, 4, 4);
    }

    // ============= XP BAR =============
    public static void drawXpBar(Graphics2D g, int x, int y, int w, int h,
                                 double current, double max) {
        g.setColor(ColorCache.blackA(150));
        g.fillRoundRect(x - 1, y - 1, w + 2, h + 2, 4, 4);

        g.setColor(new Color(60, 50, 0));
        g.fillRoundRect(x, y, w, h, 3, 3);

        double pct = Math.max(0, Math.min(1, current / max));
        GradientPaint gp = new GradientPaint(
                x, y, new Color(255, 220, 60),
                x, y + h, new Color(180, 140, 20));
        g.setPaint(gp);
        g.fillRoundRect(x, y, (int) (w * pct), h, 3, 3);
    }

    // ============= КНОПКА =============
    public static void drawButton(Graphics2D g, int x, int y, int w, int h,
                                  String text, boolean selected) {
        g.setColor(ColorCache.blackA(120));
        g.fillRoundRect(x + 2, y + 2, w, h, 12, 12);

        Color bg1 = selected ? new Color(100, 80, 140) : new Color(50, 45, 70);
        Color bg2 = selected ? new Color(60, 50, 90) : new Color(30, 25, 45);
        GradientPaint gp = new GradientPaint(x, y, bg1, x, y + h, bg2);
        g.setPaint(gp);
        g.fillRoundRect(x, y, w, h, 12, 12);

        g.setColor(selected ? new Color(255, 220, 100) : new Color(100, 100, 130));
        g.setStroke(selected ? STROKE_3 : STROKE_2);
        g.drawRoundRect(x, y, w, h, 12, 12);

        g.setFont(FontCache.arialBold(18));
        g.setColor(selected ? Color.WHITE : new Color(200, 200, 200));
        int tw = g.getFontMetrics().stringWidth(text);
        g.drawString(text, x + (w - tw) / 2, y + h / 2 + 6);
    }

    // ============= ГРАДИЕНТ =============
    public static void drawGradient(Graphics2D g, int w, int h, Color top, Color bottom) {
        g.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g.fillRect(0, 0, w, h);
    }

    // ============= ВИНЬЕТКА (КЕШИРОВАННАЯ) =============
    public static void drawVignette(Graphics2D g, int w, int h) {
        g.drawImage(FrameCache.getVignette(w, h), 0, 0, null);
    }

    // ============= СПРАЙТ =============
    public static void drawSprite(Graphics2D g, BufferedImage sprite,
                                  int cx, int cy, int targetW, int targetH,
                                  int facingX, int walkFrame, boolean walking) {
        if (sprite == null) return;

        int sw = sprite.getWidth();
        int sh = sprite.getHeight();
        double ratio = Math.min(targetW / (double) sw, targetH / (double) sh);
        int drawW = (int) (sw * ratio);
        int drawH = (int) (sh * ratio);

        g.drawImage(sprite,
                cx - drawW / 2, cy - drawH / 2,
                drawW, drawH,
                null);
    }

    // Совместимость
    public static void drawSprite(Graphics2D g, BufferedImage sprite,
                                  int cx, int cy, int targetW, int targetH,
                                  int facingX) {
        drawSprite(g, sprite, cx, cy, targetW, targetH, facingX, 0, false);
    }

    // ============= ПУЛЬСАЦИЯ =============
    public static double pulse(long time, double base, double amp) {
        return base + Math.sin(time / 300.0) * amp;
    }
}