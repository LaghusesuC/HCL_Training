package com.hcl.training.generics.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom AutoCloseable implementation demonstrating resource lifecycle and close order.
 *
 * In Java's try-with-resources:
 * - Resources are initialized from left to right (in order of declaration).
 * - Resources are closed in REVERSE order (Last-In, First-Out / LIFO) upon exiting the try block.
 * - This prevents resource dependency leaks (e.g., an outer wrapper stream closing before its inner stream).
 */
public class CustomResource implements AutoCloseable {
    private final String name;
    private boolean closed = false;

    // Static event tracker for auditing and unit test verification
    private static final List<String> CLOSE_HISTORY = new ArrayList<>();

    public CustomResource(String name) {
        this.name = name;
        System.out.printf("  [INIT]  Opened resource : %s%n", name);
    }

    public void doWork() {
        if (closed) {
            throw new IllegalStateException("Cannot do work: resource " + name + " is already closed!");
        }
        System.out.printf("  [WORK]  Operating with  : %s%n", name);
    }

    public String getName() {
        return name;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            CLOSE_HISTORY.add(name);
            System.out.printf("  [CLOSE] Closed resource : %s (LIFO reverse order)%n", name);
        }
    }

    /**
     * Returns an unmodifiable view of resources closed so far.
     */
    public static List<String> getCloseHistory() {
        return Collections.unmodifiableList(CLOSE_HISTORY);
    }

    /**
     * Resets close history for testing.
     */
    public static void clearCloseHistory() {
        CLOSE_HISTORY.clear();
    }
}
