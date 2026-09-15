package skill;

import entity.Player;
import entity.Stats;

import java.util.ArrayList;
import java.util.List;

public class SkillTree {
    public enum Branch { WARRIOR, RANGER, MAGE, NEUTRAL }

    public static class Skill {
        public String name;
        public String description;
        public Branch branch;
        public int requiredLevel;
        public int cost;
        public boolean unlocked = false;

        public Skill(String name, String desc, Branch branch, int req, int cost) {
            this.name = name;
            this.description = desc;
            this.branch = branch;
            this.requiredLevel = req;
            this.cost = cost;
        }
    }

    private List<Skill> allSkills = new ArrayList<>();
    private int skillPoints = 0;

    public SkillTree() {
        // ===== WARRIOR =====
        allSkills.add(new Skill("Крепкий удар", "+20% урона в ближнем бою", Branch.WARRIOR, 2, 1));
        allSkills.add(new Skill("Толстая кожа", "+20% HP", Branch.WARRIOR, 3, 1));
        allSkills.add(new Skill("Ярость", "+50% урона при HP<30%", Branch.WARRIOR, 5, 2));
        allSkills.add(new Skill("Несокрушимость", "+15% защиты", Branch.WARRIOR, 8, 3));
        allSkills.add(new Skill("Берсерк", "+30% скорости атаки", Branch.WARRIOR, 12, 3));
        allSkills.add(new Skill("Двойной удар", "Атака бьёт дважды", Branch.WARRIOR, 15, 4));
        allSkills.add(new Skill("Титан", "+100 HP", Branch.WARRIOR, 20, 5));

        // ===== RANGER =====
        allSkills.add(new Skill("Меткий глаз", "+20% дальнего урона", Branch.RANGER, 2, 1));
        allSkills.add(new Skill("Быстрые ноги", "+20% скорости", Branch.RANGER, 3, 1));
        allSkills.add(new Skill("Двойной выстрел", "Лук бьёт дважды", Branch.RANGER, 5, 2));
        allSkills.add(new Skill("Тень", "+15% уклонения", Branch.RANGER, 8, 3));
        allSkills.add(new Skill("Снайпер", "+50% урона по дальним", Branch.RANGER, 12, 3));
        allSkills.add(new Skill("Отравленные стрелы", "Наносят яд", Branch.RANGER, 15, 4));
        allSkills.add(new Skill("Прыжок", "Двойной прыжок", Branch.RANGER, 20, 5));

        // ===== MAGE =====
        allSkills.add(new Skill("Сила магии", "+25% урона заклинаний", Branch.MAGE, 2, 1));
        allSkills.add(new Skill("Мана-поток", "+50% маны", Branch.MAGE, 3, 1));
        allSkills.add(new Skill("Быстрый каст", "-30% кулдаун", Branch.MAGE, 5, 2));
        allSkills.add(new Skill("Магический щит", "Поглощает 20% урона маной", Branch.MAGE, 8, 3));
        allSkills.add(new Skill("Цепная молния", "Молния по 3 целям", Branch.MAGE, 12, 3));
        allSkills.add(new Skill("Телепорт", "Телепорт на 200 пикселей", Branch.MAGE, 15, 4));
        allSkills.add(new Skill("Архимаг", "+100% маны", Branch.MAGE, 20, 5));

        // ===== NEUTRAL =====
        allSkills.add(new Skill("Регенерация", "+2 HP/сек", Branch.NEUTRAL, 3, 1));
        allSkills.add(new Skill("Собиратель", "Авто-подбор лута", Branch.NEUTRAL, 5, 2));
        allSkills.add(new Skill("Удача", "+20% к дропу", Branch.NEUTRAL, 8, 3));
        allSkills.add(new Skill("Крепкий сон", "+50% регена в паузе", Branch.NEUTRAL, 10, 2));
        allSkills.add(new Skill("Аура света", "Радиус света +50%", Branch.NEUTRAL, 12, 2));
        allSkills.add(new Skill("Мастер лута", "Авто-продажа хлама", Branch.NEUTRAL, 15, 3));
        allSkills.add(new Skill("Универсал", "+1 ко всем статам", Branch.NEUTRAL, 20, 5));
    }

    public void addPoint() { skillPoints++; }

    public void addSkillPoints(int n) { skillPoints += n; }

    // ⬇️ ДОБАВЛЕНО
    public void setSkillPoints(int n) { this.skillPoints = n; }

    public boolean canUnlock(Skill s, Player p) {
        if (s.unlocked) return false;
        if (skillPoints < s.cost) return false;
        return p.getLevel() >= s.requiredLevel;
    }

    public boolean unlock(Skill s, Player p) {
        if (!canUnlock(s, p)) return false;
        skillPoints -= s.cost;
        s.unlocked = true;
        applySkill(s, p);
        return true;
    }

    private void applySkill(Skill s, Player p) {
        Stats st = p.getStats();
        switch (s.name) {
            case "Крепкий удар" -> st.addStrength(3);
            case "Толстая кожа" -> st.addVitality(5);
            case "Ярость" -> st.addStrength(5);
            case "Несокрушимость" -> st.addVitality(4);
            case "Берсерк" -> st.addDexterity(5);
            case "Двойной удар" -> st.addStrength(7);
            case "Титан" -> st.addVitality(10);

            case "Меткий глаз" -> st.addDexterity(3);
            case "Быстрые ноги" -> st.addDexterity(4);
            case "Двойной выстрел" -> st.addDexterity(5);
            case "Тень" -> st.addDexterity(4);
            case "Снайпер" -> st.addDexterity(6);
            case "Отравленные стрелы" -> st.addDexterity(5);
            case "Прыжок" -> st.addDexterity(7);

            case "Сила магии" -> st.addIntelligence(4);
            case "Мана-поток" -> st.addIntelligence(5);
            case "Быстрый каст" -> st.addIntelligence(4);
            case "Магический щит" -> st.addIntelligence(5);
            case "Цепная молния" -> st.addIntelligence(6);
            case "Телепорт" -> st.addIntelligence(5);
            case "Архимаг" -> st.addIntelligence(10);

            case "Регенерация" -> st.addVitality(3);
            case "Собиратель" -> st.addDexterity(3);
            case "Удача" -> st.addDexterity(4);
            case "Крепкий сон" -> st.addVitality(4);
            case "Аура света" -> st.addIntelligence(3);
            case "Мастер лута" -> st.addDexterity(5);
            case "Универсал" -> {
                st.addStrength(1);
                st.addDexterity(1);
                st.addIntelligence(1);
                st.addVitality(1);
            }
        }
    }

    public List<Skill> getAllSkills() { return allSkills; }
    public int getSkillPoints() { return skillPoints; }
}