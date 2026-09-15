package entity;

import java.awt.Color;

public class Appearance {
    public String name = "Герой";
    public Color skinColor = new Color(255, 210, 170);
    public Color hairColor = new Color(80, 40, 20);
    public Color shirtColor = new Color(180, 50, 50);
    public Color pantsColor = new Color(50, 40, 80);

    // Для UI создания персонажа
    public static final Color[] SKIN_PRESETS = {
            new Color(255, 220, 180),
            new Color(230, 190, 150),
            new Color(200, 150, 100),
            new Color(150, 100, 70),
            new Color(100, 70, 50),
    };

    public static final Color[] HAIR_PRESETS = {
            new Color(30, 20, 15),      // чёрный
            new Color(80, 40, 20),      // каштан
            new Color(180, 130, 60),    // блонд
            new Color(200, 60, 40),     // рыжий
            new Color(200, 200, 200),   // седой
            new Color(120, 80, 200),    // фиолетовый
    };

    public static final Color[] SHIRT_PRESETS = {
            new Color(180, 50, 50),     // красный
            new Color(50, 100, 180),    // синий
            new Color(50, 150, 80),     // зелёный
            new Color(180, 150, 50),    // жёлтый
            new Color(120, 60, 180),    // фиолетовый
            new Color(60, 60, 60),      // чёрный
    };

    public static final Color[] PANTS_PRESETS = {
            new Color(50, 40, 80),
            new Color(80, 60, 40),
            new Color(30, 30, 30),
            new Color(100, 80, 60),
    };
}