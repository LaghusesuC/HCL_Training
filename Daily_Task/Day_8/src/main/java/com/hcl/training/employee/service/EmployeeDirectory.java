package com.hcl.training.employee.service;

import com.hcl.training.employee.model.Employee;

import java.util.*;

/**
 * Service managing employee records using various Java Collections Framework structures.
 * 
 * Features:
 * 1. HashMap lookup by ID (O(1) average time)
 * 2. Department frequency counting using Map.merge()
 * 3. Sorted department roster reports using TreeMap
 * 4. Natural ordering sorting (Comparable by ID)
 * 5. Top-5 highest earners using chained Comparator
 */
public class EmployeeDirectory {

    // Primary storage: HashMap for fast O(1) key-value lookup by Employee ID
    private final Map<Integer, Employee> directory = new HashMap<>();

    /**
     * Comparator chain for highest earners:
     * Primary: Salary descending (highest first)
     * Secondary (Tie-breaker): Name ascending (alphabetical)
     */
    public static final Comparator<Employee> TOP_SALARY_COMPARATOR = Comparator
            .comparingDouble(Employee::getSalary).reversed()
            .thenComparing(Employee::getName);

    /**
     * Adds an employee to the directory.
     *
     * @param employee the employee to add
     * @return previous Employee associated with the ID, or null if none
     */
    public Employee addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        return directory.put(employee.getId(), employee);
    }

    /**
     * Bulk adds multiple employees.
     */
    public void addAll(Collection<Employee> employees) {
        if (employees != null) {
            for (Employee emp : employees) {
                addEmployee(emp);
            }
        }
    }

    /**
     * HashMap Lookup: Fast O(1) retrieval by employee ID.
     *
     * @param id Employee ID
     * @return Optional containing the employee if found, or empty
     */
    public Optional<Employee> lookupById(int id) {
        return Optional.ofNullable(directory.get(id));
    }

    /**
     * Checks if an employee ID exists in the directory.
     */
    public boolean containsId(int id) {
        return directory.containsKey(id);
    }

    /**
     * Returns total number of employees in the directory.
     */
    public int size() {
        return directory.size();
    }

    /**
     * Returns all employees currently in the directory.
     */
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(directory.values());
    }

    /**
     * Frequency Count with Map.merge():
     * Counts the number of employees in each department.
     * 
     * How Map.merge works:
     * - If key not present: associates key with value (1)
     * - If key present: computes new value using Integer::sum (oldVal + 1)
     *
     * @return Map of Department -> Employee count
     */
    public Map<String, Integer> getDepartmentHeadcount() {
        Map<String, Integer> counts = new HashMap<>();
        for (Employee emp : directory.values()) {
            counts.merge(emp.getDepartment(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * TreeMap Department Report:
     * Groups employees by department in natural alphabetical order using TreeMap.
     * TreeMap guarantees keys are ordered based on String.compareTo().
     *
     * @return TreeMap mapping sorted Department Name -> List of Employees in that department
     */
    public Map<String, List<Employee>> getDepartmentReport() {
        Map<String, List<Employee>> report = new TreeMap<>();
        for (Employee emp : directory.values()) {
            report.computeIfAbsent(emp.getDepartment(), k -> new ArrayList<>()).add(emp);
        }
        return report;
    }

    /**
     * Natural Ordering (Comparable by ID):
     * Sorts employees using their natural ordering defined in Employee.compareTo().
     *
     * @return List of employees sorted by ID ascending
     */
    public List<Employee> getEmployeesSortedById() {
        List<Employee> sortedList = new ArrayList<>(directory.values());
        Collections.sort(sortedList); // Delegates to Employee's Comparable implementation
        return sortedList;
    }

    /**
     * Top-5 Salary Comparator Chain:
     * Uses Comparator.comparingDouble(Employee::getSalary).reversed().thenComparing(...)
     * to rank employees by salary descending and return the top 5 earners.
     *
     * @return List containing at most 5 highest paid employees
     */
    public List<Employee> getTop5HighestPaidEmployees() {
        return directory.values().stream()
                .sorted(TOP_SALARY_COMPARATOR)
                .limit(5)
                .toList();
    }
}
