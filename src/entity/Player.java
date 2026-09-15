package entity;

import game.Config;
import item.Inventory;
import item.Weapon;
import magic.Effect;
import magic.Spell;
import ui.Animator;
import ui.PlayerSprite;
import ui.Renderer;
import ui.SpriteLoader;
import ui.SpriteRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    public int mana, maxMana;
    private Stats stats = new Stats();
    private Weapon weapon;
    private Weapon equippedWeapon = null;
    private Inventory inventory = new Inventory();
    private List<Spell> spells = new ArrayList<>();
    private List<Effect> effects = new ArrayList<>();

    private int level = 1, xp = 0, statPoints = 0;
    private int attackCooldown = 0;
    private int gold = 0;
    private int regenTimer = 0;

    public double vx = 0, vy = 0;
    private static final double ACCEL = 0.8;
    private static final double FRICTION = 0.85;
    private boolean moving = false;

    public int facingX = 1, facingY = 0;

    private double walkBob = 0;

    private boolean dead = false;
    private int deathTimer = 0;
    public double spawnX = 100, spawnY = 100;

    public Appearance appearance = new Appearance();

    private int dashTimer = 0;

    private long lastStepSoundTime = 0;
    private static final long STEP_INTERVAL_MS = 350;

    private int attackAnimTimer = 0;

    private Animator walkAnimDown, walkAnimUp, walkAnimLeft, walkAnimRight;
    private Animator attackAnimDown, attackAnimUp, attackAnimLeft, attackAnimRight;
    private Animator currentAttackAnim;
    private boolean animatorsLoaded = false;

    private BufferedImage cachedIdle = null;

    public Player() {
        this.width = 64;
        this.height = 64;
        maxHp = stats.getMaxHp();
        hp = maxHp;
        maxMana = stats.getMaxMana();
        mana = maxMana;
        weapon = new Weapon("Ржавый меч", Weapon.Type.SWORD, 5);
    }

    @Override
    public void update() {
        if (dead) {
            deathTimer++;
            return;
        }

        if (!animatorsLoaded) {
            animatorsLoaded = true;
            loadPlayerSprites();
        }

        if (++regenTimer >= 60) {
            regenTimer = 0;
            if (mana < maxMana) mana = Math.min(maxMana, mana + stats.getManaRegen());
            if (hp < maxHp) hp = Math.min(hp + stats.getHpRegen(), maxHp);
        }

        if (dashTimer > 0) {
            dashTimer--;
            x += vx;
            y += vy;
            vx *= 0.92;
            vy *= 0.92;
            if (Math.abs(vx) < 0.5) vx = 0;
            if (Math.abs(vy) < 0.5) vy = 0;
        } else {
            x += vx;
            y += vy;
            vx *= FRICTION;
            vy *= FRICTION;
            if (Math.abs(vx) < 0.05) vx = 0;
            if (Math.abs(vy) < 0.05) vy = 0;
        }

        if (moving) {
            walkBob = Math.sin(System.currentTimeMillis() / 100.0) * 3;
        } else {
            walkBob *= 0.9;
        }

        if (moving && Math.hypot(vx, vy) > 0.5) {
            if (walkAnimDown != null) walkAnimDown.update();
            if (walkAnimUp != null) walkAnimUp.update();
            if (walkAnimLeft != null) walkAnimLeft.update();
            if (walkAnimRight != null) walkAnimRight.update();
        }

        if (attackAnimTimer > 0 && currentAttackAnim != null) {
            currentAttackAnim.update();
        }

        if (moving && Math.hypot(vx, vy) > 1.0
                && System.currentTimeMillis() - lastStepSoundTime > STEP_INTERVAL_MS) {
            lastStepSoundTime = System.currentTimeMillis();
            game.SoundManager.play("step");
        }

        if (attackAnimTimer > 0) attackAnimTimer--;

        effects.removeIf(e -> { e.apply(this); return e.isExpired(); });
        if (attackCooldown > 0) attackCooldown--;
        for (Spell s : spells) s.tick();

        maxHp = stats.getMaxHp();
        maxMana = stats.getMaxMana();
    }

    public void die() {
        if (dead) return;
        dead = true;
        deathTimer = 0;
        vx = 0;
        vy = 0;
    }

    public void respawn() {
        dead = false;
        deathTimer = 0;
        hp = maxHp;
        mana = maxMana;
        x = spawnX;
        y = spawnY;
        vx = 0;
        vy = 0;
        effects.clear();
        moving = false;
    }

    @Override
    public void takeDamage(int dmg) {
        if (dead) return;
        super.takeDamage(dmg);
    }

    public void addVelocity(double dx, double dy) {
        if (dead) return;
        if (dashTimer > 0) return;

        vx += dx * ACCEL;
        vy += dy * ACCEL;

        double speed = stats.getMoveSpeed();
        double len = Math.hypot(vx, vy);
        if (len > speed) {
            vx = (vx / len) * speed;
            vy = (vy / len) * speed;
        }

        if (dx != 0 || dy != 0) {
            double dlen = Math.hypot(dx, dy);
            facingX = (int) Math.round(dx / dlen);
            facingY = (int) Math.round(dy / dlen);
            moving = true;
        }
    }

    public void faceTowards(double tx, double ty) {
        double dx = tx - (x + width / 2.0);
        double dy = ty - (y + height / 2.0);
        if (Math.abs(dx) > Math.abs(dy)) {
            facingX = dx > 0 ? 1 : -1;
            facingY = 0;
        } else if (dy != 0) {
            facingX = 0;
            facingY = dy > 0 ? 1 : -1;
        }
    }

    public void dash() {
        double len = Math.hypot(vx, vy);
        if (len < 0.1) {
            vx = facingX * 15;
            vy = facingY * 15;
        } else {
            vx = (vx / len) * 15;
            vy = (vy / len) * 15;
        }
        dashTimer = 20;
    }

    public int getDashTimer() { return dashTimer; }
    public int getAttackAnimTimer() { return attackAnimTimer; }

    @Override
    public void draw(Graphics2D g) {
        Renderer.enableQuality(g);

        if (dead) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillOval((int) x, (int) y, width, height);
            return;
        }

        int cx = (int) x + width / 2;
        int cy = (int) y + height / 2 + (int) walkBob;

        Renderer.drawGlow(g, cx, cy, width / 2, new Color(255, 80, 80));

        if (!animatorsLoaded) {
            animatorsLoaded = true;
            loadPlayerSprites();
        }

        BufferedImage sprite = null;

        if (attackAnimTimer > 0 && currentAttackAnim != null
                && currentAttackAnim.hasFrames()) {
            sprite = currentAttackAnim.getCurrentFrame();
        } else if (moving && Math.hypot(vx, vy) > 0.5) {
            Animator walk = getWalkAnimator();
            if (walk != null && walk.hasFrames()) {
                sprite = walk.getCurrentFrame();
            }
        }

        if (sprite == null) sprite = cachedIdle;

        if (sprite != null) {
            Renderer.enablePixelArt(g);
            Renderer.drawSprite(g, sprite, cx, cy, width, height, facingX);
        } else {
            SpriteRenderer.drawPlayer(g, cx, cy, facingX, facingY, walkBob,
                    0, appearance, weapon);
        }
    }

    private Animator getWalkAnimator() {
        if (facingY < 0) return walkAnimUp;
        if (facingY > 0) return walkAnimDown;
        if (facingX < 0) return walkAnimLeft;
        return walkAnimRight;
    }

    private Animator getAttackAnimator() {
        if (facingY < 0) return attackAnimUp;
        if (facingY > 0) return attackAnimDown;
        if (facingX < 0) return attackAnimLeft;
        return attackAnimRight;
    }

    private void loadPlayerSprites() {
        walkAnimDown = new Animator(
                SpriteLoader.loadFrames("/sprites/player/walk_down", 13), 80, true);
        walkAnimUp = new Animator(
                SpriteLoader.loadFrames("/sprites/player/walk_up", 13), 80, true);
        walkAnimLeft = new Animator(
                SpriteLoader.loadFrames("/sprites/player/walk_left", 13), 80, true);
        walkAnimRight = new Animator(
                SpriteLoader.loadFrames("/sprites/player/walk_right", 13), 80, true);

        attackAnimDown = new Animator(
                SpriteLoader.loadFrames("/sprites/player/slash_down", 13), 60, false);
        attackAnimUp = new Animator(
                SpriteLoader.loadFrames("/sprites/player/slash_up", 13), 60, false);
        attackAnimLeft = new Animator(
                SpriteLoader.loadFrames("/sprites/player/slash_left", 13), 60, false);
        attackAnimRight = new Animator(
                SpriteLoader.loadFrames("/sprites/player/slash_right", 13), 60, false);

        cachedIdle = SpriteLoader.load("/sprites/player/idle_down_1.png");
        if (cachedIdle == null) cachedIdle = SpriteLoader.findAny("/sprites/player");

        if (!walkAnimDown.hasFrames() && cachedIdle != null) {
            List<BufferedImage> single = new ArrayList<>();
            single.add(cachedIdle);
            walkAnimDown = new Animator(single, 150, true);
            walkAnimUp = new Animator(single, 150, true);
            walkAnimLeft = new Animator(single, 150, true);
            walkAnimRight = new Animator(single, 150, true);
        }
    }

    public int attack() {
        if (dead) return 0;
        if (attackCooldown > 0) return 0;
        attackCooldown = stats.getAttackSpeed();
        attackAnimTimer = 15;

        currentAttackAnim = getAttackAnimator();
        if (currentAttackAnim != null) currentAttackAnim.reset();

        int dmg = getEquippedWeapon().getDamage(stats);
        boolean isCrit = Math.random() * 100 < stats.getCritChance();
        if (isCrit) dmg = dmg * stats.getCritDamage() / 100;
        return dmg;
    }

    public void gainXp(int amount) {
        xp += amount;
        while (xp >= getXpToNext()) {
            xp -= getXpToNext();
            level++;
            statPoints += 3;
            hp = maxHp;
            mana = maxMana;
            game.SoundManager.play("levelup");
        }
    }

    public int getXpToNext() {
        if (level < Config.XP_LATE_LEVEL) {
            return (int) (Config.XP_BASE * Math.pow(level, Config.XP_GROWTH));
        }
        int baseXp = (int) (Config.XP_BASE * Math.pow(Config.XP_LATE_LEVEL, Config.XP_GROWTH));
        int extra = level - Config.XP_LATE_LEVEL + 1;
        return baseXp + (int) (Config.XP_BASE * Math.pow(extra, Config.XP_GROWTH_LATE));
    }

    public boolean spendPoint(Stats.ScalingStat stat) {
        if (statPoints <= 0) return false;
        statPoints--;
        switch (stat) {
            case STRENGTH     -> stats.addStrength(1);
            case DEXTERITY    -> stats.addDexterity(1);
            case INTELLIGENCE -> stats.addIntelligence(1);
        }
        return true;
    }

    public void spendVitality() {
        if (statPoints <= 0) return;
        statPoints--;
        stats.addVitality(1);
    }

    public void setLevel(int lvl, int xpVal, int statPts) {
        this.level = lvl;
        this.xp = xpVal;
        this.statPoints = statPts;
        recalcStats();
    }

    private void recalcStats() {
        maxHp = stats.getMaxHp();
        maxMana = stats.getMaxMana();
        if (hp > maxHp) hp = maxHp;
        if (mana > maxMana) mana = maxMana;
    }

    public void setGold(int g) { this.gold = Math.max(0, g); }

    public Stats getStats() { return stats; }
    public Weapon getWeapon() { return weapon; }
    public Inventory getInventory() { return inventory; }
    public List<Spell> getSpells() { return spells; }
    public List<Effect> getEffects() { return effects; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getStatPoints() { return statPoints; }
    public int getGold() { return gold; }
    public String getClassName() { return stats.getClassName(); }

    public boolean isDead() { return dead; }
    public int getDeathTimer() { return deathTimer; }

    public Appearance getAppearance() { return appearance; }
    public void setAppearance(Appearance a) { this.appearance = a; }

    public void setWeapon(Weapon w) { weapon = w; }
    public void addGold(int g) { gold = Math.max(0, gold + g); }
    public void addSpell(Spell s) { spells.add(s); }
    public void addEffect(Effect e) { effects.add(e); }
    public void setMoving(boolean m) { this.moving = m; }

    public Weapon getEquippedWeapon() {
        return equippedWeapon != null ? equippedWeapon : weapon;
    }

    public void equipWeapon(Weapon w) {
        this.equippedWeapon = w;
    }
}