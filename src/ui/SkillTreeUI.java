package ui;

import entity.Player;
import game.FontCache;
import skill.SkillTree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

public class SkillTreeUI {
    private int selected = 0;
    private List<SkillTree.Skill> skills;
    private SkillTree tree;

    public void open(SkillTree tree) {
        this.tree = tree;
        this.skills = tree.getAllSkills();
        this.selected = 0;
    }

    public void handleKey(int keyCode, Player player) {
        if (skills == null) return;
        int size = skills.size();
        switch (keyCode) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + size) % size;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % size;
            case KeyEvent.VK_ENTER -> {
                SkillTree.Skill s = skills.get(selected);
                if (tree.unlock(s, player)) {
                    game.SoundManager.play("levelup");
                } else {
                    game.SoundManager.play("menu");
                }
            }
        }
    }

    public void draw(Graphics2D g2, int w, int h, Player player) {
        g2.setColor(new Color(15, 15, 25, 240));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 220, 100));
        g2.setFont(FontCache.arialBold(48));
        String title = "ДЕРЕВО СКИЛЛОВ";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, 80);

        g2.setFont(FontCache.arialBold(20));
        g2.setColor(new Color(150, 220, 255));
        String pts = "Очки: " + tree.getSkillPoints();
        int pw = g2.getFontMetrics().stringWidth(pts);
        g2.drawString(pts, (w - pw) / 2, 115);

        int startY = 150;
        int rowH = 32;
        int visibleRows = (h - startY - 60) / rowH;
        int offset = Math.max(0, Math.min(skills.size() - visibleRows,
                selected - visibleRows / 2));

        for (int i = 0; i < visibleRows && i + offset < skills.size(); i++) {
            int idx = i + offset;
            SkillTree.Skill s = skills.get(idx);
            int y = startY + i * rowH;
            int x = 80;
            int panelW = w - 160;

            boolean sel = idx == selected;

            Color branchColor = switch (s.branch) {
                case WARRIOR -> new Color(200, 80, 80);
                case RANGER -> new Color(80, 200, 100);
                case MAGE -> new Color(120, 120, 255);
                case NEUTRAL -> new Color(200, 200, 100);
            };

            if (sel) {
                g2.setColor(new Color(80, 60, 120, 200));
                g2.fillRoundRect(x - 10, y - 22, panelW + 20, 28, 8, 8);
            }

            g2.setColor(branchColor);
            g2.fillRoundRect(x, y - 18, 20, 20, 4, 4);

            g2.setColor(s.unlocked ? Color.GREEN : new Color(220, 220, 220));
            g2.setFont(FontCache.arialBold(14));
            g2.drawString(s.name, x + 30, y);

            g2.setColor(new Color(180, 180, 180));
            g2.setFont(FontCache.arial(11));
            g2.drawString(s.description, x + 250, y);

            g2.setFont(FontCache.arialBold(11));
            if (s.unlocked) {
                g2.setColor(new Color(80, 220, 80));
                g2.drawString("✓", x + panelW - 20, y);
            } else {
                boolean canGet = player.getLevel() >= s.requiredLevel
                        && tree.getSkillPoints() >= s.cost;
                g2.setColor(canGet ? new Color(150, 255, 150) : new Color(200, 100, 100));
                g2.drawString("Ур." + s.requiredLevel + " | " + s.cost + " очк.",
                        x + panelW - 100, y);
            }
        }

        g2.setColor(new Color(150, 150, 150));
        g2.setFont(FontCache.arial(14));
        String hint = "↑↓ — выбор | Enter — изучить | Esc — закрыть";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 30);
    }
}