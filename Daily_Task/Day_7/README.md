# Day 7: Library Waitlist System & Collections Benchmark

## Overview

The **Library Waitlist System** is a Java 17 application demonstrating core components of the **Java Collections Framework (JCF)** and their performance trade-offs.

It provides a clean, hands-on implementation of:
1. **`ArrayList` Catalogue**: Dynamic array maintaining catalogue records with fast $O(1)$ indexed access.
2. **`LinkedHashSet` De-duplication**: Set implementation that eliminates duplicate user reservation requests while preserving insertion order.
3. **`ArrayDeque` Waitlist**: High-performance FIFO (First-In, First-Out) queue for standard book reservations.
4. **`PriorityQueue` Faculty-First Requests**: Heap-based priority queue that prioritizes requests by user hierarchy (`Faculty` > `Student` > `Guest`).
5. **`Iterator.remove()` Overdue Purge**: Safe in-place removal of overdue book loans avoiding `ConcurrentModificationException`.
6. **Performance Benchmark (`ArrayList` vs `LinkedList`)**: Empirical timing analysis comparing head insertions `add(0, e)`, tail insertions `add(e)`, and indexed random access `get(i)`.

---

## Architecture & Collections Mapping

| Requirement | Collection Used | Time Complexity | Why It Was Chosen |
|---|---|---|---|
| **Book Catalogue** | `ArrayList<Book>` | Get: $O(1)$, Append: Amortized $O(1)$ | Efficient array storage and constant-time index lookups `catalogue.get(i)`. |
| **Request De-duplication** | `LinkedHashSet<WaitlistRequest>` | Add / Contains: $O(1)$ | Prevents duplicate user requests for the same book while preserving original arrival order. |
| **Standard Waitlist** | `ArrayDeque<WaitlistRequest>` | Offer / Poll: $O(1)$ | Cache-friendly resizable array implementation of a FIFO queue without node allocation overhead. |
| **Faculty-First Priority** | `PriorityQueue<WaitlistRequest>` | Offer / Poll: $O(\log n)$, Peek: $O(1)$ | Min-heap ordering ensuring Faculty requests jump ahead of Students and Guests. |
| **Purge Overdue Loans** | `Iterator.remove()` on `List<LoanItem>` | Remove: $O(1)$ per element | Safely deletes items while iterating without causing `ConcurrentModificationException`. |
| **Performance Benchmark** | `ArrayList` vs `LinkedList` | Head Insert: $O(n)$ vs $O(1)$<br>Get: $O(1)$ vs $O(n)$ | Quantifies practical trade-offs between contiguous array memory and linked node references. |

---

## Project Structure

```text
Day_7/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── hcl/
    │               └── training/
    │                   └── library/
    │                       ├── Main.java
    │                       ├── model/
    │                       │   ├── Book.java
    │                       │   ├── LoanItem.java
    │                       │   ├── UserType.java
    │                       │   └── WaitlistRequest.java
    │                       └── service/
    │                           ├── LibraryManager.java
    │                           └── ListBenchmark.java
    └── test/
        └── java/
            └── com/
                └── hcl/
                    └── training/
                        └── library/
                            └── LibraryManagerTest.java
```

---

## Core Implementations

### 1. Request Deduplication (`LinkedHashSet`)
In `WaitlistRequest`, `equals()` and `hashCode()` are defined on `(userId, bookIsbn)`:
```java
// Duplicate requests by the same user for the same book are automatically rejected
boolean accepted = deduplicatedRequests.add(request);
```
`LinkedHashSet` ensures that the sequence of first arrival is strictly maintained for downstream queues.

### 2. Faculty-First Ordering (`PriorityQueue`)
`WaitlistRequest` implements `Comparable<WaitlistRequest>`:
```java
@Override
public int compareTo(WaitlistRequest other) {
    int priority = Integer.compare(this.userType.getPriority(), other.userType.getPriority());
    if (priority != 0) return priority;
    return Long.compare(this.timestamp, other.timestamp); // FIFO tie-breaker within tier
}
```
Faculty members (`priority = 1`) are served ahead of Students (`priority = 2`) and Guests (`priority = 3`).

### 3. Safe Removal with `Iterator.remove()`
Removing items directly inside a `for-each` loop triggers `ConcurrentModificationException` because the collection's internal modification count (`modCount`) goes out of sync with the cursor. `Iterator.remove()` updates `expectedModCount`:
```java
Iterator<LoanItem> iterator = activeLoans.iterator();
while (iterator.hasNext()) {
    LoanItem loan = iterator.next();
    if (loan.isOverdue()) {
        iterator.remove(); // Safe in-place removal
    }
}
```

### 4. Performance Benchmark: `ArrayList` vs `LinkedList`
| Benchmark Operation | ArrayList | LinkedList | Winner & Explanation |
|---|---|---|---|
| **Head Insert `add(0, e)`** | $O(n)$ | $O(1)$ | **`LinkedList` wins**: Array needs shifting all $n$ elements right, whereas `LinkedList` only updates head pointer. |
| **Tail Append `add(e)`** | Amortized $O(1)$ | $O(1)$ | **`ArrayList` wins**: Contiguous memory and cache locality outperform per-element node object allocations. |
| **Indexed Access `get(i)`** | $O(1)$ | $O(n)$ | **`ArrayList` wins by a massive margin**: Direct memory offset calculation `data[i]` vs linear node pointer traversal. |

---

## How to Build & Run

### Prerequisites
- Java 17+
- Apache Maven 3.8+

### 1. Compile the Project
```bash
mvn clean compile
```

### 2. Run the Unit Tests
```bash
mvn test
```

### 3. Execute the Main Application
```bash
mvn exec:java
```

---

## Sample Execution Output

