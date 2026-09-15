package ui;

import game.Config;
import game.SoundManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class SettingsMenu {
    public enum Action { NONE, BACK }

    private int selected = 0;
    private final String[] items = {
            "Показывать FPS",
            "Отладочная информация",
            "Звук вкл",
            "Музыка",
            "Звуки (SFX)",
            "Числа урона",
            "Освещение",
            "Назад"
    };

    public Action handleKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> selected = (selected - 1 + items.length) % items.length;
            case KeyEvent.VK_DOWN -> selected = (selected + 1) % items.length;

            case KeyEvent.VK_LEFT -> changeValue(-1);
            case KeyEvent.VK_RIGHT -> changeValue(1);

            case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                if (selected < items.length - 1) changeValue(1);
                else return Action.BACK;
            }

            case KeyEvent.VK_ESCAPE -> { return Action.BACK; }
        }
        return Action.NONE;
    }

    private void changeValue(int dir) {
        switch (selected) {
            case 0 -> Config.showFps = !Config.showFps;

            case 1 -> Config.showDebug = !Config.showDebug;

            case 2 -> {
                Config.soundEnabled = !Config.soundEnabled;
                if (Config.soundEnabled) {
                    SoundManager.playMusic("music_menu");
                } else {
                    SoundManager.stopMusic();
                }
            }

            case 3 -> {
                Config.musicVolume = clamp(Config.musicVolume + dir * 5, 0, 100);
                SoundManager.setMusicVolume(Config.musicVolume);
                // Тестовый бип, чтобы услышать эффект
                SoundManager.playBeep(700, 60, Math.max(20, Config.sfxVolume / 2));
            }

            case 4 -> {
                Config.sfxVolume = clamp(Config.sfxVolume + dir * 5, 0, 100);
                // Тестовый бип на новой громкости
                SoundManager.playBeep(1000, 80, Config.sfxVolume);
            }

            case 5 -> Config.showDamageNumbers = !Config.showDamageNumbers;
            case 6 -> Config.showLighting = !Config.showLighting;
        }
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public void draw(Graphics2D g2, int w, int h) {
        // Фон
        g2.setColor(new Color(20, 20, 30));
        g2.fillRect(0, 0, w, h);

        // Заголовок
        g2.setColor(new Color(255, 220, 100));
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "НАСТРОЙКИ";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - tw) / 2, 120);

        // Пункты
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        for (int i = 0; i < items.length; i++) {
            int y = 220 + i * 50;
            boolean sel = i == selected;

            int panelW = 700;
            int panelH = 40;
            int x = (w - panelW) / 2;

            if (sel) {
                g2.setColor(new Color(100, 80, 140, 200));
                g2.fillRoundRect(x, y - 28, panelW, panelH, 10, 10);
                g2.setColor(new Color(255, 220, 100));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x, y - 28, panelW, panelH, 10, 10);
            }

            g2.setColor(sel ? Color.WHITE : new Color(200, 200, 200));
            g2.drawString(items[i], x + 20, y);

            // Значение справа
            g2.setColor(sel ? new Color(255, 220, 100) : new Color(150, 200, 255));
            String value = getValueText(i);
            int vw = g2.getFontMetrics().stringWidth(value);
            g2.drawString(value, x + panelW - vw - 20, y);
        }

        // Подсказка
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String hint = "↑↓ — выбор | ◄ ► — изменить | Enter — вкл/выкл | Esc — назад";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 60);
    }

    private String getValueText(int i) {
        return switch (i) {
            case 0 -> Config.showFps ? "ВКЛ" : "ВЫКЛ";
            case 1 -> Config.showDebug ? "ВКЛ" : "ВЫКЛ";
            case 2 -> Config.soundEnabled ? "ВКЛ" : "ВЫКЛ";
            case 3 -> makeVolumeBar(Config.musicVolume);
            case 4 -> makeVolumeBar(Config.sfxVolume);
            case 5 -> Config.showDamageNumbers ? "ВКЛ" : "ВЫКЛ";
            case 6 -> Config.showLighting ? "ВКЛ" : "ВЫКЛ";
            case 7 -> "→";
            default -> "";
        };
    }

    private String makeVolumeBar(int volume) {
        int filled = volume / 10;   // 0..10
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < 10; i++) {
            sb.append(i < filled ? "█" : "░");
        }
        sb.append("] ").append(volume).append("%");
        return sb.toString();
    }

    public void reset() { selected = 0; }
}