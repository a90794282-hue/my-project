package test;

import entity.Player;
import org.junit.jupiter.api.Test;
import skill.SkillTree;
import static org.junit.jupiter.api.Assertions.*;

public class SkillTreeTest {

    @Test
    public void testInitialState() {
        SkillTree tree = new SkillTree();
        assertEquals(0, tree.getSkillPoints());
        assertEquals(28, tree.getAllSkills().size());
    }

    @Test
    public void testAddSkillPoints() {
        SkillTree tree = new SkillTree();
        tree.addSkillPoints(5);
        assertEquals(5, tree.getSkillPoints());
    }

    @Test
    public void testSetSkillPoints() {
        SkillTree tree = new SkillTree();
        tree.setSkillPoints(10);
        assertEquals(10, tree.getSkillPoints());
    }

    @Test
    public void testUnlockNotEnoughPoints() {
        SkillTree tree = new SkillTree();
        Player p = new Player();
        p.setLevel(10, 0, 0);

        SkillTree.Skill skill = tree.getAllSkills().get(0);
        assertFalse(tree.unlock(skill, p));
        assertFalse(skill.unlocked);
    }

    @Test
    public void testUnlockNotEnoughLevel() {
        SkillTree tree = new SkillTree();
        tree.addSkillPoints(10);
        Player p = new Player();  // level 1

        SkillTree.Skill skill = tree.getAllSkills().get(6);  // требует 20 уровень
        assertFalse(tree.unlock(skill, p));
    }

    @Test
    public void testSuccessfulUnlock() {
        SkillTree tree = new SkillTree();
        tree.addSkillPoints(10);
        Player p = new Player();
        p.setLevel(5, 0, 0);

        SkillTree.Skill skill = tree.getAllSkills().get(0);  // требует 2 уровень, 1 очко
        assertTrue(tree.unlock(skill, p));
        assertTrue(skill.unlocked);
        assertEquals(9, tree.getSkillPoints());
    }

    @Test
    public void testCannotUnlockTwice() {
        SkillTree tree = new SkillTree();
        tree.addSkillPoints(10);
        Player p = new Player();
        p.setLevel(5, 0, 0);

        SkillTree.Skill skill = tree.getAllSkills().get(0);
        tree.unlock(skill, p);
        assertFalse(tree.unlock(skill, p));  // второй раз не работает
    }
}