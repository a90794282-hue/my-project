package entity;

import game.ColorCache;
import game.FontCache;
import ui.Animator;
import ui.Renderer;
import ui.SpriteLoader;
import ui.SpriteRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy extends Entity {
    public enum Type { SLIME, GOBLIN, ORC, SKELETON }

    private static final Random RND = new Random();

    private static world.TileMap currentMap = null;

    public static void setCurrentMap(world.TileMap map) {
        currentMap = map;
    }

    public Type type;
    public int damage;
    public int xpReward;
    public int speed;
    private int attackCooldown = 0;
    private Player target;

    private static final double DETECTION_RADIUS = 320;
    private static final double LOSE_RADIUS = 480;
    private static final double ATTACK_RADIUS = 45;

    private boolean aggro = false;
    private double spawnX, spawnY;

    public double vx = 0, vy = 0;
    private static final double ACCEL = 0.3;
    private static final double FRICTION = 0.85;

    private long idleTime = 0;

    private boolean elite = false;
    private boolean boss = false;
    private double pulseTime = 0;

    private boolean dying = false;
    private int deathAnimTimer = 0;

    private long lastDamageTime = 0;

    private EnemyAI.Patrol patrol = new EnemyAI.Patrol();
    private EnemyAI.ArcherAI archer = new EnemyAI.ArcherAI();
    private EnemyAI.JumpAI jump = new EnemyAI.JumpAI();
    private EnemyAI.DodgeAI dodge = new EnemyAI.DodgeAI();

    private BossPhase.Phase currentPhase = BossPhase.Phase.PHASE_1;
    private long lastPhaseChangeTime = 0;

    // ===== Анимации по направлениям =====
    private Animator walkDown, walkUp, walkLeft, walkRight;
    private Animator idleDown;
    private boolean animatorsLoaded = false;

    // ===== Направление взгляда =====
    private int facingX = 0;
    private int facingY = 1;   // по умолчанию смотрит вниз

    public Enemy(Type type, int x, int y, Player target) {
        this(type, x, y, target, 1);
    }

    public Enemy(Type type, int x, int y, Player target, int playerLevel) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.target = target;
        this.spawnX = x;
        this.spawnY = y;

        switch (type) {
            case SLIME    -> { maxHp = 30;  damage = 5;  xpReward = 20;  speed = 1; }
            case GOBLIN   -> { maxHp = 50;  damage = 10; xpReward = 40;  speed = 2; }
            case ORC      -> { maxHp = 100; damage = 20; xpReward = 80;  speed = 2; }
            case SKELETON -> { maxHp = 70;  damage = 15; xpReward = 60;  speed = 3; }
        }

        double scale = 1.0 + (playerLevel - 1) * 0.15;
        if (scale > 5.0) scale = 5.0;

        maxHp = (int) (maxHp * scale);
        damage = (int) (damage * scale);
        xpReward = (int) (xpReward * (1 + (playerLevel - 1) * 0.2));

        hp = maxHp;
    }

    public void makeElite() {
        elite = true;
        maxHp = (int) (maxHp * 3);
        hp = maxHp;
        damage = (int) (damage * 2);
        xpReward *= 3;
        speed += 1;
        width = (int) (width * 1.3);
        height = (int) (height * 1.3);
    }

    public void makeBoss() {
        boss = true;
        elite = true;
        maxHp = (int) (maxHp * 10);
        hp = maxHp;
        damage = (int) (damage * 3);
        xpReward *= 10;
        speed += 1;
        width = (int) (width * 2.0);
        height = (int) (height * 2.0);
    }

    public boolean isElite() { return elite; }
    public boolean isBoss() { return boss; }
    public boolean isDying() { return dying; }
    public boolean isAggro() { return aggro; }
    public void setAggro(boolean a) { this.aggro = a; }
    public int getDeathTimer() { return deathAnimTimer; }

    public boolean isInsideWall(double px, double py) {
        if (currentMap == null) return false;
        return currentMap.isSolid(px, py);
    }

    @Override
    public void takeDamage(int dmg) {
        if (dying) return;
        super.takeDamage(dmg);
        lastDamageTime = System.currentTimeMillis();
        if (!alive) {
            dying = true;
            deathAnimTimer = 30;
        }
    }

    @Override
    public void update() {
        if (dying) {
            deathAnimTimer--;
            if (deathAnimTimer <= 0) deathAnimTimer = 0;
            return;
        }
        if (!alive || target == null || !target.alive) return;

        // ===== Загрузка спрайтов =====
        if (!animatorsLoaded) {
            animatorsLoaded = true;
            loadEnemyAnimators();
        }

        // ===== Обновление анимаций =====
        if (walkDown != null) walkDown.update();
        if (walkUp != null) walkUp.update();
        if (walkLeft != null) walkLeft.update();
        if (walkRight != null) walkRight.update();
        if (idleDown != null) idleDown.update();

        pulseTime += 0.1;
        double dist = distanceTo(target);
        long now = System.currentTimeMillis();
        boolean recentlyDamaged = now - lastDamageTime < 1000;

        // ===== Обновление направления взгляда =====
        if (target != null) {
            double dx = target.x - x;
            double dy = target.y - y;
            if (Math.abs(dx) > Math.abs(dy)) {
                facingX = dx > 0 ? 1 : -1;
                facingY = 0;
            } else {
                facingX = 0;
                facingY = dy > 0 ? 1 : -1;
            }
        }

        // ===== Обнаружение =====
        if (!aggro && dist < DETECTION_RADIUS) {
            aggro = true;
        } else if (aggro && dist > LOSE_RADIUS) {
            aggro = false;
        }

        if (!aggro) {
            // ПАТРУЛЬ
            patrol.update(this, now);
            if (!patrol.waiting) {
                double dx = patrol.targetX - x;
                double dy = patrol.targetY - y;
                double pdist = Math.hypot(dx, dy);
                if (pdist > 5) {
                    vx += (dx / pdist) * ACCEL * 0.5;
                    vy += (dy / pdist) * ACCEL * 0.5;
                    updateFacing(dx, dy);
                }
            }
            double homeDist = Math.hypot(x - spawnX, y - spawnY);
            if (homeDist > 300) {
                double dx = (spawnX - x) / homeDist;
                double dy = (spawnY - y) / homeDist;
                vx += dx * ACCEL * 0.8;
                vy += dy * ACCEL * 0.8;
                updateFacing(dx, dy);
            }
            idleTime++;
        } else {
            // АГРО
            if (EnemyAI.shouldFlee(this) && type != Type.ORC) {
                double dx = (x - target.x) / dist;
                double dy = (y - target.y) / dist;
                vx += dx * ACCEL * 1.5;
                vy += dy * ACCEL * 1.5;
                updateFacing(dx, dy);
            } else if (type == Type.SKELETON) {
                if (dist > 200) {
                    double dx = (target.x - x) / dist;
                    double dy = (target.y - y) / dist;
                    vx += dx * ACCEL;
                    vy += dy * ACCEL;
                    updateFacing(dx, dy);
                } else if (dist < 150) {
                    double dx = (x - target.x) / dist;
                    double dy = (y - target.y) / dist;
                    vx += dx * ACCEL * 0.8;
                    vy += dy * ACCEL * 0.8;
                    updateFacing(dx, dy);
                } else if (archer.canShoot()) {
                    archer.shoot();
                    int dmg = damage;
                    if (boss) dmg = (int)(dmg * BossPhase.getDamageMultiplier(currentPhase));
                    target.takeDamage(dmg);
                }
            } else if (type == Type.SLIME) {
                if (jump.canJump() && dist < 250 && dist > 60) {
                    jump.startJump(target.x - x, target.y - y);
                }
                if (jump.jumping) {
                    vx += jump.jumpVx * 0.2;
                    vy += jump.jumpVy * 0.2;
                    jump.update();
                } else if (dist > ATTACK_RADIUS) {
                    double dx = (target.x - x) / dist;
                    double dy = (target.y - y) / dist;
                    vx += dx * ACCEL;
                    vy += dy * ACCEL;
                    updateFacing(dx, dy);
                } else if (attackCooldown <= 0) {
                    int dmg = damage;
                    if (boss) dmg = (int)(dmg * BossPhase.getDamageMultiplier(currentPhase));
                    target.takeDamage(dmg);
                    attackCooldown = 60;
                }
            } else if (type == Type.GOBLIN) {
                if (recentlyDamaged && dodge.canDodge() && RND.nextDouble() < 0.3) {
                    dodge.dodge(this, target.x, target.y);
                } else if (dist > ATTACK_RADIUS) {
                    double dx = (target.x - x) / dist;
                    double dy = (target.y - y) / dist;
                    vx += dx * ACCEL;
                    vy += dy * ACCEL;
                    updateFacing(dx, dy);
                } else if (attackCooldown <= 0) {
                    int dmg = damage;
                    if (boss) dmg = (int)(dmg * BossPhase.getDamageMultiplier(currentPhase));
                    target.takeDamage(dmg);
                    attackCooldown = 60;
                }
            } else {
                if (dist > ATTACK_RADIUS) {
                    double dx = (target.x - x) / dist;
                    double dy = (target.y - y) / dist;
                    vx += dx * ACCEL;
                    vy += dy * ACCEL;
                    updateFacing(dx, dy);
                } else if (attackCooldown <= 0) {
                    int dmg = damage;
                    if (boss) dmg = (int)(dmg * BossPhase.getDamageMultiplier(currentPhase));
                    target.takeDamage(dmg);
                    attackCooldown = boss ? 40 : 60;
                }
            }
        }

        // ===== Фазы босса =====
        if (boss) {
            double hpPercent = hp / (double) maxHp;
            BossPhase.Phase newPhase = BossPhase.getPhase(hpPercent);
            if (newPhase != currentPhase) {
                currentPhase = newPhase;
                lastPhaseChangeTime = now;
                game.SoundManager.play("levelup");
            }
        }

        // ===== Ограничение скорости =====
        double len = Math.hypot(vx, vy);
        double maxSpeed = speed;
        if (boss) maxSpeed *= BossPhase.getSpeedMultiplier(currentPhase);
        if (len > maxSpeed) {
            vx = (vx / len) * maxSpeed;
            vy = (vy / len) * maxSpeed;
        }

        x += vx;
        y += vy;
        vx *= FRICTION;
        vy *= FRICTION;

        if (attackCooldown > 0) attackCooldown--;
    }

    /** Обновление направления взгляда по вектору движения */
    private void updateFacing(double dx, double dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            facingX = dx > 0 ? 1 : -1;
            facingY = 0;
        } else if (dy != 0) {
            facingX = 0;
            facingY = dy > 0 ? 1 : -1;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int cx = (int) x + width / 2;
        int cy = (int) y + height / 2;

        // ===== Смерть =====
        if (dying) {
            float progress = deathAnimTimer / 30f;
            progress = Math.max(0f, Math.min(1f, progress));

            int targetW = (int) (width * (1 - progress * 0.5));
            int targetH = (int) (height * (1 - progress * 0.5));

            int alpha1 = Math.max(0, Math.min(255, (int) ((1 - progress) * 200)));
            g.setColor(new Color(255, 100, 100, alpha1));
            g.fillOval(cx - targetW / 2, cy - targetH / 2, targetW, targetH);

            if (progress < 0.5) {
                int alpha2 = (int) ((1 - progress * 2) * 150);
                alpha2 = Math.max(0, Math.min(255, alpha2));
                g.setColor(new Color(255, 255, 255, alpha2));
                g.fillOval(cx - targetW / 2, cy - targetH / 2, targetW, targetH);
            }
            return;
        }

        long time = System.currentTimeMillis();
        int animFrame = (int) ((time / 250) % 2);

        if (elite) {
            int r = (int) (40 + Math.sin(pulseTime) * 5);
            Color c;
            if (boss) c = BossPhase.getAuraColor(currentPhase);
            else c = new Color(255, 200, 0, 80);
            g.setColor(c);
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
        }

        if (aggro && !boss) {
            g.setColor(ColorCache.rgba(255, 0, 0, 50));
            g.fillOval(cx - 30, cy - 30, 60, 60);
        }

        // ===== Загрузка =====
        if (!animatorsLoaded) {
            animatorsLoaded = true;
            loadEnemyAnimators();
        }

        // ===== Выбор анимации =====
        BufferedImage sprite = getCurrentSprite();

        if (sprite != null) {
            Renderer.enablePixelArt(g);
            int targetW = boss ? width * 2 : width;
            int targetH = boss ? height * 2 : height;
            Renderer.drawSprite(g, sprite, cx, cy, targetW, targetH, facingX);
        } else {
            // Fallback — примитивы
            switch (type) {
                case SLIME    -> SpriteRenderer.drawSlime(g, cx, cy, animFrame);
                case GOBLIN   -> SpriteRenderer.drawGoblin(g, cx, cy, facingX, animFrame);
                case ORC      -> SpriteRenderer.drawOrc(g, cx, cy, facingX, animFrame);
                case SKELETON -> SpriteRenderer.drawSkeleton(g, cx, cy, facingX, animFrame);
            }
        }

        // ===== HP-бар =====
        int barY = (int) y - (boss ? 40 : 20);
        Renderer.drawHealthBar(g, (int) x, barY, width, 6, hp, maxHp);

        if (boss) {
            g.setColor(new Color(255, 220, 0));
            g.setFont(FontCache.arialBold(22));
            g.drawString("★", cx - 8, barY - 5);

            g.setFont(FontCache.arialBold(12));
            g.setColor(BossPhase.getAuraColor(currentPhase));
            String phaseLabel = BossPhase.getLabel(currentPhase);
            int pw = g.getFontMetrics().stringWidth(phaseLabel);
            g.drawString(phaseLabel, cx - pw / 2, barY - 24);
        }

        if (aggro && !boss) {
            int pulse = (int) (Math.sin(time / 150.0) * 2);
            g.setColor(Color.RED);
            g.setFont(FontCache.arialBold(20 + pulse));
            g.drawString("!", cx - 4, (int) y - 26);
        }
    }

    /** Возвращает текущий кадр для отрисовки */
    private BufferedImage getCurrentSprite() {
        // Определяем какое направление использовать
        if (facingY < 0 && walkUp != null && walkUp.hasFrames()) return walkUp.getCurrentFrame();
        if (facingY > 0 && walkDown != null && walkDown.hasFrames()) return walkDown.getCurrentFrame();
        if (facingX < 0 && walkLeft != null && walkLeft.hasFrames()) return walkLeft.getCurrentFrame();
        if (facingX > 0 && walkRight != null && walkRight.hasFrames()) return walkRight.getCurrentFrame();

        // Fallback на idle_down
        if (idleDown != null && idleDown.hasFrames()) return idleDown.getCurrentFrame();
        return null;
    }

    /** Загрузка спрайтов — 4 кадра на направление */
    private void loadEnemyAnimators() {
        String typeName = type.name().toLowerCase();

        // ⬇️ 4 кадра ходьбы для каждого направления
        walkDown = new Animator(
                SpriteLoader.loadFrames("/sprites/enemy/" + typeName + "_down", 4),
                150, true);
        walkUp = new Animator(
                SpriteLoader.loadFrames("/sprites/enemy/" + typeName + "_up", 4),
                150, true);
        walkLeft = new Animator(
                SpriteLoader.loadFrames("/sprites/enemy/" + typeName + "_left", 4),
                150, true);
        walkRight = new Animator(
                SpriteLoader.loadFrames("/sprites/enemy/" + typeName + "_right", 4),
                150, true);

        // Idle = первый кадр walk_down
        idleDown = new Animator(
                SpriteLoader.loadFrames("/sprites/enemy/" + typeName + "_down", 1),
                300, true);

        // Fallback — если ничего не загрузилось
        if (!walkDown.hasFrames()) {
            List<BufferedImage> single = new ArrayList<>();
            BufferedImage img = SpriteLoader.load("/sprites/enemy/" + typeName + ".png");
            if (img == null) img = SpriteLoader.findAny("/sprites/enemy");
            if (img != null) single.add(img);
            walkDown = new Animator(single, 150, true);
            walkUp = walkDown;
            walkLeft = walkDown;
            walkRight = walkDown;
            idleDown = walkDown;
        }

        // Логи
        System.out.println("[Enemy " + typeName + "] Спрайты:");
        System.out.println("  walk_down: " + walkDown.getFrameCount());
        System.out.println("  walk_up: " + walkUp.getFrameCount());
        System.out.println("  walk_left: " + walkLeft.getFrameCount());
        System.out.println("  walk_right: " + walkRight.getFrameCount());
    }
}