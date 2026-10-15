package com.hcl.training.generics.util;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Generic Utility Toolkit demonstrating:
 * 1. Bounded Type Parameters (<T extends Comparable<T>>)
 * 2. Wildcards (Upper-Bounded, Lower-Bounded, Unbounded)
 * 3. PECS Principle (Producer Extends, Consumer Super)
 */
public final class GenericToolkit {

    private GenericToolkit() {
        // Utility class - prevent instantiation
    }

    /**
     * Finds the maximum element in a collection of Comparable elements.
     * Bounded type parameter: T must implement Comparable<T>.
     *
     * @param <T>   the element type, which must be mutually comparable
     * @param items collection of items
     * @return the maximum element according to its natural ordering
     * @throws NoSuchElementException if items is null or empty
     */
    public static <T extends Comparable<T>> T max(Collection<T> items) {
        if (items == null || items.isEmpty()) {
            throw new NoSuchElementException("Cannot find max of null or empty collection");
        }

        T maxElement = null;
        for (T item : items) {
            if (item == null) continue;
            if (maxElement == null || item.compareTo(maxElement) > 0) {
                maxElement = item;
            }
        }

        if (maxElement == null) {
            throw new NoSuchElementException("Collection contains only null elements");
        }
        return maxElement;
    }

    /**
     * Overloaded max method for varargs / array arguments.
     *
     * @param <T>   the element type
     * @param items array or varargs of comparable items
     * @return the maximum element
     */
    @SafeVarargs
    public static <T extends Comparable<T>> T max(T... items) {
        if (items == null || items.length == 0) {
            throw new NoSuchElementException("Cannot find max of null or empty array");
        }
        T maxElement = null;
        for (T item : items) {
            if (item == null) continue;
            if (maxElement == null || item.compareTo(maxElement) > 0) {
                maxElement = item;
            }
        }
        if (maxElement == null) {
            throw new NoSuchElementException("Array contains only null elements");
        }
        return maxElement;
    }

    /**
     * Upper-Bounded Wildcard Demonstration:
     * List<? extends Number> allows any list whose element type is Number or a subclass of Number
     * (e.g. List<Integer>, List<Double>, List<Float>).
     * PECS: This list acts as a PRODUCER (we only read Number values from it).
     *
     * @param numbers list of numbers (Integer, Double, Long, etc.)
     * @return sum of all numbers as a double
     */
    public static double sumOfNumbers(List<? extends Number> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Number number : numbers) {
            if (number != null) {
                sum += number.doubleValue();
            }
        }
        return sum;
    }

    /**
     * Lower-Bounded Wildcard Demonstration:
     * List<? super Integer> allows any list whose element type is Integer or a superclass of Integer
     * (e.g. List<Integer>, List<Number>, List<Object>).
     * PECS: This list acts as a CONSUMER (we write Integer objects into it).
     *
     * @param list  the destination list
     * @param start start integer (inclusive)
     * @param end   end integer (inclusive)
     */
    public static void addIntegers(List<? super Integer> list, int start, int end) {
        if (list == null) return;
        for (int i = start; i <= end; i++) {
            list.add(i);
        }
    }

    /**
     * Classic PECS (Producer Extends, Consumer Super) demonstration:
     * Copies elements from a source list into a destination list.
     * - `src` is a Producer: we read elements of type T from it (<? extends T>)
     * - `dest` is a Consumer: we write elements of type T into it (<? super T>)
     *
     * @param <T>  the common element type
     * @param src  the producer source list
     * @param dest the consumer destination list
     */
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        if (src == null || dest == null) return;
        for (T item : src) {
            dest.add(item);
        }
    }

    /**
     * Unbounded Wildcard Demonstration:
     * List<?> can match any list type (e.g. List<String>, List<Integer>, List<Product>).
     * Useful when the method only performs operations independent of the type parameter
     * (e.g. size inspection, printing, element counting).
     *
     * @param list any list of unknown type
     */
    public static void printList(List<?> list) {
        if (list == null) {
            System.out.println("  [null]");
            return;
        }
        System.out.println("  [List size=" + list.size() + "]");
        for (Object item : list) {
            System.out.println("    - " + item);
        }
    }
}
