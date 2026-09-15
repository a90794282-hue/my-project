package ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class PlayerSprite {

    public enum Dir { DOWN, UP, LEFT, RIGHT, DOWN_LEFT, DOWN_RIGHT, UP_LEFT, UP_RIGHT }

    // ⬇️ КЭШ спрайтов
    private static final Map<String, BufferedImage> spriteCache = new HashMap<>();

    public static Dir dirFromVector(int fx, int fy) {
        if (fx > 0 && fy > 0) return Dir.DOWN_RIGHT;
        if (fx < 0 && fy > 0) return Dir.DOWN_LEFT;
        if (fx > 0 && fy < 0) return Dir.UP_RIGHT;
        if (fx < 0 && fy < 0) return Dir.UP_LEFT;
        if (fx > 0) return Dir.RIGHT;
        if (fx < 0) return Dir.LEFT;
        if (fy > 0) return Dir.DOWN;
        if (fy < 0) return Dir.UP;
        return Dir.DOWN;
    }

    public static String dirName(Dir d) {
        return switch (d) {
            case DOWN, DOWN_LEFT, DOWN_RIGHT -> "down";
            case UP, UP_LEFT, UP_RIGHT -> "up";
            case LEFT -> "left";
            case RIGHT -> "right";
        };
    }

    private static BufferedImage cached(String key, String path) {
        if (spriteCache.containsKey(key)) {
            return spriteCache.get(key);
        }
        BufferedImage img = SpriteLoader.load(path);
        spriteCache.put(key, img);
        return img;
    }

    public static BufferedImage getIdle(Dir d) {
        String name = dirName(d);
        return cached("idle_" + name,
                "/sprites/player/idle_" + name + ".png");
    }

    public static BufferedImage getWalk(Dir d, int frame) {
        frame = ((frame % 4) + 4) % 4;
        String name = dirName(d);
        return cached("walk_" + name + "_" + frame,
                "/sprites/player/walk_" + name + "_" + (frame + 1) + ".png");
    }

    public static BufferedImage getAttack(Dir d) {
        String name = dirName(d);
        return cached("attack_" + name,
                "/sprites/player/attack_" + name + ".png");
    }

    public static void draw(Graphics2D g, BufferedImage img, int cx, int cy,
                            int facingX, boolean flip) {
        if (img == null) return;

        int w = img.getWidth();
        int h = img.getHeight();

        if (facingX < 0 || flip) {
            g.drawImage(img, cx + w / 2, cy - h / 2, -w, h, null);
        } else {
            g.drawImage(img, cx - w / 2, cy - h / 2, w, h, null);
        }
    }

    public static void clearCache() {
        spriteCache.clear();
    }
}