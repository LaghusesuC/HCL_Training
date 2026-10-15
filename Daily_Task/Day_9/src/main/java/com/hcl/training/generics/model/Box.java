package com.hcl.training.generics.model;

import java.util.Objects;

/**
 * A generic bounded container restricted to numeric types (subclasses of {@link Number}).
 *
 * @param <T> a type that extends Number (e.g. Integer, Double, Long, Float)
 */
public class Box<T extends Number> {
    private T item;

    public Box() {
    }

    public Box(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    /**
     * Extracts the primitive double value of the contained number.
     *
     * @return numeric double value, or 0.0 if empty
     */
    public double doubleValue() {
        return item != null ? item.doubleValue() : 0.0;
    }

    /**
     * Compares this box's numeric value with another box holding any Number subtype.
     * Uses wildcard `? extends Number`.
     *
     * @param other another numeric box
     * @return true if this box's value is strictly greater than the other box's value
     */
    public boolean isGreaterThan(Box<? extends Number> other) {
        if (other == null || other.getItem() == null) {
            return this.item != null;
        }
        if (this.item == null) {
            return false;
        }
        return this.doubleValue() > other.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box<?> box = (Box<?>) o;
        return Objects.equals(item, box.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "Box[" + (item != null ? item : "empty") + "]";
    }
}
