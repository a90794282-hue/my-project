package game;

import entity.Player;

public class Camera {
    public double x, y;
    private int screenW, screenH;
    private double smoothing = 0.12;

    public Camera(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
    }

    /**
     * Обновляет размеры экрана (при фуллскрине).
     */
    public void setScreenSize(int w, int h) {
        this.screenW = w;
        this.screenH = h;
    }

    public void follow(Player player) {
        double pcx = player.x + player.width / 2.0;
        double pcy = player.y + player.height / 2.0;

        double targetX = pcx - screenW / 2.0;
        double targetY = pcy - screenH / 2.0;

        x += (targetX - x) * smoothing;
        y += (targetY - y) * smoothing;
    }

    public int toScreenX(double worldX) { return (int) (worldX - x); }
    public int toScreenY(double worldY) { return (int) (worldY - y); }
    public int getScreenW() { return screenW; }
    public int getScreenH() { return screenH; }
}