package game;

import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class FullscreenManager {
    private final JFrame frame;
    private final GraphicsDevice device;
    private int windowedX, windowedY, windowedW, windowedH;
    private boolean isFullscreen = false;

    public FullscreenManager(JFrame frame) {
        this.frame = frame;
        this.device = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
    }

    public void toggle() {
        if (isFullscreen) exitFullscreen();
        else enterFullscreen();
    }

    public void enterFullscreen() {
        if (isFullscreen) return;

        windowedX = frame.getX();
        windowedY = frame.getY();
        windowedW = frame.getWidth();
        windowedH = frame.getHeight();

        frame.dispose();
        frame.setUndecorated(true);
        frame.setResizable(false);

        device.setFullScreenWindow(frame);
        frame.setVisible(true);
        frame.requestFocusInWindow();

        Config.SCREEN_W = frame.getWidth();
        Config.SCREEN_H = frame.getHeight();

        isFullscreen = true;
        Config.fullscreen = true;
    }

    public void exitFullscreen() {
        if (!isFullscreen) return;

        device.setFullScreenWindow(null);
        frame.dispose();
        frame.setUndecorated(false);
        frame.setResizable(true);
        frame.setBounds(windowedX, windowedY, windowedW, windowedH);
        frame.setVisible(true);
        frame.requestFocusInWindow();

        Config.SCREEN_W = windowedW;
        Config.SCREEN_H = windowedH;

        isFullscreen = false;
        Config.fullscreen = false;
    }

    public boolean isFullscreen() { return isFullscreen; }
}