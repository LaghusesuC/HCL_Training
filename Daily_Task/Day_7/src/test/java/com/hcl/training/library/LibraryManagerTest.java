package com.hcl.training.library;

import com.hcl.training.library.model.Book;
import com.hcl.training.library.model.LoanItem;
import com.hcl.training.library.model.UserType;
import com.hcl.training.library.model.WaitlistRequest;
import com.hcl.training.library.service.LibraryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LibraryManagerTest {

    private LibraryManager library;

    @BeforeEach
    void setUp() {
        library = new LibraryManager();
    }

    @Test
    @DisplayName("ArrayList catalogue stores books and supports indexed access")
    void testCatalogueArrayList() {
        Book b1 = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        Book b2 = new Book("978-0132350884", "Clean Code", "Robert C. Martin");

        library.addBook(b1);
        library.addBook(b2);

        assertEquals(2, library.getCatalogueSize());
        assertEquals("Effective Java", library.getBookByIndex(0).getTitle());
        assertEquals("Clean Code", library.getBookByIndex(1).getTitle());
    }

    @Test
    @DisplayName("LinkedHashSet de-duplicates waitlist requests while preserving arrival order")
    void testLinkedHashSetDeduplication() {
        long t = System.currentTimeMillis();
        WaitlistRequest req1 = new WaitlistRequest("R1", "U1", "Alice", UserType.STUDENT, "ISBN-1", "Book 1", t);
        WaitlistRequest req2 = new WaitlistRequest("R2", "U2", "Bob", UserType.FACULTY, "ISBN-1", "Book 1", t + 1);
        WaitlistRequest req3Duplicate = new WaitlistRequest("R3", "U1", "Alice", UserType.STUDENT, "ISBN-1", "Book 1", t + 2);
        WaitlistRequest req4 = new WaitlistRequest("R4", "U3", "Charlie", UserType.GUEST, "ISBN-1", "Book 1", t + 3);

        assertTrue(library.registerRequest(req1));
        assertTrue(library.registerRequest(req2));
        assertFalse(library.registerRequest(req3Duplicate), "Duplicate request from same user for same book must be rejected");
        assertTrue(library.registerRequest(req4));

        Set<WaitlistRequest> deduped = library.getDeduplicatedRequests();
        assertEquals(3, deduped.size());

        // Verify insertion order is preserved
        List<WaitlistRequest> ordered = new ArrayList<>(deduped);
        assertEquals("R1", ordered.get(0).getRequestId());
        assertEquals("R2", ordered.get(1).getRequestId());
        assertEquals("R4", ordered.get(2).getRequestId());
    }

    @Test
    @DisplayName("ArrayDeque waitlist processes requests in FIFO order")
    void testArrayDequeWaitlist() {
        long t = System.currentTimeMillis();
        WaitlistRequest req1 = new WaitlistRequest("R1", "U1", "Alice", UserType.STUDENT, "ISBN-1", "Book 1", t);
        WaitlistRequest req2 = new WaitlistRequest("R2", "U2", "Bob", UserType.FACULTY, "ISBN-1", "Book 1", t + 1);

        library.enqueueStandardWaitlist(req1);
        library.enqueueStandardWaitlist(req2);

        assertEquals(2, library.getStandardWaitlistSize());
        assertEquals(req1, library.processNextStandardRequest(), "FIFO must serve req1 first");
        assertEquals(req2, library.processNextStandardRequest(), "FIFO must serve req2 second");
        assertEquals(0, library.getStandardWaitlistSize());
    }

    @Test
    @DisplayName("PriorityQueue serves Faculty requests first, regardless of arrival order")
    void testPriorityQueueFacultyFirst() {
        long t = System.currentTimeMillis();
        WaitlistRequest studentReq = new WaitlistRequest("R1", "U1", "Student Alice", UserType.STUDENT, "ISBN-1", "Book 1", t);
        WaitlistRequest facultyReq = new WaitlistRequest("R2", "U2", "Prof. Bob", UserType.FACULTY, "ISBN-1", "Book 1", t + 10);
        WaitlistRequest guestReq = new WaitlistRequest("R3", "U3", "Guest Charlie", UserType.GUEST, "ISBN-1", "Book 1", t - 10);

        // Enqueue guest, student, then faculty
        library.enqueueFacultyFirst(studentReq);
        library.enqueueFacultyFirst(guestReq);
        library.enqueueFacultyFirst(facultyReq);

        // Faculty must come first
        WaitlistRequest first = library.processNextFacultyFirstRequest();
        assertEquals(UserType.FACULTY, first.getUserType());
        assertEquals("Prof. Bob", first.getUserName());

        // Student must come second
        WaitlistRequest second = library.processNextFacultyFirstRequest();
        assertEquals(UserType.STUDENT, second.getUserType());
        assertEquals("Student Alice", second.getUserName());

        // Guest must come third
        WaitlistRequest third = library.processNextFacultyFirstRequest();
        assertEquals(UserType.GUEST, third.getUserType());
        assertEquals("Guest Charlie", third.getUserName());
    }

    @Test
    @DisplayName("Iterator.remove safely purges overdue loan items without ConcurrentModificationException")
    void testPurgeOverdueItemsWithIterator() {
        library.addLoan(new LoanItem("L1", "User A", "Book A", 0)); // Not overdue
        library.addLoan(new LoanItem("L2", "User B", "Book B", 5)); // Overdue
        library.addLoan(new LoanItem("L3", "User C", "Book C", 0)); // Not overdue
        library.addLoan(new LoanItem("L4", "User D", "Book D", 2)); // Overdue

        List<LoanItem> purged = library.purgeOverdueItems();

        assertEquals(2, purged.size());
        assertEquals(2, library.getActiveLoans().size());
        assertTrue(library.getActiveLoans().stream().noneMatch(LoanItem::isOverdue));
    }
}
