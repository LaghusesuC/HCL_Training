package com.hcl.training.library.service;

import com.hcl.training.library.model.Book;
import com.hcl.training.library.model.LoanItem;
import com.hcl.training.library.model.WaitlistRequest;

import java.util.*;

/**
 * Service managing library catalogue, waitlists, deduplication, and loan purging.
 * Demonstrates:
 * 1. ArrayList: Catalogue storage with indexed random access.
 * 2. LinkedHashSet: Insertion-ordered deduplication of waitlist requests.
 * 3. ArrayDeque: Standard FIFO waitlist processing.
 * 4. PriorityQueue: Faculty-first priority waitlist queue.
 * 5. Iterator.remove(): Safe in-place removal of overdue loan items.
 */
public class LibraryManager {

    // 1. ArrayList catalogue
    private final List<Book> catalogue = new ArrayList<>();

    // 2. LinkedHashSet for deduplicating incoming requests (preserves arrival order)
    private final Set<WaitlistRequest> deduplicatedRequests = new LinkedHashSet<>();

    // 3. ArrayDeque for standard FIFO waitlist
    private final Deque<WaitlistRequest> standardWaitlist = new ArrayDeque<>();

    // 4. PriorityQueue for faculty-first waitlist requests
    private final PriorityQueue<WaitlistRequest> facultyFirstQueue = new PriorityQueue<>();

    // 5. Active loans list for demonstrating Iterator.remove
    private final List<LoanItem> activeLoans = new ArrayList<>();

    // ==========================================
    // 1. ArrayList Catalogue Operations
    // ==========================================

    public void addBook(Book book) {
        catalogue.add(book);
    }

    public Book getBookByIndex(int index) {
        if (index < 0 || index >= catalogue.size()) {
            throw new IndexOutOfBoundsException("Invalid catalogue index: " + index);
        }
        return catalogue.get(index);
    }

    public List<Book> getCatalogue() {
        return Collections.unmodifiableList(catalogue);
    }

    public int getCatalogueSize() {
        return catalogue.size();
    }

    // ==========================================
    // 2. LinkedHashSet Deduplication Operations
    // ==========================================

    /**
     * Attempts to register a waitlist request.
     * Rejects duplicate requests (same user and book) while maintaining arrival order.
     * @return true if accepted as new, false if rejected as duplicate
     */
    public boolean registerRequest(WaitlistRequest request) {
        return deduplicatedRequests.add(request);
    }

    public Set<WaitlistRequest> getDeduplicatedRequests() {
        return Collections.unmodifiableSet(deduplicatedRequests);
    }

    // ==========================================
    // 3. ArrayDeque Standard FIFO Waitlist
    // ==========================================

    public void enqueueStandardWaitlist(WaitlistRequest request) {
        standardWaitlist.offer(request);
    }

    public WaitlistRequest processNextStandardRequest() {
        return standardWaitlist.poll();
    }

    public WaitlistRequest peekStandardWaitlist() {
        return standardWaitlist.peek();
    }

    public Deque<WaitlistRequest> getStandardWaitlist() {
        return new ArrayDeque<>(standardWaitlist);
    }

    public int getStandardWaitlistSize() {
        return standardWaitlist.size();
    }

    // ==========================================
    // 4. PriorityQueue Faculty-First Waitlist
    // ==========================================

    public void enqueueFacultyFirst(WaitlistRequest request) {
        facultyFirstQueue.offer(request);
    }

    public WaitlistRequest processNextFacultyFirstRequest() {
        return facultyFirstQueue.poll();
    }

    public WaitlistRequest peekFacultyFirst() {
        return facultyFirstQueue.peek();
    }

    public int getFacultyFirstQueueSize() {
        return facultyFirstQueue.size();
    }

    // ==========================================
    // 5. Active Loans & Iterator.remove()
    // ==========================================

    public void addLoan(LoanItem loan) {
        activeLoans.add(loan);
    }

    public List<LoanItem> getActiveLoans() {
        return Collections.unmodifiableList(activeLoans);
    }

    /**
     * Purges overdue loans safely using Iterator.remove().
     * Direct removal inside a standard for-each loop causes ConcurrentModificationException.
     * Iterator.remove() avoids this by safely updating collection state.
     * @return List of removed overdue items
     */
    public List<LoanItem> purgeOverdueItems() {
        List<LoanItem> purged = new ArrayList<>();
        Iterator<LoanItem> iterator = activeLoans.iterator();

        while (iterator.hasNext()) {
            LoanItem loan = iterator.next();
            if (loan.isOverdue()) {
                purged.add(loan);
                iterator.remove(); // Safe removal during iteration
            }
        }
        return purged;
    }
}
