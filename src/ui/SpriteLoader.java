package ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteLoader {
    private static final Map<String, BufferedImage> cache = new HashMap<>();
    private static final Map<String, List<BufferedImage>> frameCache = new HashMap<>();
    private static final Map<String, BufferedImage> findAnyCache = new HashMap<>();

    public static BufferedImage load(String path) {
        if (cache.containsKey(path)) return cache.get(path);
        try {
            InputStream is = SpriteLoader.class.getResourceAsStream(path);
            if (is == null) { cache.put(path, null); return null; }
            BufferedImage img = ImageIO.read(is);
            cache.put(path, img);
            return img;
        } catch (Exception e) {
            cache.put(path, null);
            return null;
        }
    }

    /**
     * Загружает массив кадров: basePath_1.png, basePath_2.png, ...
     */
    public static List<BufferedImage> loadFrames(String basePath, int count) {
        String key = basePath + "|" + count;
        if (frameCache.containsKey(key)) return frameCache.get(key);

        List<BufferedImage> frames = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            BufferedImage img = load(basePath + "_" + i + ".png");
            if (img != null) frames.add(img);
        }

        frameCache.put(key, frames);
        return frames;
    }

    public static boolean has(String path) { return load(path) != null; }

    public static BufferedImage findAny(String folderPath) {
        if (findAnyCache.containsKey(folderPath)) return findAnyCache.get(folderPath);

        BufferedImage result = null;
        try {
            File dir = new File("src/resources" + folderPath);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".png"));
                if (files != null && files.length > 0) {
                    java.util.Arrays.sort(files);
                    for (File f : files) {
                        try {
                            result = ImageIO.read(f);
                            if (result != null) break;
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}

        findAnyCache.put(folderPath, result);
        return result;
    }

    public static void clear() {
        cache.clear();
        frameCache.clear();
        findAnyCache.clear();
    }
}