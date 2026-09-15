package item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public static final int SLOTS = 24;
    private List<Item> items = new ArrayList<>();

    public boolean add(Item item) {
        if (items.size() >= SLOTS) return false;
        items.add(item);
        return true;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() { return items; }
    public int size() { return items.size(); }
    public boolean isFull() { return items.size() >= SLOTS; }
}