# Day 8: Employee Directory & Collections Deep Dive

## Overview

The **Employee Directory System** is a lightweight, clean Java 17 application demonstrating essential **Java Collections Framework (JCF)** concepts:

1. **`HashMap` Lookup**: Instant $O(1)$ lookup of employees by their unique ID.
2. **Frequency Count with `Map.merge()`**: Clean, modern idiomatic counting of departmental headcount without verbose if-else boilerplate.
3. **`TreeMap` Department Report**: Grouping and displaying employees by department sorted alphabetically by key.
4. **`Comparable` Natural Ordering**: Implementing `Comparable<Employee>` to naturally sort employees by their ID.
5. **Top-5 Salary `Comparator` Chain**: Composing multi-level sorting logic using `Comparator.comparingDouble(...).reversed().thenComparing(...)`.
6. **Missing-`hashCode` Bug Demo & Fix**: Clear demonstration of why breaking the `equals()` and `hashCode()` contract silently breaks `HashMap` and `HashSet` lookups, and how to fix it.

---

## Architecture & Concepts

| Topic | Concept / Mechanism | Description |
|---|---|---|
| **HashMap Lookup** | `Map<Integer, Employee>` | $O(1)$ average time complexity for retrieval by key via bucket hash table. |
| **Frequency Count** | `map.merge(key, 1, Integer::sum)` | Atomic, clean accumulator pattern replacing traditional `map.get()` + `put()` checks. |
| **Sorted Reports** | `TreeMap<String, List<Employee>>` | Red-Black tree backed map guaranteeing keys are sorted in natural alphabetical order. |
| **Natural Ordering** | `implements Comparable<Employee>` | Encapsulates intrinsic ordering logic inside the model class via `compareTo()`. |
| **Comparator Chaining** | `.reversed().thenComparing(...)` | Flexible external ordering: Sorts highest salary first, with name ascending as tie-breaker. |
| **hashCode Contract** | `equals()` + `hashCode()` | Demonstrates that if $a.equals(b) \implies a.hashCode() == b.hashCode()$. |

---

## Project Structure

```text
Day_8/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── hcl/
    │               └── training/
    │                   └── employee/
    │                       ├── Main.java
    │                       ├── model/
    │                       │   ├── Employee.java
    │                       │   ├── BuggyEmployee.java
    │                       │   └── FixedEmployee.java
    │                       └── service/
    │                           └── EmployeeDirectory.java
    └── test/
        └── java/
            └── com/
                └── hcl/
                    └── training/
                        └── employee/
                            └── EmployeeDirectoryTest.java
```

---

## Key Implementations

### 1. Frequency Count with `Map.merge()`
```java
public Map<String, Integer> getDepartmentHeadcount() {
    Map<String, Integer> counts = new HashMap<>();
    for (Employee emp : directory.values()) {
        counts.merge(emp.getDepartment(), 1, Integer::sum);
    }
    return counts;
}
```

### 2. Top-5 Salary Comparator Chain
```java
public static final Comparator<Employee> TOP_SALARY_COMPARATOR = Comparator
        .comparingDouble(Employee::getSalary).reversed()
        .thenComparing(Employee::getName);

public List<Employee> getTop5HighestPaidEmployees() {
    return directory.values().stream()
            .sorted(TOP_SALARY_COMPARATOR)
            .limit(5)
            .toList();
}
```

### 3. Missing `hashCode` Bug & Fix

#### The Bug (`BuggyEmployee.java`)
```java
// Overrides equals() based on ID and Name:
@Override
public boolean equals(Object o) { ... }

// BUG: Missing hashCode()!
// Inherits java.lang.Object's memory identity hash code.
```
- When `map.put(key1, "Desk A")` is called, `key1` lands in bucket $B_1$.
- When querying `map.get(new BuggyEmployee(501, "John"))`, the new instance gets a different identity hash code, searching in bucket $B_2$.
- Result: **Returns `null`** despite logical equality!

#### The Fix (`FixedEmployee.java`)
```java
@Override
public int hashCode() {
    return Objects.hash(id, name);
}
```
- Equal objects now generate identical hash codes, landing in bucket $B_1$.
- `HashMap` compares the keys using `equals()` in the bucket and successfully retrieves `"Desk A"`.

---

## Running the Application

### Via Maven:
```bash
# Run the application
mvn clean compile exec:java

# Run all unit tests
mvn test
```
