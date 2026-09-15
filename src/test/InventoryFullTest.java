package test;

import item.Inventory;
import item.Potion;
import item.Weapon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryFullTest {

    @Test public void emptyOnCreate() {
        assertEquals(0, new Inventory().size());
    }

    @Test public void notFullOnCreate() {
        assertFalse(new Inventory().isFull());
    }

    @Test public void addItem() {
        Inventory inv = new Inventory();
        assertTrue(inv.add(new Potion("HP", Potion.Kind.HEALTH, 30)));
        assertEquals(1, inv.size());
    }

    @Test public void addMultiple() {
        Inventory inv = new Inventory();
        for (int i = 0; i < 5; i++) {
            inv.add(new Potion("P" + i, Potion.Kind.HEALTH, 10));
        }
        assertEquals(5, inv.size());
    }

    @Test public void fillToMax() {
        Inventory inv = new Inventory();
        for (int i = 0; i < Inventory.SLOTS; i++) {
            assertTrue(inv.add(new Potion("P" + i, Potion.Kind.HEALTH, 10)));
        }
        assertTrue(inv.isFull());
    }

    @Test public void cannotAddToFull() {
        Inventory inv = new Inventory();
        for (int i = 0; i < Inventory.SLOTS; i++) {
            inv.add(new Potion("P" + i, Potion.Kind.HEALTH, 10));
        }
        assertFalse(inv.add(new Potion("Extra", Potion.Kind.HEALTH, 10)));
    }

    @Test public void removeExisting() {
        Inventory inv = new Inventory();
        Potion p = new Potion("HP", Potion.Kind.HEALTH, 30);
        inv.add(p);
        assertTrue(inv.remove(p));
        assertEquals(0, inv.size());
    }

    @Test public void removeNonExistent() {
        Inventory inv = new Inventory();
        assertFalse(inv.remove(new Potion("X", Potion.Kind.HEALTH, 10)));
    }

    @Test public void getItems() {
        Inventory inv = new Inventory();
        inv.add(new Potion("HP", Potion.Kind.HEALTH, 30));
        assertEquals(1, inv.getItems().size());
    }

    @Test public void mixedItems() {
        Inventory inv = new Inventory();
        inv.add(new Potion("HP", Potion.Kind.HEALTH, 30));
        inv.add(new Weapon("Меч", Weapon.Type.SWORD, 10));
        assertEquals(2, inv.size());
    }

    @Test public void removeMiddle() {
        Inventory inv = new Inventory();
        Potion a = new Potion("A", Potion.Kind.HEALTH, 10);
        Potion b = new Potion("B", Potion.Kind.HEALTH, 20);
        Potion c = new Potion("C", Potion.Kind.HEALTH, 30);
        inv.add(a); inv.add(b); inv.add(c);
        inv.remove(b);
        assertEquals(2, inv.size());
        assertTrue(inv.getItems().contains(a));
        assertFalse(inv.getItems().contains(b));
    }

    @Test public void slotsConstant() {
        assertEquals(24, Inventory.SLOTS);
    }
}