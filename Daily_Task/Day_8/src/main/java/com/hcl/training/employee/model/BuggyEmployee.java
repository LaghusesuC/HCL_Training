package com.hcl.training.employee.model;

import java.util.Objects;

/**
 * Demonstrates the classic Java bug:
 * Overrides equals() but forgets to override hashCode()!
 *
 * Consequence:
 * Two distinct object instances with identical field values are logically equal according to equals(),
 * but produce DIFFERENT hash codes (derived from Object.hashCode() / memory identity).
 * When used as keys in a HashMap or elements in a HashSet, bucket indexing fails,
 * causing map.get() to return null even when the key exists!
 */
public class BuggyEmployee {
    private final int id;
    private final String name;

    public BuggyEmployee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Overrides equals() correctly based on logical fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuggyEmployee that = (BuggyEmployee) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    // BUG: Missing hashCode() override!
    // Inherits java.lang.Object's native identity hash code.

    @Override
    public String toString() {
        return "BuggyEmployee[id=" + id + ", name='" + name + "', identityHashCode=" + System.identityHashCode(this) + "]";
    }
}