```text
==========================================================================
            HCL TRAINING - DAY 7: LIBRARY WAITLIST SYSTEM                
==========================================================================

--- 1. BOOK CATALOGUE (ArrayList) ---
Total books in catalogue: 3
  Index 0 -> Book[ISBN=978-0134685991, Title='Effective Java', Author='Joshua Bloch']
  Index 1 -> Book[ISBN=978-0596009205, Title='Head First Design Patterns', Author='Eric Freeman']
  Index 2 -> Book[ISBN=978-0132350884, Title='Clean Code', Author='Robert C. Martin']

--- 2. WAITLIST REQUEST DEDUPLICATION (LinkedHashSet) ---
Submitting requests:
  [ACCEPTED] REQ-01 by Alice Smith
  [ACCEPTED] REQ-02 by Dr. Bob Vance
  [DUPLICATE REJECTED] REQ-03 by Alice Smith for book 'Effective Java'
  [ACCEPTED] REQ-04 by Charlie Brown
  [ACCEPTED] REQ-05 by Prof. Diane

Unique requests retained in insertion order (4 total):
  [REQ-01 | Alice Smith (Student) -> 'Effective Java' (ISBN: 978-0134685991)]
  [REQ-02 | Dr. Bob Vance (Faculty Member) -> 'Effective Java' (ISBN: 978-0134685991)]
  [REQ-04 | Charlie Brown (Guest Member) -> 'Effective Java' (ISBN: 978-0134685991)]
  [REQ-05 | Prof. Diane (Faculty Member) -> 'Effective Java' (ISBN: 978-0134685991)]

--- 3. STANDARD WAITLIST (ArrayDeque - First Come, First Served) ---
Processing requests in FIFO order from ArrayDeque (4 in queue):
  Serving standard request -> [REQ-01 | Alice Smith (Student) -> 'Effective Java' (ISBN: 978-0134685991)]
  Serving standard request -> [REQ-02 | Dr. Bob Vance (Faculty Member) -> 'Effective Java' (ISBN: 978-0134685991)]
  Serving standard request -> [REQ-04 | Charlie Brown (Guest Member) -> 'Effective Java' (ISBN: 978-0134685991)]
  Serving standard request -> [REQ-05 | Prof. Diane (Faculty Member) -> 'Effective Java' (ISBN: 978-0134685991)]

--- 4. PRIORITY WAITLIST (PriorityQueue - Faculty First) ---
Processing requests with Faculty Priority from PriorityQueue (4 in queue):
  Serving prioritized request -> [REQ-02 | Dr. Bob Vance (Faculty Member) -> 'Effective Java' (ISBN: 978-0134685991)]
  Serving prioritized request -> [REQ-05 | Prof. Diane (Faculty Member) -> 'Effective Java' (ISBN: 978-0134685991)]
  Serving prioritized request -> [REQ-01 | Alice Smith (Student) -> 'Effective Java' (ISBN: 978-0134685991)]
  Serving prioritized request -> [REQ-04 | Charlie Brown (Guest Member) -> 'Effective Java' (ISBN: 978-0134685991)]

--- 5. PURGING OVERDUE ITEMS (Iterator.remove) ---
Current active loans before purge:
  Loan[L-001: 'Effective Java' borrowed by Alice, Overdue: 0 days (ON TIME)]
  Loan[L-002: 'Clean Code' borrowed by Bob, Overdue: 5 days (OVERDUE)]
  Loan[L-003: 'Design Patterns' borrowed by Charlie, Overdue: 0 days (ON TIME)]
  Loan[L-004: 'Java Concurrency' borrowed by David, Overdue: 12 days (OVERDUE)]
  Loan[L-005: 'Refactoring' borrowed by Emma, Overdue: 0 days (ON TIME)]

Executing purge of overdue items using Iterator.remove()...
Removed 2 overdue loans:
  [PURGED] Loan[L-002: 'Clean Code' borrowed by Bob, Overdue: 5 days (OVERDUE)]
  [PURGED] Loan[L-004: 'Java Concurrency' borrowed by David, Overdue: 12 days (OVERDUE)]

Remaining active loans in library:
  [ACTIVE] Loan[L-001: 'Effective Java' borrowed by Alice, Overdue: 0 days (ON TIME)]
  [ACTIVE] Loan[L-003: 'Design Patterns' borrowed by Charlie, Overdue: 0 days (ON TIME)]
  [ACTIVE] Loan[L-005: 'Refactoring' borrowed by Emma, Overdue: 0 days (ON TIME)]

==========================================================================
   PERFORMANCE BENCHMARK: ArrayList vs LinkedList
==========================================================================
Test 1: Inserting 30000 elements at HEAD: add(0, e)
  - ArrayList.add(0, e)  :  134 ms (O(n) due to array shifting)
  - LinkedList.add(0, e) :    3 ms (O(1) pointer updates at head)
  => Winner for Head Insert: LinkedList

Test 2: Appending 100000 elements at TAIL: add(e)
  - ArrayList.add(e)     :    7 ms (Amortized O(1), contiguous memory)
  - LinkedList.add(e)    :   15 ms (O(1) with Node object allocations)
  => Winner for Tail Append: ArrayList

Test 3: Indexed Access: 30000 calls to get(i) across list of size 30000
  - ArrayList.get(i)     :    1 ms (O(1) direct offset indexing)
  - LinkedList.get(i)    :  420 ms (O(n) sequential node traversal)
  => Winner for get(i): ArrayList (Massive Advantage!)

Summary:
  * Use ArrayList for reading/searching by index and general purpose lists.
  * Use LinkedList only when frequent insertions/removals occur at head/tail without index lookups.
==========================================================================

Day 7 Library Waitlist demonstration finished successfully!
```
