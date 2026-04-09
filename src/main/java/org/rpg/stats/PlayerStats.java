package org.rpg.stats;
import net.minecraft.nbt.NbtCompound;
public class PlayerStats {

    public int strengthLevel = 1;
    public int agilityLevel = 1;
    public int enduranceLevel = 1;
    public int miningLevel = 1;
    public int magicLevel = 1;

    public int strengthXp = 0;
    public int agilityXp = 0;
    public int enduranceXp = 0;
    public int miningXp = 0;
    public int magicXp = 0;

    public int skillPoints = 0;

    public int strengthSkills = 0;
    public int agilitySkills = 0;
    public int enduranceSkills = 0;
    public int miningSkills = 0;
    public int magicSkills = 0;

    public static int xpForLevel(int level) {
        return 100 + (level - 1) * 50;
    }

    public static final int MAX_LEVEL = 50;

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putInt("strengthLevel", strengthLevel);
        nbt.putInt("agilityLevel", agilityLevel);
        nbt.putInt("enduranceLevel", enduranceLevel);
        nbt.putInt("miningLevel", miningLevel);
        nbt.putInt("magicLevel", magicLevel);

        nbt.putInt("strengthXp", strengthXp);
        nbt.putInt("agilityXp", agilityXp);
        nbt.putInt("enduranceXp", enduranceXp);
        nbt.putInt("miningXp", miningXp);
        nbt.putInt("magicXp", magicXp);

        nbt.putInt("skillPoints", skillPoints);
        nbt.putInt("strengthSkills", strengthSkills);
        nbt.putInt("agilitySkills", agilitySkills);
        nbt.putInt("enduranceSkills", enduranceSkills);
        nbt.putInt("miningSkills", miningSkills);
        nbt.putInt("magicSkills", magicSkills);

        return nbt;
    }

    public static PlayerStats fromNbt(NbtCompound nbt) {
        PlayerStats s = new PlayerStats();

        s.strengthLevel = nbt.getInt("strengthLevel");
        s.agilityLevel = nbt.getInt("agilityLevel");
        s.enduranceLevel = nbt.getInt("enduranceLevel");
        s.magicLevel = nbt.getInt("magicLevel");
        s.miningLevel = nbt.getInt("miningLevel");

        s.strengthXp = nbt.getInt("strengthXp");
        s.agilityXp = nbt.getInt("agilityXp");
        s.enduranceXp = nbt.getInt("enduranceXp");
        s.magicXp = nbt.getInt("magicXp");
        s.miningXp = nbt.getInt("miningXp");

        s.skillPoints = nbt.getInt("skillPoints");
        s.strengthSkills = nbt.getInt("strengthSkills");
        s.agilitySkills = nbt.getInt("agilitySkills");
        s.enduranceSkills = nbt.getInt("enduranceSkills");
        s.magicSkills = nbt.getInt("miningSkills");
        s.miningSkills = nbt.getInt("magicSkills");

        if (s.strengthLevel < 1) s.strengthLevel = 1;
        if (s.agilityLevel < 1) s.agilityLevel = 1;
        if (s.enduranceLevel < 1) s.enduranceLevel = 1;
        if (s.magicLevel < 1) s.magicLevel = 1;
        if (s.miningLevel < 1) s.miningLevel = 1;
        return s;
    }
}