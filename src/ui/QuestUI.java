package ui;

import game.FontCache;
import quest.QuestSystem;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

public class QuestUI {
    private int selected = 0;

    public void handleKey(int code, QuestSystem quests, entity.Player player) {
        List<QuestSystem.Quest> list = quests.getAll();
        int size = list.size();
        switch (code) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + size) % size;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % size;
            case KeyEvent.VK_ENTER -> quests.claimCompleted(player);
        }
    }

    public void draw(Graphics2D g2, int w, int h, QuestSystem quests) {
        g2.setColor(new Color(15, 15, 25, 240));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 220, 100));
        g2.setFont(FontCache.arialBold(48));
        String title = "ЖУРНАЛ КВЕСТОВ";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, 80);

        List<QuestSystem.Quest> list = quests.getAll();
        int startY = 160;
        for (int i = 0; i < list.size(); i++) {
            QuestSystem.Quest q = list.get(i);
            int y = startY + i * 70;
            boolean sel = i == selected;

            if (sel) {
                g2.setColor(new Color(80, 60, 120, 200));
                g2.fillRoundRect(80, y - 30, w - 160, 60, 12, 12);
            }

            g2.setFont(FontCache.arialBold(18));
            if (q.completed && q.claimed) {
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("✓ " + q.title, 100, y);
            } else if (q.completed) {
                g2.setColor(new Color(80, 220, 80));
                g2.drawString("★ " + q.title, 100, y);
            } else {
                g2.setColor(sel ? Color.WHITE : new Color(200, 200, 200));
                g2.drawString(q.title, 100, y);
            }

            g2.setFont(FontCache.arial(13));
            g2.setColor(new Color(180, 180, 180));
            g2.drawString(q.description + " — " + q.progressText(), 120, y + 22);

            if (q.completed && !q.claimed) {
                g2.setColor(new Color(255, 220, 60));
                g2.setFont(FontCache.arialBold(12));
                g2.drawString("[Enter — забрать]", w - 200, y);
            }
        }

        g2.setColor(new Color(150, 150, 150));
        g2.setFont(FontCache.arial(14));
        String hint = "↑↓ — выбор | Enter — забрать | Esc — закрыть";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 40);
    }
}