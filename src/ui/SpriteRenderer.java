package ui;

import entity.Appearance;
import item.Weapon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Point;

public class SpriteRenderer {

    // ============ ИГРОК ============
    public static void drawPlayer(Graphics2D g, int cx, int cy,
                                  int facingX, int facingY, double walkBob,
                                  int animFrame, Appearance look, Weapon weapon) {
        int bob = (int) walkBob;
        boolean attacking = animFrame >= 10;

        Color skin = look != null ? look.skinColor : new Color(255, 210, 170);
        Color hair = look != null ? look.hairColor : new Color(80, 40, 20);
        Color shirt = look != null ? look.shirtColor : new Color(180, 50, 50);
        Color pants = look != null ? look.pantsColor : new Color(50, 40, 80);

        // Тень
        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(cx - 16, cy + 12, 32, 8);

        // Ноги
        int legSwing = (animFrame % 2 == 0) ? 2 : -2;
        g.setColor(pants);
        g.fillRect(cx - 8, cy + 6 + bob, 6, 10 + legSwing);
        g.fillRect(cx + 2, cy + 6 + bob, 6, 10 - legSwing);

        // Обувь
        g.setColor(new Color(40, 25, 15));
        g.fillRect(cx - 8, cy + 14 + bob + legSwing, 6, 3);
        g.fillRect(cx + 2, cy + 14 + bob - legSwing, 6, 3);

        // Тело
        g.setColor(shirt);
        g.fillRect(cx - 9, cy - 8 + bob, 18, 16);

        // Ремень
        g.setColor(new Color(60, 40, 20));
        g.fillRect(cx - 9, cy + 4 + bob, 18, 3);

        // Руки
        g.setColor(skin);
        int armSwing = (animFrame % 2 == 0) ? -2 : 2;
        g.fillRect(cx - 13, cy - 6 + bob + armSwing, 5, 12);
        g.fillRect(cx + 8, cy - 6 + bob - armSwing, 5, 12);

        // Голова
        g.setColor(skin);
        g.fillOval(cx - 9, cy - 22 + bob, 18, 16);
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(150, 100, 70));
        g.drawOval(cx - 9, cy - 22 + bob, 18, 16);

        // Волосы
        g.setColor(hair);
        g.fillArc(cx - 9, cy - 24 + bob, 18, 14, 0, 180);

