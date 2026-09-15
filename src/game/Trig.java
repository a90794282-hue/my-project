package game;

public class Trig {
    private static final int SIZE = 3600;        // 10 шагов на градус
    private static final double[] SIN = new double[SIZE];
    private static final double[] COS = new double[SIZE];

    static {
        for (int i = 0; i < SIZE; i++) {
            double a = i * Math.PI * 2 / SIZE;
            SIN[i] = Math.sin(a);
            COS[i] = Math.cos(a);
        }
    }

    private static int idx(double angle) {
        int i = (int) ((angle % (Math.PI * 2)) * SIZE / (Math.PI * 2));
        if (i < 0) i += SIZE;
        return i % SIZE;
    }

    public static double sin(double a) { return SIN[idx(a)]; }
    public static double cos(double a) { return COS[idx(a)]; }

    /** Быстрый sin по времени (для анимаций покачивания) */
    public static double sinTime(long periodMs) {
        return Math.sin(System.currentTimeMillis() / (double) periodMs);
    }

    /** Пульсация 0..1 (для свечения) */
    public static double pulse(long periodMs) {
        return (Math.sin(System.currentTimeMillis() * Math.PI * 2 / periodMs) + 1) / 2.0;
    }
}