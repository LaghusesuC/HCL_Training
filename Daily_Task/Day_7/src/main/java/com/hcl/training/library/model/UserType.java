package com.hcl.training.library.model;

/**
 * Represents the category of library user with associated priority ranking.
 * Lower priority number indicates higher service priority.
 */
public enum UserType {
    FACULTY(1, "Faculty Member"),
    STUDENT(2, "Student"),
    GUEST(3, "Guest Member");

    private final int priority;
    private final String displayName;

    UserType(int priority, String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }

    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }
}
