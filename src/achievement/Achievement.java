package achievement;

import java.util.ArrayList;
import java.util.List;

public class Achievement {
    public String name;
    public String description;
    public boolean unlocked = false;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static class Manager {
        public List<Achievement> all = new ArrayList<>();

        public void unlock(String name) {
            for (Achievement a : all) {
                if (a.name.equals(name) && !a.unlocked) {
                    a.unlocked = true;
                    System.out.println("🏆 Достижение: " + a.name);
                }
            }
        }
    }
}