package game;

import java.awt.Graphics2D;

public class GameUIManager {
    private final Game game;

    public GameUIManager(Game game) {
        this.game = game;
    }

    public void drawAll(Graphics2D g2) {
        int w = game.getWidth();
        int h = game.getHeight();

        game.hud.draw(g2, game.player, w);
        if (game.getMap() != null) {
            game.miniMap.draw(g2, game.player, game.getMap(), game.enemies, w);
        }
        game.skillBar.draw(g2, game.player, w, h);
        game.timeOfDayUI.draw(g2, game.timeOfDay, w);
        game.achievementToast.draw(g2, w);

        if (game.state.getScreen() == GameState.Screen.INVENTORY) {
            game.inventoryUI.draw(g2, game.player, w, h);
        }
        if (game.state.getScreen() == GameState.Screen.PAUSED) {
            game.pauseMenu.draw(g2, w, h);
        }

        game.dialogueUI.draw(g2, w, h, game.player);
        game.shopUI.draw(g2, w, h, game.player);
    }
}