package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Кеш для часто перерисовываемых вещей
 */
public class FrameCache {
    private static BufferedImage cachedVignette = null;
    private static int lastVW = -1, lastVH = -1;

    public static BufferedImage getVignette(int w, int h) {
        if (cachedVignette == null || w != lastVW || h != lastVH) {
            cachedVignette = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cachedVignette.createGraphics();

            java.awt.RadialGradientPaint rgp = new java.awt.RadialGradientPaint(
                    new java.awt.Point(w / 2, h / 2),
                    Math.max(w, h) * 0.75f,
                    new float[]{0.5f, 1f},
                    new java.awt.Color[]{
                            new java.awt.Color(0, 0, 0, 0),
                            new java.awt.Color(0, 0, 0, 180)
                    });
            g.setPaint(rgp);
            g.fillRect(0, 0, w, h);
            g.dispose();

            lastVW = w;
            lastVH = h;
        }
        return cachedVignette;
    }
}