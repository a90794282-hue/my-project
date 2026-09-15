package game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private List<T> available = new ArrayList<>();
    private List<T> active = new ArrayList<>();
    private Supplier<T> factory;
    private int maxSize;

    public ObjectPool(Supplier<T> factory, int initialSize, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        for (int i = 0; i < initialSize; i++) {
            available.add(factory.get());
        }
    }

    public T obtain() {
        T obj;
        if (!available.isEmpty()) {
            obj = available.remove(available.size() - 1);
        } else if (active.size() < maxSize) {
            obj = factory.get();
        } else {
            return null;  // пул полон
        }
        active.add(obj);
        return obj;
    }

    public void release(T obj) {
        if (active.remove(obj)) {
            available.add(obj);
        }
    }

    public void clear() {
        available.addAll(active);
        active.clear();
    }

    public int activeCount() { return active.size(); }
    public int availableCount() { return available.size(); }
}