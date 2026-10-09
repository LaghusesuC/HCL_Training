# 💳 Day 5: Payment Hierarchy & Git Conflict Resolution Lab

A simple, clean Java implementation demonstrating **OOP Inheritance**, **Method Overloading**, and **Interfaces**, along with a step-by-step paired **Git Merge Conflict & Rebase Lab**.

---

## 📂 Project Structure

```text
Day_5/
├── pom.xml                                      # Maven configuration (Java 17)
├── README.md                                    # Documentation and Git lab guide
└── src/
    └── main/
        └── java/
            └── com/
                └── hcl/
                    └── payment/
                        ├── Payment.java         # Abstract base class with overloaded pay()
                        ├── Refundable.java      # Interface for refundable payments
                        ├── CardPayment.java     # Extends Payment, implements Refundable
                        ├── UpiPayment.java      # Extends Payment, implements Refundable
                        ├── CashPayment.java     # Extends Payment (Non-refundable)
                        └── Main.java            # Demo runner
```

---

## ☕ 1. Core Java Concepts

### Abstract Class & Method Overloading (`Payment.java`)
- **Abstract class `Payment`** provides the base `amount` and an abstract `pay()` method.
- **Method Overloading**:
  - `pay()`: Standard payment execution.
  - `pay(double discount)`: Applies discount to amount and executes payment.

### Subclasses
- **`CardPayment`**: Takes `amount` and `cardNumber`. Implements `Refundable`.
- **`UpiPayment`**: Takes `amount` and `upiId`. Implements `Refundable`.
- **`CashPayment`**: Takes `amount`. Does **not** implement `Refundable` (Interface Segregation Principle).

### Interface (`Refundable.java`)
- Defines `void refund(double amount)`.
- Implemented by `CardPayment` and `UpiPayment`.

---

## ▶️ Running the Code

### Option 1: Using Maven
```bash
cd Daily_Task/Day_5
mvn compile exec:java
```

### Option 2: Using javac / java
```bash
cd Daily_Task/Day_5/src/main/java
javac com/hcl/training/payment/*.java
java com.hcl.training.payment.Main
```

### Expected Output
```text
==================================================
         DAY 5: PAYMENT HIERARCHY DEMO            
==================================================

--- 1. Card Payment ---
Processing Card Payment of Rs.1500.0 via Card: **** 3456
Applying discount of 200.0 on 1500.0
Processing Card Payment of Rs.1300.0 via Card: **** 3456
Processing Card Refund of Rs.500.0 to Card ending in 3456

--- 2. UPI Payment ---
Processing UPI Payment of Rs.850.0 to VPA: rohit@okaxis
Applying discount of 50.0 on 850.0
Processing UPI Payment of Rs.800.0 to VPA: rohit@okaxis
Processing UPI Refund of Rs.100.0 back to VPA: rohit@okaxis

--- 3. Cash Payment ---
Processing Cash Payment of Rs.500.0 at the counter.
Applying discount of 50.0 on 500.0
Processing Cash Payment of Rs.450.0 at the counter.
Notice: CashPayment does NOT implement Refundable interface.

==================================================
```

---

## 🔀 2. Paired Git Lab: Merge Conflict & Rebase

### 🎯 Objective
Two students work on separate branches modifying the same line in `Payment.java`, create a merge conflict on `main`, resolve it, and rebase a second branch onto `main`.

---

### Step 1: Student A creates branch and edits `Payment.java`

```bash
# Ensure you are on main and up to date
git checkout main

# Create Student A's branch
git checkout -b student-a-card-fee

# Edit Payment.java (e.g., change message inside pay() method to):
# System.out.println("Payment processed with standard fee.");

git add src/main/java/com/hcl/training/payment/Payment.java
git commit -m "Student A: Added standard fee note to pay"
```

---

### Step 2: Student B creates another branch and edits the same line

```bash
# Switch back to main
git checkout main

# Create Student B's branch
git checkout -b student-b-upi-discount

# Edit the EXACT SAME line in Payment.java to:
# System.out.println("Payment processed with instant discount.");

git add src/main/java/com/hcl/training/payment/Payment.java
git commit -m "Student B: Added instant discount note to pay"
```

---

### Step 3: Student A merges into `main` (Clean Merge)

```bash
git checkout main
git merge student-a-card-fee
# Merges cleanly because main had no conflicting changes
```

---

### Step 4: Student B creates the Merge Conflict

```bash
# Student B tries to merge into main:
git merge student-b-upi-discount
```

**Git Output:**
```text
Auto-merging src/main/java/com/hcl/training/payment/Payment.java
CONFLICT (content): Merge conflict in src/main/java/com/hcl/training/payment/Payment.java
Automatic merge failed; fix conflicts and then commit the result.
```

---

### Step 5: Resolve the Conflict

Open `Payment.java`. Git shows conflict markers:

```java
<<<<<<< HEAD
        System.out.println("Payment processed with standard fee.");
=======
        System.out.println("Payment processed with instant discount.");
>>>>>>> student-b-upi-discount
```

**To resolve:**
1. Choose the combined or desired code:
   ```java
   System.out.println("Payment processed (standard fee and instant discount applied).");
   ```
2. Delete `<<<<<<< HEAD`, `=======`, and `>>>>>>> student-b-upi-discount`.
3. Save the file and complete the merge:
   ```bash
   git add src/main/java/com/hcl/training/payment/Payment.java
   git commit -m "Resolved merge conflict between Student A and Student B"
   ```

---

### Step 6: Rebase a Second Branch onto `main`

Suppose Student B has another feature branch (`feature/receipt-generator`):

```bash
# Create feature branch from an older commit or main
git checkout -b feature/receipt-generator

# Make a small commit
# (e.g., add a comment in CardPayment.java)
git commit -am "Added receipt generation support"

# Now rebase onto updated main
git checkout feature/receipt-generator
git rebase main
```

- If conflicts occur during rebase:
  1. Fix the file.
  2. Run `git add <file>`.
  3. Run `git rebase --continue`.
- Result: The branch now has a linear history on top of `main`!

```bash
# Verify clean history
git log --oneline --graph
```
