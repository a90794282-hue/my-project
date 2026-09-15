package game;

import entity.NPC;
import ui.Renderer;
import world.Portal;

import java.awt.Color;
import java.awt.Graphics2D;

public class GameOverlayDrawer {
    private Game game;

    public GameOverlayDrawer(Game game) {
        this.game = game;
    }

    public void drawAll(Graphics2D g2) {
        drawPortalHint(g2);
        drawDungeonHint(g2);
        drawToast(g2);
        drawGameOver(g2);
        drawDebug(g2);
        drawNPCPrompt(g2);
    }

    private void drawPortalHint(Graphics2D g2) {
        if (game.portalManager.inDungeon || !game.state.isPlaying()) return;
        Portal near = game.portalManager.findPortalNear(game.player);
        if (near == null) return;

        int w = game.getWidth(), h = game.getHeight();
        g2.setColor(ColorCache.blackA(200));
        g2.fillRoundRect(w / 2 - 220, h - 130, 440, 80, 16, 16);
        g2.setColor(near.difficulty.color);
        g2.setStroke(Renderer.stroke2());
        g2.drawRoundRect(w / 2 - 220, h - 130, 440, 80, 16, 16);

        g2.setFont(FontCache.arialBold(18));
        g2.setColor(near.difficulty.color.brighter());
        String title = "Портал: " + near.difficulty.getLabel();
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, w / 2 - tw / 2, h - 100);

        g2.setFont(FontCache.arial(13));
        boolean canEnter = near.canEnter(game.player.getLevel());
        String info = canEnter ? "Нажмите E для входа"
                : "Нужен уровень " + near.difficulty.minLevel;
        g2.setColor(canEnter ? Color.WHITE : ColorCache.rgb(255, 100, 100));
        int iw = g2.getFontMetrics().stringWidth(info);
        g2.drawString(info, w / 2 - iw / 2, h - 75);

        g2.setColor(ColorCache.rgb(200, 200, 200));
        String stats = "HP x" + near.difficulty.hpMult
                + "  DMG x" + near.difficulty.dmgMult
                + "  XP x" + near.difficulty.xpMult;
        int sw = g2.getFontMetrics().stringWidth(stats);
        g2.drawString(stats, w / 2 - sw / 2, h - 58);
    }

    private void drawDungeonHint(Graphics2D g2) {
        if (!game.portalManager.inDungeon || !game.state.isPlaying()) return;
        int w = game.getWidth();
        g2.setColor(ColorCache.blackA(180));
        g2.fillRoundRect(w - 250, 100, 230, 50, 12, 12);
        g2.setColor(game.portalManager.currentDifficulty.color);
        g2.setFont(FontCache.arialBold(14));
        g2.drawString("Подземелье: " + game.portalManager.currentDifficulty.getLabel(),
                w - 240, 122);
        g2.setColor(Color.WHITE);
        g2.setFont(FontCache.arial(12));
        g2.drawString("Q — выйти | K — скиллы", w - 240, 142);
    }

    private void drawToast(Graphics2D g2) {
        long now = System.currentTimeMillis();
        if (now > game.toastUntil) return;
        float alpha = Math.min(1f, (game.toastUntil - now) / 500f);
        int w = game.getWidth();
        g2.setColor(ColorCache.blackA((int) (200 * alpha)));
        g2.fillRoundRect(w / 2 - 250, 40, 500, 40, 12, 12);
        g2.setColor(ColorCache.whiteA((int) (255 * alpha)));
        g2.setFont(FontCache.arialBold(15));
        int tw = g2.getFontMetrics().stringWidth(game.toastMessage);
        g2.drawString(game.toastMessage, w / 2 - tw / 2, 65);
    }

    private void drawGameOver(Graphics2D g2) {
        if (game.state.getScreen() != GameState.Screen.GAME_OVER) return;
        int w = game.getWidth(), h = game.getHeight();
        g2.setColor(ColorCache.blackA(180));
        g2.fillRect(0, 0, w, h);
        g2.setColor(ColorCache.rgb(200, 0, 0));
        g2.setFont(FontCache.arialBold(64));
        String title = "ВЫ ПОГИБЛИ";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, h / 2 - 40);
        g2.setColor(Color.WHITE);
        g2.setFont(FontCache.arial(20));
        String sub = "R — возродиться | Esc — главное меню";
        int sw = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (w - sw) / 2, h / 2 + 20);
    }

    private void drawDebug(Graphics2D g2) {
        if (!Config.showDebug) return;
        g2.setColor(Color.GREEN);
        g2.setFont(FontCache.consolas(11));
        int h = game.getHeight();

        if (Config.showFps) {
            g2.drawString("FPS: " + Time.fps(), 5, h - 88);
        }
        g2.drawString("Enemies: " + game.enemies.size()
                        + " | Kills: " + game.getEnemiesKilled()
                        + " | State: " + game.state.getScreen(),
                5, h - 76);
        g2.drawString("Gold: " + game.player.getGold()
                        + " | Items: " + game.player.getInventory().size() + "/24",
                5, h - 64);
        g2.drawString("F5 save | F9 load", 5, h - 52);
        g2.drawString("T NPC | K скиллы | C крафт | B ачивки | J квесты",
                5, h - 40);
        g2.drawString("I инв | E портал | Esc пауза | F11 фуллскрин",
                5, h - 28);
        g2.setColor(Color.YELLOW);
        g2.drawString("F12 — диагностика", 5, h - 16);
    }

    private void drawNPCPrompt(Graphics2D g2) {
        if (game.dialogueUI.isVisible() || !game.state.isPlaying()
                || game.player.isDead()) return;

        NPC near = game.npcManager.findNPCNear(game.player);
        if (near == null) return;

        int w = game.getWidth(), h = game.getHeight();
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(w / 2 - 100, h - 160, 200, 40, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(FontCache.arialBold(16));
        String hint = "T — поговорить";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, w / 2 - hw / 2, h - 135);
    }
}