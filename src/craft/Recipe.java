package craft;

import item.Item;
import java.util.Map;

public class Recipe {
    public Map<String, Integer> ingredients;  // название → количество
    public Item result;

    public Recipe(Map<String, Integer> ingredients, Item result) {
        this.ingredients = ingredients;
        this.result = result;
    }
}