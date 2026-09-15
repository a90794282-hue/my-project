package test;

import craft.CraftSystem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CraftSystemTest {

    @Test
    public void testRecipesExist() {
        CraftSystem cs = new CraftSystem();
        assertFalse(cs.getRecipes().isEmpty());
        assertEquals(5, cs.getRecipes().size());
    }

    @Test
    public void testRecipeHasName() {
        CraftSystem cs = new CraftSystem();
        CraftSystem.Recipe r = cs.getRecipes().get(0);
        assertNotNull(r.resultName);
        assertNotNull(r.result);
    }
}