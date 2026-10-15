# Day 9: Generic Toolkit & Resource Management

## Overview

A clean, modern Java 17 application demonstrating **Java Generics** (type safety, type bounds, wildcards, and the PECS principle) alongside **Automatic Resource Management** (`try-with-resources` and `AutoCloseable` execution lifecycle).

---

## Core Concepts & Highlights

| Feature | Code Representation | Key Principle / Benefit |
|---|---|---|
| **Generic Pair** | `Pair<K, V>` | Type-safe 2-tuple holding key-value associations without type-casting. Includes `.swap()`. |
| **Bounded Type Parameter** | `Box<T extends Number>` | Restricts `T` to `Number` subclasses (`Integer`, `Double`, etc.), allowing numeric operations like `.doubleValue()`. |
| **Generic Bounded Method** | `<T extends Comparable<T>> T max(...)` | Reusable max algorithm bounded by `Comparable<T>`, working uniformly on numbers, strings, and domain models. |
| **Upper-Bounded Wildcard** | `List<? extends Number>` | **Producer (Read-only)**: Accepts lists of any `Number` subtype; reads elements safely as `Number`. |
| **Lower-Bounded Wildcard** | `List<? super Integer>` | **Consumer (Write-only)**: Accepts lists of `Integer` or its supertypes (`Number`, `Object`); permits adding integers. |
| **PECS Pattern** | `copy(List<? extends T> src, List<? super T> dest)` | **P**roducer **E**xtends, **C**onsumer **S**uper — idiomatic generic data transfer. |
| **Unbounded Wildcard** | `List<?>` | Operates on lists of unknown type when logic is independent of the element type. |
| **Try-with-Resources** | `try (BufferedReader reader = ...) { ... }` | Safe, deterministic resource cleanup ensuring streams are closed even upon exceptions. |
| **Close Order (LIFO)** | `AutoCloseable` | Resources in `try(...)` are closed in **reverse order** of their declaration (Last-In, First-Out). |

---

## Project Structure

```text
Day_9/
├── pom.xml
├── products.csv
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── hcl/
    │   │           └── training/
    │   │               └── generics/
    │   │                   ├── Main.java
    │   │                   ├── model/
    │   │                   │   ├── Box.java
    │   │                   │   ├── Pair.java
    │   │                   │   └── Product.java
    │   │                   ├── resource/
    │   │                   │   └── CustomResource.java
    │   │                   ├── service/
    │   │                   │   └── ProductCsvReader.java
    │   │                   └── util/
    │   │                       └── GenericToolkit.java
    │   └── resources/
    │       └── products.csv
    └── test/
        └── java/
            └── com/
                └── hcl/
                    └── training/
                        └── generics/
                            └── GenericToolkitTest.java
```

---

## Technical Explanations

### 1. `Pair<K, V>`
`Pair<K, V>` provides an immutable, generic container for two related objects:
```java
Pair<String, Integer> student = Pair.of("Alice", 95);
Pair<Integer, String> swapped = student.swap(); // Pair(95, "Alice")
```

### 2. `Box<T extends Number>`
The upper bound `<T extends Number>` enforces compile-time safety:
```java
Box<Integer> intBox = new Box<>(42);        // OK: Integer extends Number
Box<Double> dblBox = new Box<>(99.95);      // OK: Double extends Number
// Box<String> strBox = new Box<>("test");  // COMPILE ERROR: String is not a Number!
```
It enables calling `item.doubleValue()` directly without casting.

### 3. `<T extends Comparable<T>> max()`
The method specifies that type `T` must be comparable to itself:
```java
public static <T extends Comparable<T>> T max(Collection<T> items)
```
Works seamlessly across:
- Numbers: `GenericToolkit.max(14, 88, 23, 95, 42)` -> `95`
- Strings: `GenericToolkit.max("Apple", "Zebra", "Banana")` -> `"Zebra"`
- Custom Objects: `GenericToolkit.max(productList)` -> product with highest price

### 4. Wildcards & The PECS Rule
- **Producer Extends (`? extends T`)**: When you only **read** items from a collection, use `extends`.
  ```java
  public static double sumOfNumbers(List<? extends Number> numbers)
  ```
- **Consumer Super (`? super T`)**: When you only **write** items to a collection, use `super`.
  ```java
  public static void addIntegers(List<? super Integer> list, int start, int end)
  ```
- **PECS Copy**:
  ```java
  public static <T> void copy(List<? extends T> src, List<? super T> dest)
  ```

### 5. CSV Parsing with Try-with-Resources
`ProductCsvReader` automatically cleans up file and stream handles upon completion:
```java
try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
    // Read and parse lines into List<Product>
} // reader.close() is guaranteed to execute here
```

### 6. AutoCloseable & Reverse Close Order (LIFO)
According to the Java Language Specification (§14.20.3):
- Resources in a `try-with-resources` statement are opened from **left to right**.
- Resources are closed in **reverse order** (Last-In, First-Out).

**Why Reverse Order?**
In real applications, later resources frequently depend on earlier ones (e.g., `BufferedWriter` wrapping a `FileWriter`, or a `Statement` depending on a `Connection`). Closing in reverse order ensures the dependent resource flushes/terminates before its underlying dependency closes.

