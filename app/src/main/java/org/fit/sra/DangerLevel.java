package org.fit.sra;

/**
 * Enums represent differ Danger Levels from audio classification process
 */
public enum DangerLevel {

    NONE(0, "none"),
    LOW(1, "low"),
    MEDIUM(2, "medium"),
    HIGH(3, "high");

    private final int value;
    private final String displayName;

    DangerLevel(int level, String displayName) {
        this.value = level;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}
