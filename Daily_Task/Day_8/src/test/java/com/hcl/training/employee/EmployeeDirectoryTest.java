package com.hcl.training.employee;

import com.hcl.training.employee.model.BuggyEmployee;
import com.hcl.training.employee.model.Employee;
import com.hcl.training.employee.model.FixedEmployee;
import com.hcl.training.employee.service.EmployeeDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDirectoryTest {

    private EmployeeDirectory directory;

    @BeforeEach
    void setUp() {
        directory = new EmployeeDirectory();
        directory.addEmployee(new Employee(105, "Clark Kent", "Editorial", 85000.0));
        directory.addEmployee(new Employee(101, "Alice Walker", "Engineering", 110000.0));
        directory.addEmployee(new Employee(102, "Bruce Wayne", "Management", 190000.0));
        directory.addEmployee(new Employee(103, "Arthur Curry", "Operations", 88000.0));
        directory.addEmployee(new Employee(104, "Diana Prince", "Engineering", 125000.0));
        directory.addEmployee(new Employee(106, "Hal Jordan", "Engineering", 95000.0));
    }

    @Test
    @DisplayName("HashMap Lookup: Retrieve employee by ID in O(1)")
    void testHashMapLookup() {
        Optional<Employee> emp = directory.lookupById(101);
        assertTrue(emp.isPresent(), "Employee 101 should be present");
        assertEquals("Alice Walker", emp.get().getName());
        assertEquals("Engineering", emp.get().getDepartment());

        Optional<Employee> missing = directory.lookupById(999);
        assertTrue(missing.isEmpty(), "Non-existent ID should return Optional.empty");
    }

    @Test
    @DisplayName("Frequency Count with merge(): Department headcount")
    void testFrequencyCountWithMerge() {
        Map<String, Integer> headcount = directory.getDepartmentHeadcount();

        assertEquals(3, headcount.get("Engineering"), "Engineering should have 3 employees");
        assertEquals(1, headcount.get("Editorial"), "Editorial should have 1 employee");
        assertEquals(1, headcount.get("Management"), "Management should have 1 employee");
        assertEquals(1, headcount.get("Operations"), "Operations should have 1 employee");
        assertEquals(4, headcount.size(), "Should have 4 distinct departments");
    }

    @Test
    @DisplayName("TreeMap Department Report: Departments sorted in alphabetical order")
    void testTreeMapDepartmentReport() {
        Map<String, List<Employee>> report = directory.getDepartmentReport();

        // TreeMap should sort keys alphabetically: Editorial -> Engineering -> Management -> Operations
        List<String> depts = new ArrayList<>(report.keySet());
        List<String> expectedOrder = List.of("Editorial", "Engineering", "Management", "Operations");

        assertEquals(expectedOrder, depts, "TreeMap keys must be alphabetically sorted");
        assertEquals(3, report.get("Engineering").size());
    }

    @Test
    @DisplayName("Comparable by ID: Natural ordering sorting")
    void testComparableNaturalOrder() {
        List<Employee> sorted = directory.getEmployeesSortedById();

        assertEquals(6, sorted.size());
        assertEquals(101, sorted.get(0).getId());
        assertEquals(102, sorted.get(1).getId());
        assertEquals(103, sorted.get(2).getId());
        assertEquals(104, sorted.get(3).getId());
        assertEquals(105, sorted.get(4).getId());
        assertEquals(106, sorted.get(5).getId());
    }

    @Test
    @DisplayName("Top-5 Salary Comparator Chain: Salary descending, ties broken by name ascending")
    void testTop5SalaryComparatorChain() {
        // Add duplicate salary to test tie breaker
        directory.addEmployee(new Employee(107, "Barry Allen", "Engineering", 125000.0)); // Same salary as Diana Prince

        List<Employee> top5 = directory.getTop5HighestPaidEmployees();

        assertEquals(5, top5.size(), "Should return exactly top 5");
        assertEquals(102, top5.get(0).getId(), "Highest: Bruce Wayne ($190,000)");

        // Diana Prince ($125,000) and Barry Allen ($125,000): Barry Allen should come before Diana Prince due to name ASC
        assertEquals("Barry Allen", top5.get(1).getName());
        assertEquals("Diana Prince", top5.get(2).getName());
        assertEquals("Alice Walker", top5.get(3).getName()); // $110,000
        assertEquals("Hal Jordan", top5.get(4).getName());   // $95,000
    }

    @Test
    @DisplayName("BuggyEmployee: Missing hashCode breaks HashMap retrieval")
    void testMissingHashCodeBug() {
        Map<BuggyEmployee, String> buggyMap = new HashMap<>();
        BuggyEmployee stored = new BuggyEmployee(1, "Test User");
        buggyMap.put(stored, "Developer");

        BuggyEmployee lookup = new BuggyEmployee(1, "Test User");

        // They are equal according to equals()
        assertEquals(stored, lookup, "Logical equals should be true");

        // But because hashCode is not overridden, lookup fails in HashMap
        assertNotEquals(stored.hashCode(), lookup.hashCode(), "Identity hash codes should differ for different instances");
        assertNull(buggyMap.get(lookup), "HashMap.get() returns null due to missing hashCode() override");
    }

    @Test
    @DisplayName("FixedEmployee: Correct hashCode fixes HashMap retrieval")
    void testFixedHashCode() {
        Map<FixedEmployee, String> fixedMap = new HashMap<>();
        FixedEmployee stored = new FixedEmployee(1, "Test User");
        fixedMap.put(stored, "Developer");

        FixedEmployee lookup = new FixedEmployee(1, "Test User");

        // Both equals and hashCode match
        assertEquals(stored, lookup);
        assertEquals(stored.hashCode(), lookup.hashCode());

        // Successfully retrieves the value!
        assertEquals("Developer", fixedMap.get(lookup), "HashMap.get() successfully returns value with fixed hashCode");
    }
}
