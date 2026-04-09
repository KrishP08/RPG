package org.rpg.stats;

public enum StatType {
    STRENGTH("Strength"),
    AGILITY("Agility"),
    ENDURANCE("Endurance"),
    MINING("Mining"),
    MAGIC("Magic");

    private final String displayName;

    StatType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    public byte toByte() {
        return (byte) this.ordinal();
    }

    public static StatType fromByte(byte b) {
        return values()[b];
    }
}
