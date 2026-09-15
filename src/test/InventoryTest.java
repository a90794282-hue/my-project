package test;

import item.Inventory;
import item.Potion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    public void testEmptyInventory() {
        Inventory inv = new Inventory();
        assertEquals(0, inv.size());
        assertFalse(inv.isFull());
    }

    @Test
    public void testAddItem() {
        Inventory inv = new Inventory();
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        assertTrue(inv.add(p));
        assertEquals(1, inv.size());
    }

    @Test
    public void testFullInventory() {
        Inventory inv = new Inventory();
        for (int i = 0; i < Inventory.SLOTS; i++) {
            assertTrue(inv.add(new Potion("P" + i, Potion.Kind.HEALTH, 10)));
        }
        assertEquals(Inventory.SLOTS, inv.size());
        assertTrue(inv.isFull());
        assertFalse(inv.add(new Potion("Extra", Potion.Kind.HEALTH, 10)));
    }

    @Test
    public void testRemoveItem() {
        Inventory inv = new Inventory();
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        inv.add(p);
        assertTrue(inv.remove(p));
        assertEquals(0, inv.size());
    }

    @Test
    public void testRemoveNonExistent() {
        Inventory inv = new Inventory();
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        assertFalse(inv.remove(p));
    }

    @Test
    public void testGetItems() {
        Inventory inv = new Inventory();
        Potion p1 = new Potion("HP1", Potion.Kind.HEALTH, 30);
        Potion p2 = new Potion("MP1", Potion.Kind.MANA, 20);
        inv.add(p1);
        inv.add(p2);
        assertEquals(2, inv.getItems().size());
    }
}