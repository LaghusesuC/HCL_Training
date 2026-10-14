package com.hcl.training.employee;

import com.hcl.training.employee.model.BuggyEmployee;
import com.hcl.training.employee.model.Employee;
import com.hcl.training.employee.model.FixedEmployee;
import com.hcl.training.employee.service.EmployeeDirectory;

import java.util.*;

/**
 * Main application class demonstrating Day 8 concepts:
 * 1. HashMap lookup
 * 2. Frequency count with Map.merge()
 * 3. TreeMap department report (alphabetical sorting)
 * 4. Comparable by ID (natural ordering)
 * 5. Top-5 salary using Comparator chain
 * 6. Missing hashCode bug demonstration and fix
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("          DAY 8: EMPLOYEE DIRECTORY & COLLECTIONS DEEP DIVE        ");
        System.out.println("==================================================================");

        EmployeeDirectory directory = new EmployeeDirectory();

        // Populate sample employees across multiple departments and salary ranges
        List<Employee> sampleEmployees = List.of(
                new Employee(108, "Diana Prince", "Engineering", 125000.0),
                new Employee(102, "Bruce Wayne", "Management", 190000.0),
                new Employee(105, "Clark Kent", "Editorial", 85000.0),
                new Employee(101, "Alice Walker", "Engineering", 110000.0),
                new Employee(107, "Barry Allen", "Engineering", 95000.0),
                new Employee(103, "Arthur Curry", "Operations", 88000.0),
                new Employee(106, "Victor Stone", "Engineering", 135000.0),
                new Employee(104, "Hal Jordan", "Operations", 95000.0),
                new Employee(110, "Selina Kyle", "Security", 92000.0),
                new Employee(109, "Oliver Queen", "Management", 150000.0)
        );

        directory.addAll(sampleEmployees);
        System.out.println("Initialized directory with " + directory.size() + " employees.\n");

        // -------------------------------------------------------------
        // 1. HashMap Lookup
        // -------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println(" 1. HASHMAP LOOKUP (O(1) AVERAGE TIME)");
        System.out.println("------------------------------------------------------------------");
        int searchIdFound = 101;
        int searchIdNotFound = 999;

        System.out.println("Searching for Employee ID: " + searchIdFound);
        directory.lookupById(searchIdFound).ifPresentOrElse(
                emp -> System.out.println(" -> Found: " + emp),
                () -> System.out.println(" -> Not found.")
        );

        System.out.println("Searching for Employee ID: " + searchIdNotFound);
        directory.lookupById(searchIdNotFound).ifPresentOrElse(
                emp -> System.out.println(" -> Found: " + emp),
                () -> System.out.println(" -> Employee ID " + searchIdNotFound + " does not exist in directory.")
        );
        System.out.println();

        // -------------------------------------------------------------
        // 2. Frequency Count with Map.merge()
        // -------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println(" 2. FREQUENCY COUNT WITH Map.merge()");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Counting employees per department using: map.merge(dept, 1, Integer::sum)");
        Map<String, Integer> headcount = directory.getDepartmentHeadcount();
        headcount.forEach((dept, count) ->
                System.out.printf(" - %-14s : %d employee(s)%n", dept, count)
        );
        System.out.println();

        // -------------------------------------------------------------
        // 3. TreeMap Department Report (Sorted Alphabetically)
        // -------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println(" 3. TREEMAP DEPARTMENT REPORT (ALPHABETICAL ORDER)");
        System.out.println("------------------------------------------------------------------");
        Map<String, List<Employee>> report = directory.getDepartmentReport();
        report.forEach((dept, staff) -> {
            System.out.println("[" + dept.toUpperCase() + "] (Headcount: " + staff.size() + ")");
            for (Employee emp : staff) {
                System.out.println("    " + emp);
            }
        });
        System.out.println();

        // -------------------------------------------------------------
        // 4. Comparable by ID (Natural Ordering)
        // -------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println(" 4. COMPARABLE BY ID (NATURAL ORDERING)");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Employees sorted by ID using Employee.compareTo():");
        List<Employee> sortedById = directory.getEmployeesSortedById();
        sortedById.forEach(emp -> System.out.println("  " + emp));
        System.out.println();

        // -------------------------------------------------------------
        // 5. Top-5 Salary Comparator Chain
        // -------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println(" 5. TOP-5 SALARY (COMPARATOR CHAIN)");
        System.out.println("    Chain: Salary DESC -> Name ASC (Tie-breaker)");
        System.out.println("------------------------------------------------------------------");
        List<Employee> top5 = directory.getTop5HighestPaidEmployees();
        for (int i = 0; i < top5.size(); i++) {
            System.out.printf("  #%d: %s%n", i + 1, top5.get(i));
        }
        System.out.println();

        // -------------------------------------------------------------
        // 6. Missing hashCode Bug Demo and Fix
        // -------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println(" 6. MISSING-HASHCODE BUG DEMO AND FIX");
        System.out.println("------------------------------------------------------------------");
        demoMissingHashCodeBugAndFix();

        System.out.println("==================================================================");
        System.out.println("                       END OF DAY 8 DEMO                          ");
        System.out.println("==================================================================");
    }

    private static void demoMissingHashCodeBugAndFix() {
        System.out.println("SCENARIO: Overriding equals() without overriding hashCode().\n");

        // 1. THE BUG
        System.out.println(">>> BUG DEMONSTRATION (BuggyEmployee with missing hashCode):");
        Map<BuggyEmployee, String> buggyMap = new HashMap<>();

        BuggyEmployee key1 = new BuggyEmployee(501, "John Doe");
        buggyMap.put(key1, "Assigned Desk A-12");

        BuggyEmployee keyLookup = new BuggyEmployee(501, "John Doe");

        System.out.println("Stored Key:   " + key1);
        System.out.println("Lookup Key:   " + keyLookup);
        System.out.println("key1.equals(keyLookup)?               -> " + key1.equals(keyLookup));
        System.out.println("key1.hashCode() == keyLookup.hashCode()? -> " + (key1.hashCode() == keyLookup.hashCode()) +
                " (" + key1.hashCode() + " != " + keyLookup.hashCode() + ")");
        System.out.println("HashMap lookup result (buggyMap.get):  -> " + buggyMap.get(keyLookup));
        System.out.println("RESULT: Returns NULL! Even though equals() is true, they hash to different buckets!\n");

        // 2. THE FIX
        System.out.println(">>> THE FIX (FixedEmployee with matching hashCode implementation):");
        Map<FixedEmployee, String> fixedMap = new HashMap<>();

        FixedEmployee fixedKey1 = new FixedEmployee(501, "John Doe");
        fixedMap.put(fixedKey1, "Assigned Desk A-12");

        FixedEmployee fixedKeyLookup = new FixedEmployee(501, "John Doe");

        System.out.println("Stored Key:   " + fixedKey1);
        System.out.println("Lookup Key:   " + fixedKeyLookup);
        System.out.println("fixedKey1.equals(fixedKeyLookup)?         -> " + fixedKey1.equals(fixedKeyLookup));
        System.out.println("fixedKey1.hashCode() == fixedKeyLookup.hashCode()? -> " +
                (fixedKey1.hashCode() == fixedKeyLookup.hashCode()) +
                " (" + fixedKey1.hashCode() + " == " + fixedKeyLookup.hashCode() + ")");
        System.out.println("HashMap lookup result (fixedMap.get):    -> \"" + fixedMap.get(fixedKeyLookup) + "\"");
        System.out.println("RESULT: SUCCESS! Objects hash to the exact same bucket and equals() confirms match.");
        System.out.println();
    }
}
