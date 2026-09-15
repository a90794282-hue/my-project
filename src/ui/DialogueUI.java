package ui;

import entity.NPC;
import game.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class DialogueUI {
    private NPC currentNPC = null;
    private String currentLine = "";
    private boolean visible = false;
    private int choiceIndex = 0;
    private boolean showChoices = false;

    private ShopUI shopUI = null;

    public void setShopUI(ShopUI ui) {
        this.shopUI = ui;
    }

    public void open(NPC npc) {
        this.currentNPC = npc;
        this.visible = true;
        this.currentLine = npc.dialogue.isEmpty() ? "..." : npc.dialogue.get(0);
        this.currentNPC.currentLine = 0;
        this.showChoices = false;
        this.choiceIndex = 0;
    }

    public void close() {
        visible = false;
        currentNPC = null;
        showChoices = false;
    }

    public boolean isVisible() { return visible; }

    public void nextLine() {
        if (currentNPC == null) return;
        currentNPC.currentLine++;
        if (currentNPC.currentLine >= currentNPC.dialogue.size()) {
            showChoices = true;
            choiceIndex = 0;
        } else {
            currentLine = currentNPC.dialogue.get(currentNPC.currentLine);
        }
    }

    public void handleKey(int code, entity.Player player, quest.QuestSystem quests) {
        if (!visible) return;

        if (!showChoices) {
            if (code == java.awt.event.KeyEvent.VK_ENTER
                    || code == java.awt.event.KeyEvent.VK_SPACE) {
                nextLine();
            } else if (code == java.awt.event.KeyEvent.VK_ESCAPE) {
                close();
            }
            return;
        }

        int choices = 1;
        if (currentNPC.hasQuest && !currentNPC.questGiven) choices++;
        if (currentNPC.shop && currentNPC.shopData != null) choices++;

        switch (code) {
            case java.awt.event.KeyEvent.VK_UP ->
                    choiceIndex = (choiceIndex - 1 + choices) % choices;
            case java.awt.event.KeyEvent.VK_DOWN ->
                    choiceIndex = (choiceIndex + 1) % choices;
            case java.awt.event.KeyEvent.VK_ESCAPE -> close();
            case java.awt.event.KeyEvent.VK_ENTER -> handleChoice(choiceIndex, player, quests);
        }
    }

    private void handleChoice(int index, entity.Player player, quest.QuestSystem quests) {
        int idx = 0;

        if (index == idx) {
            close();
            return;
        }
        idx++;

        if (currentNPC.hasQuest && !currentNPC.questGiven) {
            if (index == idx) {
                currentNPC.questGiven = true;
                quests.addQuest(currentNPC.questTitle,
                        currentNPC.questDescription,
                        currentNPC.questTarget,
                        currentNPC.questRewardGold,
                        currentNPC.questRewardXp);
                close();
                return;
            }
            idx++;
        }

        if (currentNPC.shop && currentNPC.shopData != null) {
            if (index == idx) {
                if (shopUI != null) {
                    shopUI.open(currentNPC.shopData);
                }
                close();
            }
        }
    }

    public void draw(Graphics2D g2, int w, int h, entity.Player player) {
        if (!visible || currentNPC == null) return;

        int panelW = 700;
        int panelH = 200;
        int px = (w - panelW) / 2;
        int py = h - panelH - 40;

        g2.setColor(new Color(15, 15, 25, 240));
        g2.fillRoundRect(px, py, panelW, panelH, 16, 16);
        g2.setColor(new Color(180, 150, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(px, py, panelW, panelH, 16, 16);

        g2.setFont(FontCache.arialBold(20));
        g2.setColor(new Color(255, 220, 100));
        g2.drawString(currentNPC.name, px + 25, py + 35);

        g2.setColor(new Color(100, 100, 130));
        g2.drawLine(px + 20, py + 45, px + panelW - 20, py + 45);

        if (!showChoices) {
            g2.setFont(FontCache.arial(18));
            g2.setColor(Color.WHITE);
            g2.drawString(currentLine, px + 25, py + 80);

            g2.setFont(FontCache.arial(13));
            g2.setColor(new Color(150, 150, 150));
            g2.drawString("Enter — продолжить | Esc — выйти", px + 25, py + panelH - 15);
        } else {
            g2.setFont(FontCache.arialBold(16));
            int y = py + 75;
            int idx = 0;

            drawChoice(g2, px + 40, y, "До свидания", idx == choiceIndex);
            y += 30;
            idx++;

            if (currentNPC.hasQuest && !currentNPC.questGiven) {
                drawChoice(g2, px + 40, y, "Взять квест: " + currentNPC.questTitle,
                        idx == choiceIndex);
                y += 30;
                idx++;
            }

            if (currentNPC.shop && currentNPC.shopData != null) {
                drawChoice(g2, px + 40, y, "Открыть магазин", idx == choiceIndex);
            }
        }
    }

    private void drawChoice(Graphics2D g2, int x, int y, String text, boolean selected) {
        if (selected) {
            g2.setColor(new Color(255, 220, 100));
            g2.drawString("> " + text, x, y);
        } else {
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("  " + text, x, y);
        }
    }
}