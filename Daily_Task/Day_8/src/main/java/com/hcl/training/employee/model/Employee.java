package com.hcl.training.employee.model;

import java.util.Objects;

/**
 * Represents an Employee in the directory.
 * Implements Comparable for natural ordering by Employee ID.
 */
public class Employee implements Comparable<Employee> {
    private final int id;
    private final String name;
    private final String department;
    private final double salary;

    public Employee(int id, String name, String department, double salary) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive: " + id);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department cannot be null or blank");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative: " + salary);
        }
        this.id = id;
        this.name = name.trim();
        this.department = department.trim();
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    /**
     * Natural Ordering: Sorted by employee ID ascending.
     */
    @Override
    public int compareTo(Employee other) {
        if (other == null) {
            return 1;
        }
        return Integer.compare(this.id, other.id);
    }

    /**
     * Standard equals contract based on employee fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
               Double.compare(employee.salary, salary) == 0 &&
               Objects.equals(name, employee.name) &&
               Objects.equals(department, employee.department);
    }

    /**
     * Standard hashCode contract consistent with equals.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, department, salary);
    }

    @Override
    public String toString() {
        return String.format("[ID: %-4d | Name: %-15s | Dept: %-12s | Salary: $%,10.2f]",
                id, name, department, salary);
    }
}