        // Глаза
        int eyeOffX = facingX * 3;
        int eyeOffY = facingY * 2;
        g.setColor(Color.WHITE);
        g.fillOval(cx - 6 + eyeOffX, cy - 16 + bob + eyeOffY, 4, 4);
        g.fillOval(cx + 2 + eyeOffX, cy - 16 + bob + eyeOffY, 4, 4);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 5 + eyeOffX, cy - 15 + bob + eyeOffY, 2, 2);
        g.fillOval(cx + 3 + eyeOffX, cy - 15 + bob + eyeOffY, 2, 2);

        // Рот
        g.setColor(new Color(120, 50, 50));
        g.drawLine(cx - 2, cy - 10 + bob, cx + 2, cy - 10 + bob);

        // Оружие
        if (weapon != null) {
            int hx = cx + 12;
            int hy = cy - 4 + bob + armSwing;

            if (attacking) {
                hx += facingX * 4;
                hy -= 4;
            }

            switch (weapon.getType()) {
                case SWORD -> drawSword(g, hx, hy, attacking);
                case BOW   -> drawBow(g, hx, hy, attacking);
                case STAFF -> drawStaff(g, hx, hy, attacking);
            }
        }
    }

    private static void drawSword(Graphics2D g, int hx, int hy, boolean attacking) {
        int length = attacking ? 22 : 18;
        g.setColor(new Color(80, 50, 30));
        g.fillRect(hx, hy, 3, 8);
        g.setColor(new Color(200, 180, 60));
        g.fillRect(hx - 3, hy - 2, 9, 3);
        g.setColor(new Color(220, 220, 240));
        g.fillRect(hx, hy - length, 3, length - 2);
        g.setColor(new Color(180, 180, 200));
        g.drawLine(hx + 1, hy - length, hx + 1, hy - 2);
        if (attacking) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(hx, hy - length, 1, length - 2);
        }
    }

    private static void drawBow(Graphics2D g, int hx, int hy, boolean attacking) {
        g.setColor(new Color(120, 70, 30));
        g.setStroke(new BasicStroke(3f));
        g.drawArc(hx - 4, hy - 14, 12, 26, -60, 120);
        g.setColor(new Color(230, 230, 230));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(hx + 2, hy - 12, hx + 2, hy + 10);

        if (attacking) {
            g.setColor(new Color(200, 200, 200));
            g.fillRect(hx + 6, hy - 2, 12, 3);
            g.setColor(new Color(150, 150, 150));
            g.fillPolygon(new int[]{hx + 18, hx + 22, hx + 18},
                    new int[]{hy - 4, hy, hy + 4}, 3);
        }
    }

    private static void drawStaff(Graphics2D g, int hx, int hy, boolean attacking) {
        g.setColor(new Color(120, 70, 30));
        g.fillRect(hx, hy - 20, 3, 30);
        g.setColor(new Color(120, 100, 255));
        g.fillOval(hx - 3, hy - 28, 9, 9);
        g.setColor(new Color(180, 160, 255));
        g.fillOval(hx - 1, hy - 26, 5, 5);

        int glowSize = attacking ? 30 : 19;
        g.setColor(new Color(150, 130, 255, 80));
        g.fillOval(hx - glowSize / 2, hy - 33 - glowSize / 4,
                glowSize, glowSize);
    }

    // ============ ВРАГИ ============

    public static void drawSlime(Graphics2D g, int cx, int cy, int animFrame) {
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(cx - 16, cy + 10, 32, 8);

        int squish = (animFrame % 2 == 0) ? 2 : 0;
        int w = 32 + squish;
        int h = 24 - squish;

        RadialGradientPaint body = new RadialGradientPaint(
                new Point(cx - 4, cy - 4),
                18, new float[]{0f, 1f},
                new Color[]{new Color(150, 255, 150), new Color(40, 180, 60)});
        g.setPaint(body);
        g.fillOval(cx - w / 2, cy - h / 2, w, h);

        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(20, 120, 40));
        g.drawOval(cx - w / 2, cy - h / 2, w, h);

        g.setColor(new Color(255, 255, 255, 150));
        g.fillOval(cx - 8, cy - 6, 6, 4);

        g.setColor(Color.WHITE);
        g.fillOval(cx - 8, cy - 4, 5, 5);
        g.fillOval(cx + 3, cy - 4, 5, 5);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 7, cy - 3, 3, 3);
        g.fillOval(cx + 4, cy - 3, 3, 3);

        g.setColor(new Color(20, 80, 30));
        g.drawArc(cx - 5, cy + 2, 10, 6, 0, 180);
    }

    public static void drawGoblin(Graphics2D g, int cx, int cy,
                                  int facingX, int animFrame) {
        int bob = (animFrame % 2 == 0) ? 1 : -1;

        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(cx - 14, cy + 12, 28, 7);

        g.setColor(new Color(60, 40, 20));
        g.fillRect(cx - 7, cy + 6 + bob, 6, 10);
        g.fillRect(cx + 1, cy + 6 - bob, 6, 10);

        g.setColor(new Color(120, 170, 60));
        g.fillRect(cx - 9, cy - 6 + bob, 18, 14);

        g.setColor(new Color(80, 60, 30));
        g.fillRect(cx - 9, cy + 2 + bob, 18, 6);
        g.fillRect(cx - 9, cy + 5 + bob, 5, 4);
        g.fillRect(cx + 4, cy + 5 + bob, 5, 4);

        g.setColor(new Color(100, 150, 50));
        int armSwing = (animFrame % 2 == 0) ? -2 : 2;
        g.fillRect(cx - 12, cy - 4 + bob + armSwing, 4, 10);
        g.fillRect(cx + 8, cy - 4 + bob - armSwing, 4, 10);

        g.setColor(new Color(130, 180, 70));
        g.fillOval(cx - 9, cy - 20 + bob, 18, 15);

        g.setColor(new Color(110, 160, 60));
        Polygon earL = new Polygon(
                new int[]{cx - 9, cx - 14, cx - 9},
                new int[]{cy - 14 + bob, cy - 12 + bob, cy - 8 + bob}, 3);
        Polygon earR = new Polygon(
                new int[]{cx + 9, cx + 14, cx + 9},
                new int[]{cy - 14 + bob, cy - 12 + bob, cy - 8 + bob}, 3);
        g.fillPolygon(earL);
        g.fillPolygon(earR);

        int eyeOffX = facingX * 2;
        g.setColor(new Color(255, 230, 60));
        g.fillOval(cx - 6 + eyeOffX, cy - 14 + bob, 4, 4);
        g.fillOval(cx + 2 + eyeOffX, cy - 14 + bob, 4, 4);
        g.setColor(Color.RED);
        g.fillOval(cx - 5 + eyeOffX, cy - 13 + bob, 2, 2);
        g.fillOval(cx + 3 + eyeOffX, cy - 13 + bob, 2, 2);

        g.setColor(Color.WHITE);
        g.fillRect(cx - 3, cy - 7 + bob, 2, 2);
        g.fillRect(cx + 1, cy - 7 + bob, 2, 2);
    }

    public static void drawOrc(Graphics2D g, int cx, int cy,
                               int facingX, int animFrame) {
        int bob = (animFrame % 2 == 0) ? 2 : 0;

        g.setColor(new Color(0, 0, 0, 100));
        g.fillOval(cx - 18, cy + 14, 36, 9);

        g.setColor(new Color(60, 40, 20));
        g.fillRect(cx - 10, cy + 8 + bob, 8, 12);
        g.fillRect(cx + 2, cy + 8 + bob, 8, 12);

        g.setColor(new Color(80, 130, 60));
        g.fillRect(cx - 14, cy - 10 + bob, 28, 20);

        g.setColor(new Color(100, 90, 70));
        g.fillRect(cx - 14, cy - 6 + bob, 28, 8);
        g.setColor(new Color(60, 55, 40));
        g.drawLine(cx - 14, cy - 2 + bob, cx + 14, cy - 2 + bob);

        g.setColor(new Color(70, 110, 50));
        g.fillRect(cx - 18, cy - 10 + bob, 6, 12);
        g.fillRect(cx + 12, cy - 10 + bob, 6, 12);

        g.setColor(new Color(80, 130, 60));
        int armSwing = (animFrame % 2 == 0) ? -3 : 3;
        g.fillRect(cx - 22, cy - 6 + bob + armSwing, 5, 14);
        g.fillRect(cx + 17, cy - 6 + bob - armSwing, 5, 14);

        g.setColor(new Color(90, 140, 70));
        g.fillOval(cx - 12, cy - 26 + bob, 24, 20);

        g.setColor(new Color(70, 110, 50));
        g.fillRect(cx - 10, cy - 12 + bob, 20, 5);

        int eyeOffX = facingX * 2;
        g.setColor(Color.RED);
        g.fillOval(cx - 8 + eyeOffX, cy - 20 + bob, 5, 5);
        g.fillOval(cx + 3 + eyeOffX, cy - 20 + bob, 5, 5);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 7 + eyeOffX, cy - 19 + bob, 3, 3);
        g.fillOval(cx + 4 + eyeOffX, cy - 19 + bob, 3, 3);

        g.setColor(Color.WHITE);
        Polygon fang1 = new Polygon(
                new int[]{cx - 4, cx - 2, cx - 6},
                new int[]{cy - 10 + bob, cy - 4 + bob, cy - 4 + bob}, 3);
        Polygon fang2 = new Polygon(
                new int[]{cx + 4, cx + 2, cx + 6},
                new int[]{cy - 10 + bob, cy - 4 + bob, cy - 4 + bob}, 3);
        g.fillPolygon(fang1);
        g.fillPolygon(fang2);
    }

    public static void drawSkeleton(Graphics2D g, int cx, int cy,
                                    int facingX, int animFrame) {
        int bob = (animFrame % 2 == 0) ? 1 : -1;

        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(cx - 14, cy + 12, 28, 7);

        Color bone = new Color(230, 230, 220);
        Color boneDark = new Color(180, 180, 170);

        g.setColor(bone);
        g.fillRect(cx - 8, cy + 6 + bob, 4, 12);
        g.fillRect(cx + 4, cy + 6 - bob, 4, 12);

        g.fillRect(cx - 9, cy + 4 + bob, 18, 4);

        g.setColor(bone);
        g.fillRect(cx - 8, cy - 8 + bob, 16, 3);
        g.fillRect(cx - 9, cy - 4 + bob, 18, 3);
        g.fillRect(cx - 9, cy + 0 + bob, 18, 3);

        g.fillRect(cx - 1, cy - 8 + bob, 2, 12);

        g.setColor(bone);
        int armSwing = (animFrame % 2 == 0) ? -2 : 2;
        g.fillRect(cx - 12, cy - 6 + bob + armSwing, 3, 12);
        g.fillRect(cx + 9, cy - 6 + bob - armSwing, 3, 12);

        g.setColor(bone);
        g.fillOval(cx - 9, cy - 22 + bob, 18, 16);

        g.setColor(boneDark);
        g.setStroke(new BasicStroke(1f));
        g.drawOval(cx - 9, cy - 22 + bob, 18, 16);

        int eyeOffX = facingX * 2;
        g.setColor(new Color(0, 0, 0));
        g.fillOval(cx - 6 + eyeOffX, cy - 18 + bob, 5, 5);
        g.fillOval(cx + 1 + eyeOffX, cy - 18 + bob, 5, 5);

        g.setColor(new Color(255, 40, 40, 200));
        g.fillOval(cx - 5 + eyeOffX, cy - 17 + bob, 3, 3);
        g.fillOval(cx + 2 + eyeOffX, cy - 17 + bob, 3, 3);

        g.setColor(bone);
        for (int i = 0; i < 4; i++) {
            g.fillRect(cx - 6 + i * 3, cy - 10 + bob, 2, 3);
        }

        g.setColor(boneDark);
        g.drawLine(cx - 7, cy - 7 + bob, cx + 7, cy - 7 + bob);
    }
}