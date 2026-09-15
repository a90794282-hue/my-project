package game;

import ui.Renderer;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Универсальный fade-переход между экранами.
 *
 * Использование:
 *   transition.start(() -> { state.setScreen(NEW_SCREEN); });
 *
 * В draw: transition.draw(g2, width, height);
 */
public class ScreenTransition {
    public enum Phase { IDLE, FADE_OUT, FADE_IN }

    private Phase phase = Phase.IDLE;
    private int timer = 0;
    private int halfDuration = 15;   // 15 кадров на затухание + 15 на появление

    private Runnable onSwitch = null;

    /** Запустить переход с действием посередине */
    public void start(Runnable switchAction) {
        if (phase != Phase.IDLE) return;   // уже идёт
        this.onSwitch = switchAction;
        this.phase = Phase.FADE_OUT;
        this.timer = 0;
    }

    /** Простой вызов без действия */
    public void start() {
        start(null);
    }

    public void update() {
        switch (phase) {
            case FADE_OUT -> {
                timer++;
                if (timer >= halfDuration) {
                    // Дошли до чёрного — выполняем действие
                    if (onSwitch != null) {
                        onSwitch.run();
                        onSwitch = null;
                    }
                    phase = Phase.FADE_IN;
                    timer = 0;
                }
            }
            case FADE_IN -> {
                timer++;
                if (timer >= halfDuration) {
                    phase = Phase.IDLE;
                    timer = 0;
                }
            }
            case IDLE -> { /* ничего */ }
        }
    }

    /** Рисует чёрный слой с нужной прозрачностью */
    public void draw(Graphics2D g2, int w, int h) {
        float alpha = 0f;

        switch (phase) {
            case IDLE -> { return; }
            case FADE_OUT -> {
                // 0 → 1
                alpha = (float) timer / halfDuration;
            }
            case FADE_IN -> {
                // 1 → 0
                alpha = 1f - (float) timer / halfDuration;
            }
        }

        if (alpha < 0f) alpha = 0f;
        if (alpha > 1f) alpha = 1f;

        g2.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
        g2.fillRect(0, 0, w, h);
    }

    /** Идёт ли сейчас переход */
    public boolean isActive() {
        return phase != Phase.IDLE;
    }

    /** Идёт ли фаза затухания (нужно блокировать ввод) */
    public boolean isBlocking() {
        return phase == Phase.FADE_OUT;
    }
}