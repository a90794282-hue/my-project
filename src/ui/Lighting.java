package ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Lighting {
    // Рисует темноту, вырезая круг вокруг игрока
    public static void draw(Graphics2D g, int screenW, int screenH,
                            int playerScreenX, int playerScreenY, int radius) {
        // Создаём маску: чёрный экран с «дыркой»
        java.awt.image.BufferedImage mask =
                new java.awt.image.BufferedImage(screenW, screenH,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D mg = mask.createGraphics();
        Renderer.enableQuality(mg);

        // Заливаем полупрозрачным чёрным
        mg.setColor(new Color(0, 0, 0, 200));
        mg.fillRect(0, 0, screenW, screenH);

        // Вырезаем круг (прозрачность)
        mg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(playerScreenX, playerScreenY),
                radius,
                new float[]{0f, 0.6f, 1f},
                new Color[]{
                        new Color(0, 0, 0, 255),
                        new Color(0, 0, 0, 180),
                        new Color(0, 0, 0, 0)
                }
        );
        mg.setPaint(rgp);
        mg.fillOval(playerScreenX - radius, playerScreenY - radius,
                radius * 2, radius * 2);
        mg.dispose();

        // Рисуем маску поверх всего
        g.drawImage(mask, 0, 0, null);
    }
}