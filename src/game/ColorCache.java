package game;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorCache {
    private static final Map<String, Color> cache = new HashMap<>();

    public static Color rgb(int r, int g, int b) {
        String key = r + "," + g + "," + b;
        Color c = cache.get(key);
        if (c == null) {
            c = new Color(r, g, b);
            cache.put(key, c);
        }
        return c;
    }

    public static Color rgba(int r, int g, int b, int a) {
        String key = r + "," + g + "," + b + "," + a;
        Color c = cache.get(key);
        if (c == null) {
            c = new Color(r, g, b, a);
            cache.put(key, c);
        }
        return c;
    }

    /** Полупрозрачный чёрный разных уровней (часто используется) */
    private static final Color[] BLACK_ALPHA = new Color[256];
    static {
        for (int i = 0; i < 256; i++) {
            BLACK_ALPHA[i] = new Color(0, 0, 0, i);
        }
    }

    public static Color blackA(int alpha) {
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;
        return BLACK_ALPHA[alpha];
    }

    /** Полупрозрачный белый */
    private static final Color[] WHITE_ALPHA = new Color[256];
    static {
        for (int i = 0; i < 256; i++) {
            WHITE_ALPHA[i] = new Color(255, 255, 255, i);
        }
    }

    public static Color whiteA(int alpha) {
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;
        return WHITE_ALPHA[alpha];
    }
}