package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class NPC extends Entity {
    public String name;
    public List<String> dialogue = new ArrayList<>();
    public int currentLine = 0;

    public boolean hasQuest = false;
    public boolean questGiven = false;
    public String questTitle = "";
    public String questDescription = "";
    public int questTarget = 0;
    public int questRewardGold = 0;
    public int questRewardXp = 0;

    public boolean shop = false;
    public shop.Shop shopData = null;

    public String faction = "neutral";

    public NPC(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.maxHp = 100;
        this.hp = maxHp;
        this.width = 32;
        this.height = 32;

        dialogue.add("Привет, путник!");
        dialogue.add("Что тебя привело сюда?");
        dialogue.add("Удачи в приключениях!");
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
        int cx = (int) x + width / 2;
        int cy = (int) y + height / 2;

        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval((int) x, (int) y + height - 6, width, 8);

        g.setColor(new Color(200, 180, 100));
        g.fillRect((int) x, (int) y, width, height);

        g.setColor(new Color(150, 130, 70));
        g.setStroke(new java.awt.BasicStroke(2f));
        g.drawRect((int) x, (int) y, width, height);

        g.setColor(Color.WHITE);
        g.fillOval(cx - 6, cy - 6, 4, 4);
        g.fillOval(cx + 2, cy - 6, 4, 4);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 5, cy - 5, 2, 2);
        g.fillOval(cx + 3, cy - 5, 2, 2);

        // Имя
        g.setFont(game.FontCache.arialBold(11));
        int nw = g.getFontMetrics().stringWidth(name);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(cx - nw / 2 - 4, (int) y - 20, nw + 8, 16, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString(name, cx - nw / 2, (int) y - 8);

        // Индикаторы
        if (hasQuest && !questGiven) {
            long time = System.currentTimeMillis();
            int pulse = (int) (Math.sin(time / 200.0) * 3);
            g.setColor(new Color(255, 220, 60));
            g.setFont(game.FontCache.arialBold(22 + pulse));
            g.drawString("!", cx - 4, (int) y - 24);
        } else if (hasQuest && questGiven) {
            g.setColor(new Color(150, 150, 150));
            g.setFont(game.FontCache.arialBold(18));
            g.drawString("✓", cx - 6, (int) y - 24);
        }

        if (shop) {
            g.setColor(new Color(100, 200, 255));
            g.setFont(game.FontCache.arialBold(14));
            g.drawString("$", cx - 4, (int) y - 24);
        }
    }

    public boolean isPlayerNear(double px, double py) {
        double dx = px - (x + width / 2.0);
        double dy = py - (y + height / 2.0);
        return Math.hypot(dx, dy) < 60;
    }

    public String nextLine() {
        if (dialogue.isEmpty()) return "...";
        String line = dialogue.get(currentLine);
        currentLine = (currentLine + 1) % dialogue.size();
        return line;
    }

    public void setDialogue(String... lines) {
        dialogue.clear();
        for (String l : lines) dialogue.add(l);
        currentLine = 0;
    }

    public void setQuest(String title, String desc, int target, int gold, int xp) {
        this.hasQuest = true;
        this.questTitle = title;
        this.questDescription = desc;
        this.questTarget = target;
        this.questRewardGold = gold;
        this.questRewardXp = xp;
    }

    public void setShop(shop.Shop s) {
        this.shop = true;
        this.shopData = s;
    }
}