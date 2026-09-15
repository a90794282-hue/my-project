package test;

import game.Config;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void testScreenSize() {
        assertEquals(1024, Config.SCREEN_W);
        assertEquals(768, Config.SCREEN_H);
    }

    @Test
    public void testFps() {
        assertEquals(60, Config.FPS);
    }

    @Test
    public void testXpFormulas() {
        assertEquals(100, Config.XP_BASE);
        assertEquals(1.35, Config.XP_GROWTH, 0.01);
        assertEquals(1.5, Config.XP_GROWTH_LATE, 0.01);
    }

    @Test
    public void testRarityChances() {
        double sum = Config.RARITY_COMMON + Config.RARITY_RARE
                + Config.RARITY_EPIC + Config.RARITY_LEGENDARY;
        assertEquals(1.0, sum, 0.01);
    }
}