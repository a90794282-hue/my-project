package game;

import magic.Fireball;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Загрузка всех звуков заранее
        SoundManager.load("hit");
        SoundManager.load("enemy_death");
        SoundManager.load("player_death");
        SoundManager.load("pickup");
        SoundManager.load("levelup");
        SoundManager.load("spell");
        SoundManager.load("step");
        SoundManager.load("menu");

        // Стартовая музыка
        SoundManager.playMusic("music_menu");

        JFrame frame = new JFrame("RPG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Config.SCREEN_W, Config.SCREEN_H);
        frame.setLocationRelativeTo(null);

        Game game = new Game();
        FullscreenManager fm = new FullscreenManager(frame);
        game.setFullscreenManager(fm);

        game.player.addSpell(new Fireball());

        frame.add(game);
        frame.setVisible(true);

        game.requestFocusInWindow();
        game.start();
    }
}