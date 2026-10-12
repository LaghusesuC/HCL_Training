# Order Processing System

## Overview

The Order Processing System is a Java application that processes customer orders by validating order quantities and checking product availability. It demonstrates object-oriented programming, custom exception handling, exception chaining, and Maven project management.

## Features

- **Order Validation:** Rejects quantities less than or equal to zero.
- **Stock Management:** Checks stock availability before processing an order.
- **Custom Exceptions:** Uses `InvalidQuantityException` and `InsufficientStockException`.
- **Exception Handling:** Demonstrates `try`, `catch`, and `finally`.
- **Exception Chaining:** Preserves the original exception as the cause of a higher-level exception.
- **Maven Build:** Uses Maven to compile and run the application.

## Technologies Used

- Java 17
- Apache Maven
- JUnit 5
- Git and GitHub

## Project Structure

```text
order-processing/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── hcl/
    │               └── training/
    │                   ├── Main.java
    │                   └── order/
    │                       ├── service/
    │                       │   └── OrderProcessor.java
    │                       └── exception/
    │                           ├── InvalidQuantityException.java
    │                           └── InsufficientStockException.java
    └── test/
        └── java/
```

## How It Works

The application initializes the available stock and processes different order quantities.

| Scenario | Initial Stock | Quantity | Expected Result |
|---|---:|---:|---|
| Valid order | 10 | 5 | Order succeeds; remaining stock is 5 |
| Invalid quantity | 10 | -1 | Order fails with `InvalidQuantityException` |
| Insufficient stock | 5 | 15 | Order fails with `InsufficientStockException` |

## Exception Handling

### InvalidQuantityException

Thrown when the requested order quantity is zero or negative.

### InsufficientStockException

Thrown when the requested quantity exceeds the available stock.

### Exception Chaining

When an order fails, the original custom exception can be wrapped in a `RuntimeException`. The original exception remains available through the cause and stack trace.

### Finally Block

The `finally` block prints a completion message whether the order succeeds or fails.

## Prerequisites

Ensure Java 17 and Maven are installed and configured in your system.

Verify the installations:

```bash
java -version
mvn -version
```

## How to Run

Open a terminal in the project root directory, where `pom.xml` is located.

### 1. Compile the project

```bash
mvn clean compile
```

### 2. Run the application

```bash
mvn exec:java
```

Ensure the `exec-maven-plugin` in `pom.xml` specifies the correct main class:

```xml
<mainClass>com.hcl.training.Main</mainClass>
```

### 3. Run tests

```bash
mvn test
```

### 4. Package the project

```bash
mvn clean package
```

## Learning Outcomes

- Understanding Java classes, constructors, and encapsulation.
- Creating and using custom exceptions.
- Understanding checked and unchecked exceptions.
- Using `throw` and `throws`.
- Implementing `try`, `catch`, and `finally`.
- Understanding exception chaining and stack traces.
- Configuring and executing a Java project using Maven.
- Writing unit tests using JUnit 5.

## Author

HCL Training — Java Development Practice Project
