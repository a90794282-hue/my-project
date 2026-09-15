package item;

import java.awt.Color;

public class Coin extends Item {
    public int value;

    public Coin(int value) {
        this.name = "Монета";
        this.value = value;
        this.rarity = Rarity.COMMON;
    }

    @Override
    public Color getRarityColor() { return new Color(255, 215, 0); }

    @Override
    public String getDescription() { return "+" + value + " золота"; }
}