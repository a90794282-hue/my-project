package game;

import achievement.AchievementManager;
import craft.CraftSystem;
import entity.*;
import magic.Spell;
import quest.QuestSystem;
import skill.SkillTree;
import ui.*;
import world.*;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends JPanel {
    // ===== Данные мира =====
    public Player player = new Player();
    public List<Enemy> enemies = new ArrayList<>();
    public Boss boss;

    public GameState state = new GameState();
    public SkillTree skillTree = new SkillTree();
    public AchievementManager achievements = new AchievementManager();
    public NPCManager npcManager = new NPCManager();

    // ===== World =====
    private TileMap map;
    private Camera camera;
    private Weather weather = new Weather();
    public TimeOfDay timeOfDay = new TimeOfDay();

    // ===== UI =====
    public HUD hud = new HUD();
    public MiniMap miniMap = new MiniMap();
    public InventoryUI inventoryUI = new InventoryUI();
    public SkillBar skillBar = new SkillBar();
    public CharacterCreationUI creationUI = new CharacterCreationUI();
    public PauseMenu pauseMenu = new PauseMenu();
    public MainMenu mainMenu = new MainMenu();
    public SettingsMenu settingsMenu = new SettingsMenu();

    public SkillTreeUI skillTreeUI = new SkillTreeUI();
    public CraftSystem craftSystem = new CraftSystem();
    public CraftUI craftUI = new CraftUI();
    public AchievementUI achievementUI = new AchievementUI();
    public AchievementToast achievementToast = new AchievementToast();
    public QuestSystem questSystem = new QuestSystem();
    public QuestUI questUI = new QuestUI();
    public DialogueUI dialogueUI = new DialogueUI();
    public ShopUI shopUI = new ShopUI();
    public TimeOfDayUI timeOfDayUI = new TimeOfDayUI();

    // ===== Системы =====
    public ParticleSystem particles = new ParticleSystem();
    private ParallaxBackground background;
    private BloodDecal bloodDecals = new BloodDecal();
    private LootManager lootManager = new LootManager();
    public PortalManager portalManager = new PortalManager();
    private EnemySpawner spawner;
    public List<DamageNumber> damageNumbers = new ArrayList<>();

    public InputHandler input = new InputHandler();
    private FullscreenManager fullscreen;

    private SpatialGrid spatialGrid = new SpatialGrid(128);
    private BossController bossController = new BossController();
    public ScreenTransition transition = new ScreenTransition();

    // ===== Модули =====
    private GameInputHandler inputHandler;
    private GameOverlayDrawer overlayDrawer;
    private GameUIManager uiManager;

    // ===== Состояние =====
    private long worldSeed;
    public String toastMessage = "";
    public long toastUntil = 0;

    public int dashCooldown = 0;
    private int lastLevel = 1;
    private int enemiesKilled = 0;
    private boolean bossKilled = false;

    private boolean showDiagnostics = false;
    private int currentSaveSlot = 1;
    private long lastAutoSave = 0;

    public Game() {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        camera = new Camera(Config.SCREEN_W, Config.SCREEN_H);
        background = new ParallaxBackground(Config.SCREEN_W, Config.SCREEN_H);

        AchievementManager.globalToast = achievementToast;
        dialogueUI.setShopUI(shopUI);

        inputHandler = new GameInputHandler(this);
        overlayDrawer = new GameOverlayDrawer(this);
        uiManager = new GameUIManager(this);

        addKeyListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
    }

    public void setFullscreenManager(FullscreenManager fm) { this.fullscreen = fm; }

    public TileMap getMap() { return map; }
    public long getWorldSeed() { return worldSeed; }
    public void setWorldSeed(long s) { this.worldSeed = s; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public void setEnemiesKilled(int n) { enemiesKilled = n; }
    public boolean isBossKilled() { return bossKilled; }
    public void setBossKilled(boolean b) { bossKilled = b; }

    // ============= ЖИЗНЕННЫЙ ЦИКЛ =============

    public void loadWorld() {
        worldSeed = System.currentTimeMillis();
        map = WorldGenerator.generate(100, 100, worldSeed);
        Enemy.setCurrentMap(map);

        player.x = map.getPixelWidth() / 2.0;
        player.y = map.getPixelHeight() / 2.0;
        player.spawnX = player.x;
        player.spawnY = player.y;

        state.setLocation(GameState.Location.WORLD);
        portalManager.inDungeon = false;
        portalManager.spawnPortalsInWorld(map, 6);
        npcManager.spawnNPCsInWorld(map, player);

        enemies.clear();
        bloodDecals = new BloodDecal();
        lootManager = new LootManager();
        damageNumbers.clear();
        spawner = new EnemySpawner(Config.SPAWN_INTERVAL_WORLD, Config.SPAWN_MAX_WORLD);
        lastLevel = player.getLevel();
        bossController.setBoss(null);

        SoundManager.playMusic("music_world");
        showToast("Мир загружен");
    }

    public void loadWorldFromSeed(long seed) {
        this.worldSeed = seed;
        this.map = WorldGenerator.generate(100, 100, seed);
        Enemy.setCurrentMap(map);

        portalManager.inDungeon = false;
        portalManager.spawnPortalsInWorld(map, 6);
        npcManager.spawnNPCsInWorld(map, player);

        enemies.clear();
        bloodDecals = new BloodDecal();
        lootManager = new LootManager();
        damageNumbers.clear();
        spawner = new EnemySpawner(Config.SPAWN_INTERVAL_WORLD, Config.SPAWN_MAX_WORLD);

        state.setScreen(GameState.Screen.PLAYING);
        SoundManager.playMusic("music_world");
    }

    public void enterDungeon(Portal portal) {
        state.setLocation(GameState.Location.DUNGEON);
        portalManager.inDungeon = true;
        portalManager.currentDifficulty = portal.difficulty;
        portalManager.activePortal = portal;
        portalManager.returnX = player.x;
        portalManager.returnY = player.y;

        map = DungeonGenerator.generate(
                portal.dungeonWidth, portal.dungeonHeight,
                portal.dungeonSeed, portal.difficulty);
        Enemy.setCurrentMap(map);

        player.x = map.getPixelWidth() / 2.0;
        player.y = map.getPixelHeight() / 2.0;

        npcManager.clear();
        enemies.clear();
        bloodDecals = new BloodDecal();
        lootManager = new LootManager();
        damageNumbers.clear();

        int count = switch (portal.difficulty) {
            case EASY -> 6;
            case MEDIUM -> 10;
            case HARD -> 14;
            case NIGHTMARE -> 20;
        };
        spawner = new EnemySpawner(Config.SPAWN_INTERVAL_DUNGEON, count);
        spawnInitialEnemies(count);

        if (portal.difficulty == Portal.Difficulty.HARD
                || portal.difficulty == Portal.Difficulty.NIGHTMARE) {
            int bx = map.getPixelWidth() / 2;
            int by = map.getPixelHeight() / 2;
            Enemy bossEnemy = new Enemy(Enemy.Type.ORC, bx, by,
                    player, player.getLevel());
            bossEnemy.makeBoss();
            enemies.add(bossEnemy);
            bossController.setBoss(bossEnemy);
            showToast("⚔ Босс!");
        } else {
            bossController.setBoss(null);
            showToast("Подземелье: " + portal.difficulty.getLabel());
        }

        achievements.unlock("Первый портал");
        achievements.onDungeonEntered();
    }

    public void exitDungeon() {
        state.setLocation(GameState.Location.WORLD);
        map = WorldGenerator.generate(100, 100, worldSeed);
        Enemy.setCurrentMap(map);
        player.x = portalManager.returnX;
        player.y = portalManager.returnY;
        player.spawnX = player.x;
        player.spawnY = player.y;

        npcManager.spawnNPCsInWorld(map, player);
        enemies.clear();
        bloodDecals = new BloodDecal();
        lootManager = new LootManager();
        damageNumbers.clear();
        spawner = new EnemySpawner(Config.SPAWN_INTERVAL_WORLD, Config.SPAWN_MAX_WORLD);
        bossController.setBoss(null);

        SoundManager.playMusic("music_world");
        showToast("Возврат в мир");
    }

    private void spawnInitialEnemies(int count) {
        Random rnd = new Random();
        for (int i = 0; i < count; i++) {
            for (int attempt = 0; attempt < 20; attempt++) {
                double angle = rnd.nextDouble() * Math.PI * 2;
                double dist = 300 + rnd.nextDouble() * 500;
                double sx = player.x + Math.cos(angle) * dist;
                double sy = player.y + Math.sin(angle) * dist;
                if (sx < 64 || sy < 64) continue;
                if (sx > map.getPixelWidth() - 64) continue;
                if (sy > map.getPixelHeight() - 64) continue;
                if (map.isSolid(sx, sy)) continue;

                Enemy.Type type = switch (rnd.nextInt(4)) {
                    case 0 -> Enemy.Type.SLIME;
                    case 1 -> Enemy.Type.GOBLIN;
                    case 2 -> Enemy.Type.SKELETON;
                    default -> Enemy.Type.ORC;
                };
                Enemy enemy = new Enemy(type, (int) sx, (int) sy,
                        player, player.getLevel());
                if (rnd.nextDouble() < 0.2) enemy.makeElite();
                enemies.add(enemy);
                break;
            }
        }
    }

    public void start() {
        new Timer(1000 / Config.FPS, e -> {
            Time.tick();
            update();
            repaint();
        }).start();
    }

    // ============= ОБНОВЛЕНИЕ =============

    private void update() {
        camera.setScreenSize(getWidth(), getHeight());
        timeOfDay.update();
        achievementToast.update();
        transition.update();

        if (state.getScreen() == GameState.Screen.MAIN_MENU) {
            mainMenu.update(getWidth(), getHeight());
            mainMenu.updateHover(input.getMouseX(), input.getMouseY(),
                    getWidth(), getHeight());
        }

        // Блокировка ввода во время перехода
        if (transition.isActive()) {
            input.clearFrame();
            return;
        }

        if (input.wasPressed(KeyEvent.VK_F11)) {
            if (fullscreen != null) {
                fullscreen.toggle();
                camera.setScreenSize(getWidth(), getHeight());
            }
            input.clearFrame();
            return;
        }

        if (input.wasPressed(KeyEvent.VK_F12)) {
            showDiagnostics = !showDiagnostics;
            if (showDiagnostics) Diagnostics.printReport();
            input.clearFrame();
            return;
        }
        if (showDiagnostics) {
            if (input.wasPressed(KeyEvent.VK_ESCAPE)) showDiagnostics = false;
            input.clearFrame();
            return;
        }

        if (shopUI.isVisible()) {
            for (int code : input.getPressed()) shopUI.handleKey(code, player);
            input.clearFrame();
            return;
        }

        if (dialogueUI.isVisible()) {
            for (int code : input.getPressed()) {
                if (code == KeyEvent.VK_ESCAPE) {
                    dialogueUI.close();
                    input.reset();
                    input.clearFrame();
                    return;
                }
                dialogueUI.handleKey(code, player, questSystem);
            }
            input.clearFrame();
            return;
        }

        if (input.wasPressed(KeyEvent.VK_F5)) {
            if (state.getScreen() == GameState.Screen.PLAYING) {
                SaveManager.save(this, currentSaveSlot);
            }
            input.clearFrame();
            return;
        }
        if (input.wasPressed(KeyEvent.VK_F9)) {
            SaveManager.load(this, currentSaveSlot);
            input.clearFrame();
            return;
        }

        long now = System.currentTimeMillis();
        if (state.getScreen() == GameState.Screen.PLAYING
                && now - lastAutoSave > 60000) {
            lastAutoSave = now;
            SaveManager.save(this, currentSaveSlot);
        }

        switch (state.getScreen()) {
            case MAIN_MENU -> {
                if (input.wasLeftMousePressed()) {
                    inputHandler.handleMainMenuClick();
                }
                forEachKey(inputHandler::handleMainMenu);
            }
            case CHARACTER_CREATION -> forEachKey(inputHandler::handleCharacterCreation);
            case PAUSED -> {
                pauseMenu.updateHover(input.getMouseX(), input.getMouseY(),
                        getWidth(), getHeight());
                if (input.wasLeftMousePressed()) inputHandler.handlePauseClick();
                forEachKey(inputHandler::handlePause);
            }
            case SETTINGS -> forEachKey(inputHandler::handleSettings);
            case SKILL_TREE -> forEachKey(inputHandler::handleSkillTree);
            case CRAFT -> forEachKey(inputHandler::handleCraft);
            case ACHIEVEMENTS -> forEachKey(inputHandler::handleAchievements);
            case QUESTS -> forEachKey(inputHandler::handleQuests);
            case GAME_OVER -> {
                forEachKey(inputHandler::handleGameOver);
                player.update();
                particles.update();
            }
            default -> handleGameplay();
        }

        input.clearFrame();
    }

    private interface KeyAction { void run(int code); }
    private void forEachKey(KeyAction action) {
        for (int code : input.getPressed()) action.run(code);
    }

    private void handleGameplay() {
        if (dashCooldown > 0) dashCooldown--;

        if (player.getLevel() > lastLevel) {
            int diff = player.getLevel() - lastLevel;
            skillTree.addSkillPoints(diff);
            lastLevel = player.getLevel();
            showToast("+" + diff + " очко скиллов");
        }

        achievements.check(player, enemiesKilled,
                player.getInventory().getItems().size(),
                bossKilled, portalManager.inDungeon);
        questSystem.update(player);

        if (state.getScreen() == GameState.Screen.INVENTORY) {
            for (int code : input.getPressed()) inputHandler.handleGameplay(code);
            return;
        }

        if (player.isDead()) {
            state.setScreen(GameState.Screen.GAME_OVER);
            player.update();
            particles.update();
            return;
        }

        // ===== Клавиши =====
        for (int code : input.getPressed()) inputHandler.handleGameplay(code);

        // ===== АТАКА МЫШЬЮ (ЛКМ) =====
        if (input.wasLeftMousePressed()) {
            int mouseWorldX = input.getMouseX() + (int) camera.x;
            int mouseWorldY = input.getMouseY() + (int) camera.y;
            player.faceTowards(mouseWorldX, mouseWorldY);
            attackEnemies();
        }

        // ===== Правая кнопка — каст =====
        if (input.wasRightMousePressed()) {
            castSpell();
        }

        // ===== Движение WASD =====
        double dx = 0, dy = 0;
        if (input.isDown(KeyEvent.VK_W)) dy -= 1;
        if (input.isDown(KeyEvent.VK_S)) dy += 1;
        if (input.isDown(KeyEvent.VK_A)) dx -= 1;
        if (input.isDown(KeyEvent.VK_D)) dx += 1;

        if (dx != 0 || dy != 0) player.addVelocity(dx, dy);
        else player.setMoving(false);

        double oldX = player.x, oldY = player.y;
        player.update();
        if (map != null) {
            clampPlayerToMap();
            if (map.collides(player.x, player.y, player.width, player.height)) {
                player.x = oldX;
                player.y = oldY;
                player.vx = 0;
                player.vy = 0;
            }
        }

        updateEnemies();
        spawner.update(enemies, player, map);
        bossController.update(enemies, player);
        lootManager.update(player);

        weather.update(particles, getWidth(), getHeight());
        particles.update();
        camera.follow(player);

        damageNumbers.removeIf(DamageNumber::isExpired);
        for (DamageNumber dn : damageNumbers) dn.update();

        checkPlayerDeath();
        removeDeadEnemies();

        for (Spell s : player.getSpells()) s.tick();
    }

    private void updateEnemies() {
        spatialGrid.clear();
        for (Enemy e : enemies) {
            if (e.alive && !e.isDying()) spatialGrid.insert(e);
        }
        for (Enemy e : enemies) {
            double eOldX = e.x, eOldY = e.y;
            e.update();
            if (map != null) {
                clampEntityToMap(e);
                if (map.collides(e.x, e.y, e.width, e.height)) {
                    e.x = eOldX;
                    e.y = eOldY;
                    e.vx = 0;
                    e.vy = 0;
                }
            }
        }
        if (boss != null) boss.update();
    }

    private void checkPlayerDeath() {
        if (player.hp <= 0 && !player.isDead()) {
            player.die();
            SoundManager.play("player_death");
            SoundManager.playMusic("music_menu");
            particles.spawnBlood(player.x + player.width / 2.0,
                    player.y + player.height / 2.0);
            bloodDecals.addBlood(player.x + player.width / 2.0,
                    player.y + player.height / 2.0);
            achievements.onDeath();
        }
    }

    private void removeDeadEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy en = enemies.get(i);
            if (!en.alive && !en.isDying()) {
                particles.spawnSmoke(en.x + en.width / 2.0,
                        en.y + en.height / 2.0,
                        ColorCache.rgb(100, 100, 100));
                bloodDecals.addBlood(en.x + en.width / 2.0,
                        en.y + en.height / 2.0);
                lootManager.spawnLoot(en);
                enemiesKilled++;
                questSystem.onEnemyKilled();
                achievements.onEnemyKilled(en.isBoss());
                if (en.isBoss()) bossKilled = true;
                enemies.remove(i);
            }
        }
    }

    private void clampPlayerToMap() {
        if (player.x < 0) player.x = 0;
        if (player.y < 0) player.y = 0;
        double pw = map.getPixelWidth();
        double ph = map.getPixelHeight();
        if (player.x + player.width > pw) player.x = pw - player.width;
        if (player.y + player.height > ph) player.y = ph - player.height;
    }

    private void clampEntityToMap(Entity e) {
        if (e.x < 0) e.x = 0;
        if (e.y < 0) e.y = 0;
        if (e.x + e.width > map.getPixelWidth()) e.x = map.getPixelWidth() - e.width;
        if (e.y + e.height > map.getPixelHeight()) e.y = map.getPixelHeight() - e.height;
    }

    // ============= ОТРИСОВКА =============

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Renderer.enableQuality(g2);

        switch (state.getScreen()) {
            case MAIN_MENU -> {
                mainMenu.draw(g2, getWidth(), getHeight());
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
            case CHARACTER_CREATION -> {
                creationUI.draw(g2, getWidth(), getHeight());
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
            case SETTINGS -> {
                settingsMenu.draw(g2, getWidth(), getHeight());
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
            case SKILL_TREE -> {
                skillTreeUI.draw(g2, getWidth(), getHeight(), player);
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
            case CRAFT -> {
                craftUI.draw(g2, getWidth(), getHeight(), player);
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
            case ACHIEVEMENTS -> {
                achievementUI.draw(g2, getWidth(), getHeight(), achievements);
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
            case QUESTS -> {
                questUI.draw(g2, getWidth(), getHeight(), questSystem);
                transition.draw(g2, getWidth(), getHeight());
                return;
            }
        }

        int camX = (int) camera.x, camY = (int) camera.y;

        background.draw(g2, camX, camY);

        RenderLayer.drawWorld(g2, camX, camY, map, bloodDecals,
                portalManager.getPortals(), lootManager.getDrops(),
                enemies, player, damageNumbers);

        for (NPC npc : npcManager.getNPCs()) {
            g2.translate(-camX, -camY);
            npc.draw(g2);
            g2.translate(camX, camY);
        }

        if (portalManager.inDungeon && map != null) {
            map.drawTorches(g2, camX, camY);
        }

        particles.setCamera(camX, camY);
        particles.draw(g2);

        weather.applyOverlay(g2, getWidth(), getHeight());
        timeOfDay.applyOverlay(g2, getWidth(), getHeight());

        if (Config.showLighting && !player.isDead()) {
            int radius = portalManager.inDungeon
                    ? Config.LIGHT_RADIUS_DUNGEON : Config.LIGHT_RADIUS_WORLD;
            Lighting.draw(g2, getWidth(), getHeight(),
                    camera.toScreenX(player.x + player.width / 2.0),
                    camera.toScreenY(player.y + player.height / 2.0),
                    radius);
        }

        Renderer.drawVignette(g2, getWidth(), getHeight());

        uiManager.drawAll(g2);
        overlayDrawer.drawAll(g2);

        if (showDiagnostics) {
            Diagnostics.drawInGame(g2, getWidth(), getHeight());
        }

        // Fade-переход ПОВЕРХ всего
        transition.draw(g2, getWidth(), getHeight());
    }

    public void showToast(String msg) {
        this.toastMessage = msg;
        this.toastUntil = System.currentTimeMillis() + Config.TOAST_DURATION_MS;
    }

    public void attackEnemies() {
        double px = player.x + player.width / 2.0;
        double py = player.y + player.height / 2.0;

        List<Entity> nearby = spatialGrid.query(px, py, Config.PLAYER_ATTACK_RANGE);
        boolean hitSomething = false;

        for (Entity ent : nearby) {
            if (!(ent instanceof Enemy en)) continue;
            if (!en.alive || en.isDying()) continue;

            int dmg = player.attack();
            if (dmg > 0) {
                hitSomething = true;
                en.takeDamage(dmg);
                if (Config.showDamageNumbers) {
                    damageNumbers.add(new DamageNumber(
                            en.x + en.width / 2.0, en.y, dmg, false));
                }
                particles.spawnHitSparks(en.x + en.width / 2.0,
                        en.y + en.height / 2.0);
                particles.spawnBlood(en.x + en.width / 2.0,
                        en.y + en.height / 2.0);
                if (!en.alive) {
                    player.gainXp(en.xpReward);
                    SoundManager.play("enemy_death");
                }
            }
        }

        if (hitSomething) SoundManager.play("hit");
    }

    public void castSpell() {
        if (player.getSpells().isEmpty()) {
            showToast("Нет заклинаний");
            return;
        }
        Spell spell = player.getSpells().get(0);
        if (!spell.canCast(player)) {
            showToast("Кулдаун или нет маны");
            return;
        }

        double px = player.x + player.width / 2.0;
        double py = player.y + player.height / 2.0;
        Entity nearest = spatialGrid.queryNearest(px, py, 400);

        if (!(nearest instanceof Enemy target) || !target.alive || target.isDying()) {
            showToast("Нет цели");
            return;
        }

        spell.cast(player, target);
        SoundManager.play("spell");
        particles.spawnMagicBurst(target.x + target.width / 2.0,
                target.y + target.height / 2.0,
                ColorCache.rgb(255, 140, 0));
        damageNumbers.add(new DamageNumber(
                target.x + target.width / 2.0, target.y,
                spell.name, ColorCache.rgb(255, 140, 0)));
    }
}