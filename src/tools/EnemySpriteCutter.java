package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class EnemySpriteCutter {

    private static final String[] ROWS = {"up", "left", "down", "right"};

    // ⬇️ Класс-структура вместо String[][]
    private static class EnemyConfig {
        String fileName;
        String enemyName;
        int frameW;
        int frameH;

        EnemyConfig(String fileName, String enemyName, int frameW, int frameH) {
            this.fileName = fileName;
            this.enemyName = enemyName;
            this.frameW = frameW;
            this.frameH = frameH;
        }
    }

    public static void main(String[] args) throws Exception {
        String rawDir = "src/resources/sprites/enemy_raw/";
        String outDir = "src/resources/sprites/enemy/";

        new File(outDir).mkdirs();

        // Чистим старые
        File out = new File(outDir);
        File[] old = out.listFiles((d, n) -> n.endsWith(".png"));
        if (old != null) {
            for (File f : old) f.delete();
        }

        // ⬇️ Конфигурация врагов
        EnemyConfig[] enemies = {
                new EnemyConfig("slime.png",      "slime",    64,  64),
                new EnemyConfig("goblin.png",     "goblin",   64,  64),
                new EnemyConfig("Zombie.png",     "skeleton", 64,  64),
                new EnemyConfig("wolfsheet1.png", "orc",      160, 96),
        };

        for (EnemyConfig e : enemies) {
            cut(rawDir + e.fileName, outDir, e.enemyName, e.frameW, e.frameH);
        }

        System.out.println("\n✅ Готово! Файлы в: " + outDir);
    }

    private static void cut(String srcPath, String outDir,
                            String name, int frameW, int frameH) throws Exception {
        File src = new File(srcPath);
        if (!src.exists()) {
            System.out.println("⚠️ Нет файла: " + srcPath);
            return;
        }

        BufferedImage sheet = ImageIO.read(src);
        int rows = sheet.getHeight() / frameH;
        int cols = sheet.getWidth() / frameW;

        System.out.println("\n📄 " + name + ": " + sheet.getWidth()
                + "x" + sheet.getHeight()
                + " → " + cols + " × " + rows);

        int saved = 0;
        for (int row = 0; row < rows && row < ROWS.length; row++) {
            for (int col = 0; col < cols; col++) {
                BufferedImage frame = sheet.getSubimage(
                        col * frameW, row * frameH, frameW, frameH);

                if (isEmpty(frame)) continue;

                String fileName = outDir + name + "_" + ROWS[row]
                        + "_" + (col + 1) + ".png";
                ImageIO.write(frame, "png", new File(fileName));
                saved++;
            }
        }
        System.out.println("   ✅ Нарезано: " + saved + " файлов");
    }

    private static boolean isEmpty(BufferedImage img) {
        for (int y = 0; y < img.getHeight(); y += 8) {
            for (int x = 0; x < img.getWidth(); x += 8) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xFF;
                if (alpha > 10) return false;
            }
        }
        return true;
    }
}