package quest;

import java.util.HashMap;
import java.util.Map;

public class Faction {
    public static class Reputation {
        public Map<String, Integer> rep = new HashMap<>();

        public void add(String faction, int amount) {
            rep.merge(faction, amount, Integer::sum);
        }

        public String getRank(String faction) {
            int r = rep.getOrDefault(faction, 0);
            if (r < -50) return "Враг";
            if (r < 0)   return "Неприязнь";
            if (r < 50)  return "Нейтралитет";
            if (r < 200) return "Дружелюбие";
            if (r < 500) return "Уважение";
            return "Легенда";
        }
    }
}