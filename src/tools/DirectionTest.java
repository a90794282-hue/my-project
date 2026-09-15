package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DirectionTest {
    public static void main(String[] args) throws Exception {
        BufferedImage sheet = ImageIO.read(
                new File("src/resources/sprites/player_raw/walk.png"));

        int frameW = 64, frameH = 64;
        int rows = sheet.getHeight() / frameH;

        // Сохраняем 1-й кадр каждой строки для просмотра
        for (int row = 0; row < rows; row++) {
            BufferedImage frame = sheet.getSubimage(0, row * frameH, frameW, frameH);
            File out = new File("row_" + row + ".png");
            ImageIO.write(frame, "png", out);
            System.out.println("Сохранён: row_" + row + ".png");
        }
    }
}