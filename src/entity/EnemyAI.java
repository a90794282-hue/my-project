package entity;

import java.util.List;
import java.util.Random;

public class EnemyAI {
    private static final Random RND = new Random();

    // ============ ПАТРУЛЬ ============
    public static class Patrol {
        public double targetX, targetY;
        public long nextPatrolTime = 0;
        public boolean waiting = false;

        public void update(Enemy e, long now) {
            if (waiting) {
                if (now > nextPatrolTime) {
                    waiting = false;
                    pickNewTarget(e);
                }
                return;
            }
            if (Math.hypot(e.x - targetX, e.y - targetY) < 5) {
                waiting = true;
                nextPatrolTime = now + 1000 + RND.nextInt(2000);
            }
        }

        public void pickNewTarget(Enemy e) {
            for (int attempt = 0; attempt < 10; attempt++) {
                double angle = RND.nextDouble() * Math.PI * 2;
                double dist = 64 + RND.nextInt(128);
                double tx = e.x + Math.cos(angle) * dist;
                double ty = e.y + Math.sin(angle) * dist;
                if (!e.isInsideWall(tx, ty)) {
                    targetX = tx;
                    targetY = ty;
                    return;
                }
            }
            targetX = e.x;
            targetY = e.y;
        }
    }

    // ============ ЛУЧНИК ============
    public static class ArcherAI {
        public long lastShotTime = 0;
        public static final long SHOT_COOLDOWN = 2000;

        public boolean canShoot() {
            return System.currentTimeMillis() - lastShotTime > SHOT_COOLDOWN;
        }

        public void shoot() {
            lastShotTime = System.currentTimeMillis();
        }
    }

    // ============ ПРЫЖОК ============
    public static class JumpAI {
        public long lastJumpTime = 0;
        public boolean jumping = false;
        public double jumpVx = 0, jumpVy = 0;
        public static final long JUMP_COOLDOWN = 3000;

        public boolean canJump() {
            return !jumping && System.currentTimeMillis() - lastJumpTime > JUMP_COOLDOWN;
        }

        public void startJump(double dx, double dy) {
            jumping = true;
            double len = Math.hypot(dx, dy);
            if (len < 0.01) len = 1;
            jumpVx = dx / len * 5;
            jumpVy = dy / len * 5;
            lastJumpTime = System.currentTimeMillis();
        }

        public void update() {
            if (!jumping) return;
            jumpVx *= 0.9;
            jumpVy *= 0.9;
            if (Math.abs(jumpVx) < 0.1 && Math.abs(jumpVy) < 0.1) {
                jumping = false;
            }
        }
    }

    // ============ УКЛОНЕНИЕ ============
    public static class DodgeAI {
        public long lastDodgeTime = 0;
        public static final long DODGE_COOLDOWN = 1500;

        public boolean canDodge() {
            return System.currentTimeMillis() - lastDodgeTime > DODGE_COOLDOWN;
        }

        public void dodge(Enemy e, double px, double py) {
            double dx = e.x - px;
            double dy = e.y - py;
            double len = Math.hypot(dx, dy);
            if (len < 0.01) len = 1;
            e.vx = dx / len * 4;
            e.vy = dy / len * 4;
            lastDodgeTime = System.currentTimeMillis();
        }
    }

    public static boolean shouldFlee(Enemy e) {
        return e.hp < e.maxHp * 0.2;
    }

    public static void callForHelp(Enemy caller, List<Enemy> all, Player target) {
        for (Enemy other : all) {
            if (other == caller) continue;
            if (!other.alive) continue;
            double dist = Math.hypot(other.x - caller.x, other.y - caller.y);
            if (dist < 200 && !other.isAggro()) {
                other.setAggro(true);
            }
        }
    }
}