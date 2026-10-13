package com.hcl.training.library.model;

import java.util.Objects;

/**
 * Represents a user's reservation request for a book.
 * Implements Comparable to prioritize Faculty requests first in PriorityQueue,
 * and uses userId + bookIsbn for de-duplication in LinkedHashSet.
 */
public class WaitlistRequest implements Comparable<WaitlistRequest> {
    private final String requestId;
    private final String userId;
    private final String userName;
    private final UserType userType;
    private final String bookIsbn;
    private final String bookTitle;
    private final long timestamp;

    public WaitlistRequest(String requestId, String userId, String userName,
                           UserType userType, String bookIsbn, String bookTitle, long timestamp) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * PriorityQueue ordering:
     * 1. Higher priority user type (e.g., Faculty before Student, Student before Guest).
     * 2. Earlier request timestamp if user types are equal (FIFO within same tier).
     */
    @Override
    public int compareTo(WaitlistRequest other) {
        int priorityComparison = Integer.compare(this.userType.getPriority(), other.userType.getPriority());
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return Long.compare(this.timestamp, other.timestamp);
    }

    /**
     * Deduplication: A user cannot place duplicate waitlist requests for the same book.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitlistRequest that = (WaitlistRequest) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(bookIsbn, that.bookIsbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookIsbn);
    }

    @Override
    public String toString() {
        return String.format("[%s | %s (%s) -> '%s' (ISBN: %s)]",
                requestId, userName, userType.getDisplayName(), bookTitle, bookIsbn);
    }
}
