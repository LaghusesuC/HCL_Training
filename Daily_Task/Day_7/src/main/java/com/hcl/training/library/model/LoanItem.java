package com.hcl.training.library.model;

/**
 * Represents an active book checkout/loan record.
 * Used to demonstrate safe removal of overdue items using Iterator.remove().
 */
public class LoanItem {
    private final String loanId;
    private final String borrowerName;
    private final String bookTitle;
    private final int daysOverdue;

    public LoanItem(String loanId, String borrowerName, String bookTitle, int daysOverdue) {
        this.loanId = loanId;
        this.borrowerName = borrowerName;
        this.bookTitle = bookTitle;
        this.daysOverdue = daysOverdue;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getDaysOverdue() {
        return daysOverdue;
    }

    public boolean isOverdue() {
        return daysOverdue > 0;
    }

    @Override
    public String toString() {
        return String.format("Loan[%s: '%s' borrowed by %s, Overdue: %d days (%s)]",
                loanId, bookTitle, borrowerName, daysOverdue, isOverdue() ? "OVERDUE" : "ON TIME");
    }
}
