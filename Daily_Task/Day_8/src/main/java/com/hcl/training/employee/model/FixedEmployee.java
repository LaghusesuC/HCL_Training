package com.hcl.training.employee.model;

import java.util.Objects;

/**
 * Fixed version of BuggyEmployee:
 * Properly overrides both equals() and hashCode(), honoring the fundamental Java contract:
 * "If two objects are equal according to equals(), they MUST have the same hashCode()."
 */
public class FixedEmployee {
    private final int id;
    private final String name;

    public FixedEmployee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedEmployee that = (FixedEmployee) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    // FIX: Properly compute hashCode based on identical fields used in equals()
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "FixedEmployee[id=" + id + ", name='" + name + "', hashCode=" + hashCode() + "]";
    }
}
