package com.example.models;

/**
 * Task flags for process indicators and team coordination
 */
public enum TaskFlag {
    // Default/No flag
    NONE("None"),

    // Process Indicators
    NEEDS_INPUT("Needs Input"),
    DECISION_REQUIRED("Decision Required"),
    EXPERIMENTAL("Experimental"),
    TECHNICAL_DEBT("Technical Debt"),

    // Team Coordination
    HELP_WANTED("Help Wanted"),
    PAIRING_RECOMMENDED("Pairing Recommended"),
    GOOD_FIRST_TASK("Good First Task"),
    KNOWLEDGE_SHARING("Knowledge Sharing");

    private final String displayName;

    TaskFlag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


    public static TaskFlag fromString(String flagName) {
        if (flagName == null || flagName.trim().isEmpty()) {
            return NONE;
        }

        try {
            return TaskFlag.valueOf(flagName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}