```java
try (CustomResource r1 = new CustomResource("Resource-A [Database]");
     CustomResource r2 = new CustomResource("Resource-B [NetworkSocket]");
     CustomResource r3 = new CustomResource("Resource-C [FileWriter]")) {
    // Work
}
```
**Output Lifecycle:**
1. `[INIT]  Opened resource : Resource-A [Database]`
2. `[INIT]  Opened resource : Resource-B [NetworkSocket]`
3. `[INIT]  Opened resource : Resource-C [FileWriter]`
4. `[WORK]  Operating...`
5. `[CLOSE] Closed resource : Resource-C [FileWriter] (LIFO reverse order)`
6. `[CLOSE] Closed resource : Resource-B [NetworkSocket] (LIFO reverse order)`
7. `[CLOSE] Closed resource : Resource-A [Database] (LIFO reverse order)`

---

## How to Build and Run

### Run Application:
```bash
mvn clean compile exec:java
```

### Run Unit Tests:
```bash
mvn clean test
```

---

## Sample Console Output

```text
================================================================================
  DAY 9: GENERIC TOOLKIT & RESOURCE MANAGEMENT
================================================================================

--------------------------------------------------------------------------------
1. Generic Pair<K,V> Demonstration
--------------------------------------------------------------------------------
Created Pairs:
  Student Score : Pair(Alice, 95)
  Employee      : Pair(1001, John Developer)
  Product Price : Pair(Mechanical Keyboard, 89.99)
Swapped Student Score: Pair(95, Alice)
Key: 95, Value: Alice

--------------------------------------------------------------------------------
2. Bounded Type Box<T extends Number> Demonstration
--------------------------------------------------------------------------------
Integer Box : Box[42] -> doubleValue = 42.0
Double Box  : Box[99.95] -> doubleValue = 99.95
Long Box    : Box[1000000] -> doubleValue = 1000000.0
Is doubleBox > intBox? true
Is intBox > longBox?   false

--------------------------------------------------------------------------------
3. Generic Method: <T extends Comparable<T>> max()
--------------------------------------------------------------------------------
Max of [14, 88, 23, 95, 42]           : 95
Max of [Banana, Watermelon, Apple, Mango, Cherry] : Watermelon
Max Product by price: Product #2: Monitor [Electronics] - $299.99 (Qty: 5)

--------------------------------------------------------------------------------
4. Wildcards (Upper-Bounded, Lower-Bounded, Unbounded & PECS)
--------------------------------------------------------------------------------
Sum of Integer List <? extends Number> : 100.0
Sum of Double List  <? extends Number> : 36.5
Populated List<Number> via <? super Integer> : [1, 2, 3, 4, 5]
Destination after PECS copy(source, dest): [100, 200, 300]
Printing lists via unbounded wildcard List<?>:
  [List size=4]
    - Java Generics
    - PECS Rule
    - Try-With-Resources
    - AutoCloseable

--------------------------------------------------------------------------------
5. Reading products.csv with Try-With-Resources
--------------------------------------------------------------------------------
Successfully loaded 7 products from CSV:
  Product #101: Mechanical Keyboard [Electronics] - $89.99 (Qty: 15)
  Product #102: Ergonomic Chair [Furniture] - $249.50 (Qty: 8)
  Product #103: Wireless Mouse [Electronics] - $34.99 (Qty: 25)
  Product #104: Standing Desk [Furniture] - $399.00 (Qty: 5)
  Product #105: USB-C Hub [Electronics] - $45.00 (Qty: 30)
  Product #106: Noise Cancelling Headphones [Electronics] - $199.99 (Qty: 12)
  Product #107: Monitor Arm [Accessories] - $59.90 (Qty: 18)

Most expensive product (via GenericToolkit.max):
  -> Standing Desk ($399.0)

--------------------------------------------------------------------------------
6. Custom AutoCloseable & Close Order (LIFO / Reverse Order)
--------------------------------------------------------------------------------
Entering try-with-resources block...
Declaring: Resource-A (Database) -> Resource-B (Network) -> Resource-C (File)
  [INIT]  Opened resource : Resource-A [DatabaseConnection]
  [INIT]  Opened resource : Resource-B [NetworkSocket]
  [INIT]  Opened resource : Resource-C [FileWriter]

--- Inside try block: performing tasks ---
  [WORK]  Operating with  : Resource-A [DatabaseConnection]
  [WORK]  Operating with  : Resource-B [NetworkSocket]
  [WORK]  Operating with  : Resource-C [FileWriter]
--- End of try block reached, exiting scope ---

  [CLOSE] Closed resource : Resource-C [FileWriter] (LIFO reverse order)
  [CLOSE] Closed resource : Resource-B [NetworkSocket] (LIFO reverse order)
  [CLOSE] Closed resource : Resource-A [DatabaseConnection] (LIFO reverse order)
Exited try-with-resources block.
Notice that Resource-C closed first, then Resource-B, then Resource-A!

================================================================================
  DAY 9 DEMONSTRATION COMPLETE
================================================================================
```
