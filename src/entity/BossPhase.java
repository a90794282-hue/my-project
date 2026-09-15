package entity;

import java.awt.Color;

public class BossPhase {
    public enum Phase { PHASE_1, PHASE_2, PHASE_3 }

    public static Phase getPhase(double hpPercent) {
        if (hpPercent > 0.66) return Phase.PHASE_1;
        if (hpPercent > 0.33) return Phase.PHASE_2;
        return Phase.PHASE_3;
    }

    public static double getSpeedMultiplier(Phase p) {
        return switch (p) {
            case PHASE_1 -> 1.0;
            case PHASE_2 -> 1.3;
            case PHASE_3 -> 1.6;
        };
    }

    public static double getDamageMultiplier(Phase p) {
        return switch (p) {
            case PHASE_1 -> 1.0;
            case PHASE_2 -> 1.5;
            case PHASE_3 -> 2.0;
        };
    }

    public static Color getAuraColor(Phase p) {
        return switch (p) {
            case PHASE_1 -> new Color(255, 200, 0, 100);
            case PHASE_2 -> new Color(255, 100, 0, 120);
            case PHASE_3 -> new Color(255, 0, 0, 150);
        };
    }

    public static String getLabel(Phase p) {
        return switch (p) {
            case PHASE_1 -> "ФАЗА 1";
            case PHASE_2 -> "ФАЗА 2";
            case PHASE_3 -> "ЯРОСТЬ!";
        };
    }
}