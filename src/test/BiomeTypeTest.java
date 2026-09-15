package test;

import org.junit.jupiter.api.Test;
import world.BiomeType;
import static org.junit.jupiter.api.Assertions.*;

public class BiomeTypeTest {

    @Test
    public void testBiomeCount() {
        assertEquals(5, BiomeType.values().length);
    }

    @Test
    public void testFromPosition() {
        BiomeType b = BiomeType.fromPosition(0, 0, 12345L);
        assertNotNull(b);
    }

    @Test
    public void testDifferentPositions() {
        // Разные позиции могут дать разные биомы
        BiomeType b1 = BiomeType.fromPosition(10, 10, 100L);
        BiomeType b2 = BiomeType.fromPosition(1000, 1000, 100L);
        assertNotNull(b1);
        assertNotNull(b2);
    }

    @Test
    public void testBiomeLabels() {
        for (BiomeType b : BiomeType.values()) {
            assertNotNull(b.label);
            assertFalse(b.label.isEmpty());
        }
    }
}