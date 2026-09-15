package game;

public class Config {
    // ===== Экран =====
    public static int SCREEN_W = 1024;
    public static int SCREEN_H = 768;
    public static final int FPS = 60;
    public static final int TILE_SIZE = 32;

    // ===== Игрок =====
    public static final double PLAYER_ACCEL = 0.8;
    public static final double PLAYER_FRICTION = 0.85;
    public static final double PLAYER_DASH_SPEED = 15.0;
    public static final int PLAYER_DASH_TIME = 20;
    public static final int PLAYER_DASH_COOLDOWN = 60;
    public static final double PLAYER_ATTACK_RANGE = 70;

    // ===== Враг =====
    public static final double ENEMY_DETECTION_RADIUS = 320;
    public static final double ENEMY_LOSE_RADIUS = 480;
    public static final double ENEMY_ATTACK_RADIUS = 45;
    public static final double ENEMY_ACCEL = 0.3;
    public static final double ENEMY_FRICTION = 0.85;

    // ===== Спавн =====
    public static final int SPAWN_INTERVAL_WORLD = 240;
    public static final int SPAWN_INTERVAL_DUNGEON = 120;
    public static final int SPAWN_MAX_WORLD = 8;
    public static final int SPAWN_MAX_DUNGEON = 20;

    // ===== Прогрессия =====
    public static final int XP_BASE = 100;
    public static final double XP_GROWTH = 1.35;
    public static final int STAT_POINTS_PER_LEVEL = 3;

    // ===== БАЛАНС =====
    public static final double XP_GROWTH_LATE = 1.5;
    public static final int XP_LATE_LEVEL = 10;
    public static final double RARITY_COMMON = 0.55;
    public static final double RARITY_RARE = 0.30;
    public static final double RARITY_EPIC = 0.12;
    public static final double RARITY_LEGENDARY = 0.03;
    public static final int PRICE_POTION_HP = 50;
    public static final int PRICE_POTION_MANA = 40;
    public static final int PRICE_WEAPON_COMMON = 100;
    public static final int PRICE_WEAPON_RARE = 300;
    public static final int PRICE_WEAPON_EPIC = 800;
    public static final int PRICE_WEAPON_LEGENDARY = 2000;

    // ===== Освещение =====
    public static final int LIGHT_RADIUS_WORLD = 300;
    public static final int LIGHT_RADIUS_DUNGEON = 220;

    // ===== Время суток =====
    public static final int DAY_LENGTH_SECONDS = 120;
    public static final boolean TIME_CYCLE_ENABLED = true;

    // ===== UI =====
    public static final int TOAST_DURATION_MS = 3000;
    public static final int DAMAGE_NUMBER_LIFE = 60;

    // ===== НАСТРОЙКИ =====
    public static boolean showFps = true;
    public static boolean showDebug = true;
    public static boolean soundEnabled = true;
    public static int musicVolume = 25;
    public static int sfxVolume = 40;
    public static boolean showDamageNumbers = true;
    public static boolean showLighting = true;
    public static boolean fullscreen = false;
}