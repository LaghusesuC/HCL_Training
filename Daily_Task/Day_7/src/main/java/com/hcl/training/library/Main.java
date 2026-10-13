package com.hcl.training.library;

import com.hcl.training.library.model.Book;
import com.hcl.training.library.model.LoanItem;
import com.hcl.training.library.model.UserType;
import com.hcl.training.library.model.WaitlistRequest;
import com.hcl.training.library.service.LibraryManager;
import com.hcl.training.library.service.ListBenchmark;

import java.util.List;
import java.util.Set;

/**
 * Main application demonstrating Day 7 Java Collections Framework features:
 * 1. ArrayList catalogue
 * 2. LinkedHashSet de-dup
 * 3. ArrayDeque waitlist
 * 4. PriorityQueue for faculty-first requests
 * 5. Iterator.remove for overdue items
 * 6. Benchmark: ArrayList vs LinkedList inserts and get(i)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("            HCL TRAINING - DAY 7: LIBRARY WAITLIST SYSTEM                ");
        System.out.println("==========================================================================\n");

        LibraryManager library = new LibraryManager();

        // ---------------------------------------------------------
        // Feature 1: ArrayList Catalogue
        // ---------------------------------------------------------
        System.out.println("--- 1. BOOK CATALOGUE (ArrayList) ---");
        library.addBook(new Book("978-0134685991", "Effective Java", "Joshua Bloch"));
        library.addBook(new Book("978-0596009205", "Head First Design Patterns", "Eric Freeman"));
        library.addBook(new Book("978-0132350884", "Clean Code", "Robert C. Martin"));

        System.out.printf("Total books in catalogue: %d%n", library.getCatalogueSize());
        for (int i = 0; i < library.getCatalogueSize(); i++) {
            System.out.printf("  Index %d -> %s%n", i, library.getBookByIndex(i));
        }
        System.out.println();

        // ---------------------------------------------------------
        // Feature 2: LinkedHashSet Deduplication
        // ---------------------------------------------------------
        System.out.println("--- 2. WAITLIST REQUEST DEDUPLICATION (LinkedHashSet) ---");
        long now = System.currentTimeMillis();

        WaitlistRequest req1 = new WaitlistRequest("REQ-01", "U101", "Alice Smith", UserType.STUDENT,
                "978-0134685991", "Effective Java", now);
        WaitlistRequest req2 = new WaitlistRequest("REQ-02", "U102", "Dr. Bob Vance", UserType.FACULTY,
                "978-0134685991", "Effective Java", now + 10);
        WaitlistRequest req3Duplicate = new WaitlistRequest("REQ-03", "U101", "Alice Smith", UserType.STUDENT,
                "978-0134685991", "Effective Java", now + 20); // Duplicate! Same user + same book
        WaitlistRequest req4 = new WaitlistRequest("REQ-04", "U103", "Charlie Brown", UserType.GUEST,
                "978-0134685991", "Effective Java", now + 30);
        WaitlistRequest req5 = new WaitlistRequest("REQ-05", "U104", "Prof. Diane", UserType.FACULTY,
                "978-0134685991", "Effective Java", now + 40);

        System.out.println("Submitting requests:");
        submitRequest(library, req1);
        submitRequest(library, req2);
        submitRequest(library, req3Duplicate); // Should be rejected by LinkedHashSet
        submitRequest(library, req4);
        submitRequest(library, req5);

        Set<WaitlistRequest> deduped = library.getDeduplicatedRequests();
        System.out.printf("%nUnique requests retained in insertion order (%d total):%n", deduped.size());
        deduped.forEach(r -> System.out.println("  " + r));
        System.out.println();

        // ---------------------------------------------------------
        // Feature 3: ArrayDeque Waitlist (FIFO)
        // ---------------------------------------------------------
        System.out.println("--- 3. STANDARD WAITLIST (ArrayDeque - First Come, First Served) ---");
        for (WaitlistRequest r : deduped) {
            library.enqueueStandardWaitlist(r);
        }

        System.out.printf("Processing requests in FIFO order from ArrayDeque (%d in queue):%n",
                library.getStandardWaitlistSize());
        while (library.getStandardWaitlistSize() > 0) {
            WaitlistRequest served = library.processNextStandardRequest();
            System.out.println("  Serving standard request -> " + served);
        }
        System.out.println();

        // ---------------------------------------------------------
        // Feature 4: PriorityQueue Faculty-First Requests
        // ---------------------------------------------------------
        System.out.println("--- 4. PRIORITY WAITLIST (PriorityQueue - Faculty First) ---");
        for (WaitlistRequest r : deduped) {
            library.enqueueFacultyFirst(r);
        }

        System.out.printf("Processing requests with Faculty Priority from PriorityQueue (%d in queue):%n",
                library.getFacultyFirstQueueSize());
        while (library.getFacultyFirstQueueSize() > 0) {
            WaitlistRequest served = library.processNextFacultyFirstRequest();
            System.out.println("  Serving prioritized request -> " + served);
        }
        System.out.println();

        // ---------------------------------------------------------
        // Feature 5: Iterator.remove for Overdue Items
        // ---------------------------------------------------------
        System.out.println("--- 5. PURGING OVERDUE ITEMS (Iterator.remove) ---");
        library.addLoan(new LoanItem("L-001", "Alice", "Effective Java", 0));
        library.addLoan(new LoanItem("L-002", "Bob", "Clean Code", 5));       // Overdue
        library.addLoan(new LoanItem("L-003", "Charlie", "Design Patterns", 0));
        library.addLoan(new LoanItem("L-004", "David", "Java Concurrency", 12)); // Overdue
        library.addLoan(new LoanItem("L-005", "Emma", "Refactoring", 0));

        System.out.println("Current active loans before purge:");
        library.getActiveLoans().forEach(l -> System.out.println("  " + l));

        System.out.println("\nExecuting purge of overdue items using Iterator.remove()...");
        List<LoanItem> purged = library.purgeOverdueItems();
        System.out.printf("Removed %d overdue loans:%n", purged.size());
        purged.forEach(p -> System.out.println("  [PURGED] " + p));

        System.out.println("\nRemaining active loans in library:");
        library.getActiveLoans().forEach(l -> System.out.println("  [ACTIVE] " + l));
        System.out.println();

        // ---------------------------------------------------------
        // Feature 6: Performance Benchmark: ArrayList vs LinkedList
        // ---------------------------------------------------------
        ListBenchmark.runBenchmark();

        System.out.println("Day 7 Library Waitlist demonstration finished successfully!");
    }

    private static void submitRequest(LibraryManager library, WaitlistRequest request) {
        boolean accepted = library.registerRequest(request);
        if (accepted) {
            System.out.printf("  [ACCEPTED] %s by %s%n", request.getRequestId(), request.getUserName());
        } else {
            System.out.printf("  [DUPLICATE REJECTED] %s by %s for book '%s'%n",
                    request.getRequestId(), request.getUserName(), request.getBookTitle());
        }
    }
}
