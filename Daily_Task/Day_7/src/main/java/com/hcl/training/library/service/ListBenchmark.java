package com.hcl.training.library.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility to benchmark and compare performance of ArrayList vs LinkedList:
 * 1. Insertion at head: add(0, item)
 * 2. Append at tail: add(item)
 * 3. Indexed access: get(i)
 */
public class ListBenchmark {

    public static class BenchmarkResult {
        public final String operation;
        public final long arrayListTimeMs;
        public final long linkedListTimeMs;
        public final String winner;

        public BenchmarkResult(String operation, long arrayListTimeMs, long linkedListTimeMs, String winner) {
            this.operation = operation;
            this.arrayListTimeMs = arrayListTimeMs;
            this.linkedListTimeMs = linkedListTimeMs;
            this.winner = winner;
        }
    }

    /**
     * Executes the benchmarks and prints formatted results to console.
     */
    public static void runBenchmark() {
        final int INSERT_HEAD_COUNT = 30_000;
        final int INSERT_TAIL_COUNT = 100_000;
        final int GET_ACCESS_COUNT = 30_000;

        System.out.println("==========================================================================");
        System.out.println("   PERFORMANCE BENCHMARK: ArrayList vs LinkedList");
        System.out.println("==========================================================================");

        // ---------------------------------------------------------
        // 1. Insertion at Head: add(0, element)
        // ---------------------------------------------------------
        System.out.printf("Test 1: Inserting %d elements at HEAD: add(0, e)%n", INSERT_HEAD_COUNT);

        List<Integer> arrayListHead = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < INSERT_HEAD_COUNT; i++) {
            arrayListHead.add(0, i);
        }
        long arrayListHeadTime = (System.nanoTime() - start) / 1_000_000;

        List<Integer> linkedListHead = new LinkedList<>();
        start = System.nanoTime();
        for (int i = 0; i < INSERT_HEAD_COUNT; i++) {
            linkedListHead.add(0, i);
        }
        long linkedListHeadTime = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  - ArrayList.add(0, e)  : %4d ms (O(n) due to array shifting)%n", arrayListHeadTime);
        System.out.printf("  - LinkedList.add(0, e) : %4d ms (O(1) pointer updates at head)%n", linkedListHeadTime);
        System.out.printf("  => Winner for Head Insert: %s%n%n", (linkedListHeadTime < arrayListHeadTime ? "LinkedList" : "ArrayList"));

        // ---------------------------------------------------------
        // 2. Append at Tail: add(element)
        // ---------------------------------------------------------
        System.out.printf("Test 2: Appending %d elements at TAIL: add(e)%n", INSERT_TAIL_COUNT);

        List<Integer> arrayListTail = new ArrayList<>();
        start = System.nanoTime();
        for (int i = 0; i < INSERT_TAIL_COUNT; i++) {
            arrayListTail.add(i);
        }
        long arrayListTailTime = (System.nanoTime() - start) / 1_000_000;

        List<Integer> linkedListTail = new LinkedList<>();
        start = System.nanoTime();
        for (int i = 0; i < INSERT_TAIL_COUNT; i++) {
            linkedListTail.add(i);
        }
        long linkedListTailTime = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  - ArrayList.add(e)     : %4d ms (Amortized O(1), contiguous memory)%n", arrayListTailTime);
        System.out.printf("  - LinkedList.add(e)    : %4d ms (O(1) with Node object allocations)%n", linkedListTailTime);
        System.out.printf("  => Winner for Tail Append: %s%n%n", (arrayListTailTime <= linkedListTailTime ? "ArrayList" : "LinkedList"));

        // ---------------------------------------------------------
        // 3. Random/Indexed Access: get(i)
        // ---------------------------------------------------------
        System.out.printf("Test 3: Indexed Access: %d calls to get(i) across list of size %d%n", GET_ACCESS_COUNT, INSERT_HEAD_COUNT);

        // Prepopulate lists of identical size
        List<Integer> testArrayList = new ArrayList<>();
        List<Integer> testLinkedList = new LinkedList<>();
        for (int i = 0; i < INSERT_HEAD_COUNT; i++) {
            testArrayList.add(i);
            testLinkedList.add(i);
        }

        // Benchmark ArrayList.get(i)
        start = System.nanoTime();
        long dummySumArray = 0;
        for (int i = 0; i < GET_ACCESS_COUNT; i++) {
            dummySumArray += testArrayList.get(i);
        }
        long arrayListGetTime = (System.nanoTime() - start) / 1_000_000;

        // Benchmark LinkedList.get(i)
        start = System.nanoTime();
        long dummySumLinked = 0;
        for (int i = 0; i < GET_ACCESS_COUNT; i++) {
            dummySumLinked += testLinkedList.get(i);
        }
        long linkedListGetTime = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  - ArrayList.get(i)     : %4d ms (O(1) direct offset indexing)%n", arrayListGetTime);
        System.out.printf("  - LinkedList.get(i)    : %4d ms (O(n) sequential node traversal)%n", linkedListGetTime);
        System.out.printf("  => Winner for get(i): %s%n%n", (arrayListGetTime < linkedListGetTime ? "ArrayList (Massive Advantage!)" : "LinkedList"));

        // Prevent JIT compiler optimization from discarding loops
        if (dummySumArray != dummySumLinked) {
            System.out.println("Warning: Checksums do not match!");
        }

        System.out.println("Summary:");
        System.out.println("  * Use ArrayList for reading/searching by index and general purpose lists.");
        System.out.println("  * Use LinkedList only when frequent insertions/removals occur at head/tail without index lookups.");
        System.out.println("==========================================================================\n");
    }
}
