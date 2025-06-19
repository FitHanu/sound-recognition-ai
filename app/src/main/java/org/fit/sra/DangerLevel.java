package org.fit.sra;

/**
 * Enums represent differ Danger Levels from audio classification process
 */
public enum DangerLevel {

    NONE(0, "NONE"),
    LOW(1, "LOW"),
    MEDIUM(2, "MEDIUM"),
    HIGH(3, "HIGH");

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

    public static DangerLevel createFromStr(String value) {
        switch (value) {
            case "LOW":
                return LOW;
            case "MEDIUM":
                return MEDIUM;
            case "HIGH":
                return HIGH;
            default:
                return NONE;
        }
    }
}
