package entity;

public class Stats {

    public enum ScalingStat { STRENGTH, DEXTERITY, INTELLIGENCE }

    private int strength = 5;
    private int dexterity = 5;
    private int intelligence = 5;
    private int vitality = 5;

    public int getMaxHp()        { return 50 + vitality * 10 + strength * 2; }
    public int getMaxMana()      { return 20 + intelligence * 8; }
    public int getMeleeDamage()  { return strength * 2; }
    public int getRangedDamage() { return dexterity * 2; }
    public int getSpellDamage()  { return intelligence * 3; }

    public int getCritChance()   { return 5 + dexterity / 2; }
    public int getCritDamage()   { return 150 + strength; }
    public int getDodgeChance()  { return dexterity / 3; }
    public int getManaRegen()    { return 1 + intelligence / 10; }
    public int getHpRegen()      { return 1 + vitality / 10; }
    public int getMoveSpeed()    { return 4 + dexterity / 20; }
    public int getAttackSpeed()  { return 30 - dexterity / 5; }

    public int getFireResist()   { return 0; }
    public int getIceResist()    { return 0; }
    public int getPoisonResist() { return 0; }

    public String getClassName() {
        boolean s = strength > 10, d = dexterity > 10, i = intelligence > 10, v = vitality > 10;
        if (s && i && d) return "Архимаг-воин";
        if (s && i) return "Маг-рыцарь";
        if (d && i) return "Магический лучник";
        if (s && d) return "Паладин-охотник";
        if (v && i) return "Тёмный маг";
        if (s) return "Рыцарь";
        if (d) return "Лучник";
        if (i) return "Маг";
        if (v) return "Танк";
        return "Новичок";
    }

    public int getStrength()     { return strength; }
    public int getDexterity()    { return dexterity; }
    public int getIntelligence() { return intelligence; }
    public int getVitality()     { return vitality; }

    public void setStrength(int v)     { strength = v; }
    public void setDexterity(int v)    { dexterity = v; }
    public void setIntelligence(int v) { intelligence = v; }
    public void setVitality(int v)     { vitality = v; }

    public void addStrength(int v)     { strength += v; }
    public void addDexterity(int v)    { dexterity += v; }
    public void addIntelligence(int v) { intelligence += v; }
    public void addVitality(int v)     { vitality += v; }
}