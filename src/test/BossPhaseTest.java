package test;

import entity.BossPhase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BossPhaseTest {

    @Test public void phase1() {
        assertEquals(BossPhase.Phase.PHASE_1, BossPhase.getPhase(1.0));
    }

    @Test public void phase1Mid() {
        assertEquals(BossPhase.Phase.PHASE_1, BossPhase.getPhase(0.7));
    }

    @Test public void phase2() {
        assertEquals(BossPhase.Phase.PHASE_2, BossPhase.getPhase(0.5));
    }

    @Test public void phase2Low() {
        assertEquals(BossPhase.Phase.PHASE_2, BossPhase.getPhase(0.35));
    }

    @Test public void phase3() {
        assertEquals(BossPhase.Phase.PHASE_3, BossPhase.getPhase(0.3));
    }

    @Test public void phase3Low() {
        assertEquals(BossPhase.Phase.PHASE_3, BossPhase.getPhase(0.0));
    }

    @Test public void speedPhase1() {
        assertEquals(1.0, BossPhase.getSpeedMultiplier(BossPhase.Phase.PHASE_1), 0.01);
    }

    @Test public void speedPhase2() {
        assertEquals(1.3, BossPhase.getSpeedMultiplier(BossPhase.Phase.PHASE_2), 0.01);
    }

    @Test public void speedPhase3() {
        assertEquals(1.6, BossPhase.getSpeedMultiplier(BossPhase.Phase.PHASE_3), 0.01);
    }

    @Test public void damagePhase1() {
        assertEquals(1.0, BossPhase.getDamageMultiplier(BossPhase.Phase.PHASE_1), 0.01);
    }

    @Test public void damagePhase2() {
        assertEquals(1.5, BossPhase.getDamageMultiplier(BossPhase.Phase.PHASE_2), 0.01);
    }

    @Test public void damagePhase3() {
        assertEquals(2.0, BossPhase.getDamageMultiplier(BossPhase.Phase.PHASE_3), 0.01);
    }

    @Test public void auraColorsNotNull() {
        for (BossPhase.Phase p : BossPhase.Phase.values()) {
            assertNotNull(BossPhase.getAuraColor(p));
        }
    }

    @Test public void labelsNotNull() {
        for (BossPhase.Phase p : BossPhase.Phase.values()) {
            assertNotNull(BossPhase.getLabel(p));
            assertFalse(BossPhase.getLabel(p).isEmpty());
        }
    }

    @Test public void threePhases() {
        assertEquals(3, BossPhase.Phase.values().length);
    }
}