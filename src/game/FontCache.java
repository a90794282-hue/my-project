package game;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class FontCache {
    private static final Map<String, Font> cache = new HashMap<>();

    public static Font get(String name, int style, int size) {
        String key = name + "_" + style + "_" + size;
        Font f = cache.get(key);
        if (f == null) {
            f = new Font(name, style, size);
            cache.put(key, f);
        }
        return f;
    }

    public static Font arialBold(int size) {
        return get("Arial", Font.BOLD, size);
    }

    public static Font arial(int size) {
        return get("Arial", Font.PLAIN, size);
    }

    public static Font consolas(int size) {
        return get("Consolas", Font.PLAIN, size);
    }

    public static Font arialItalic(int size) {
        return get("Arial", Font.ITALIC, size);
    }
}