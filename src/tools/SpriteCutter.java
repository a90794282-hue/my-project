package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Нарезает LPC-атласы (4×13) на отдельные кадры.
 * ПРАВИЛЬНЫЙ порядок строк:
 *   0 = UP    (спина)
 *   1 = RIGHT (профиль вправо)
 *   2 = DOWN  (лицо)
 *   3 = LEFT  (профиль влево)
 */
public class SpriteCutter {

    // ⬇️ ИСПРАВЛЕННЫЙ ПОРЯДОК: right ↔ left
    private static final String[] ROWS = {"up", "left", "down", "right"};

    // У тебя 13 кадров в ряду!
    private static final int FRAME_W = 64;
    private static final int FRAME_H = 64;

    public static void main(String[] args) throws Exception {
        String rawDir = "src/resources/sprites/player_raw/";
        String outDir = "src/resources/sprites/player/";

        File out = new File(outDir);

        // ===== УДАЛЯЕМ старые файлы =====
        if (out.exists()) {
            File[] old = out.listFiles((d, n) ->
                    n.startsWith("walk_") || n.startsWith("idle_") || n.startsWith("slash_"));
            if (old != null) {
                for (File f : old) {
                    if (f.delete()) System.out.println("🗑 Удалён: " + f.getName());
                }
            }
        }
        out.mkdirs();

        // ===== НАРЕЗАЕМ =====
        process(rawDir + "walk.png", outDir, "walk");
        process(rawDir + "idle.png", outDir, "idle");
        process(rawDir + "slash.png", outDir, "slash");

        System.out.println("✅ Готово! Файлы в: " + outDir);
    }

    private static void process(String srcPath, String outDir, String name) throws Exception {
        File src = new File(srcPath);
        if (!src.exists()) {
            System.out.println("❌ Нет файла: " + srcPath);
            return;
        }

        BufferedImage sheet = ImageIO.read(src);
        System.out.println("📄 " + name + ": " + sheet.getWidth() + "x" + sheet.getHeight());

        int rows = sheet.getHeight() / FRAME_H;
        int cols = sheet.getWidth() / FRAME_W;

        System.out.println("   Кадров: " + cols + " × " + rows);

        for (int row = 0; row < rows && row < ROWS.length; row++) {
            for (int col = 0; col < cols; col++) {
                BufferedImage frame = sheet.getSubimage(
                        col * FRAME_W, row * FRAME_H, FRAME_W, FRAME_H);

                if (isEmpty(frame)) continue;

                String fileName = outDir + name + "_" + ROWS[row]
                        + "_" + (col + 1) + ".png";
                ImageIO.write(frame, "png", new File(fileName));
            }
        }
    }

    private static boolean isEmpty(BufferedImage img) {
        for (int y = 0; y < img.getHeight(); y += 4) {
            for (int x = 0; x < img.getWidth(); x += 4) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xFF;
                if (alpha > 10) return false;
            }
        }
        return true;
    }
}