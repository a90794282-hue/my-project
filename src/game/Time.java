package game;

public class Time {
    private static long startMs = System.currentTimeMillis();
    private static long lastFrameMs = startMs;
    private static double deltaSeconds = 0;
    private static int frames = 0;
    private static int fps = 0;
    private static long fpsTimer = startMs;

    public static void tick() {
        long now = System.currentTimeMillis();
        deltaSeconds = (now - lastFrameMs) / 1000.0;
        lastFrameMs = now;
        frames++;

        if (now - fpsTimer >= 1000) {
            fps = frames;
            frames = 0;
            fpsTimer = now;
        }
    }

    public static double delta() { return deltaSeconds; }
    public static int fps() { return fps; }
    public static long elapsedMs() { return System.currentTimeMillis() - startMs; }

    /** Мягкая синусоида для анимаций */
    public static double sin(double periodMs) {
        return Math.sin(elapsedMs() / periodMs);
    }
}