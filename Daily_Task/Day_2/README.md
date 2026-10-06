# 📝 Day 2 Notes: Monthly Usage Analyser

A concise revision guide covering Java arrays (1-D and 2-D), constants, type casting, ternary operators, and integer overflow handling.

---

## ⚡ Quick Cheat Sheet

| Concept | Syntax / Example | Purpose |
| :--- | :--- | :--- |
| **Constants** | `public static final int SLAB_LOW = 150;` | Immutable threshold values |
| **1-D Array** | `int[] usage = { 120, 140, ... };` | Store a sequence of values (e.g., 12 months) |
| **Type Casting** | `double avg = (double) total / length;` | Avoid integer truncation division |
| **Ternary Operator** | `char g = (avg <= 150) ? 'A' : (avg <= 300 ? 'B' : 'C');` | Inline conditional expression |
| **Overflow Fix** | `long safeSum = (long) a + b;` | Prevents 32-bit sign wrap-around |
| **2-D Array** | `int[][] houses = { house1, house2, house3 };` | Array of arrays (Rows = Houses, Cols = Months) |

---

## 💻 Source Code: `MonthlyUsageAnalyser.java`

```java
public class MonthlyUsageAnalyser {

    // Threshold slabs for efficiency grades
    public static final int SLAB_LOW = 150;     // <= 150 -> Grade A
    public static final int SLAB_MEDIUM = 300;  // <= 300 -> Grade B, > 300 -> Grade C

    public static void main(String[] args) {
        // 1. Single House Usage (1-D Array: 12 months in kWh)
        int[] house1 = { 120, 140, 180, 220, 310, 340, 290, 260, 210, 175, 130, 115 };

        System.out.println("=== 1-D Array Analysis ===");
        analyseUsage("House 1", house1);

        // 2. Multi-House Usage (2-D Array: 3 Houses x 12 Months)
        int[][] houses = {
            house1,
            {  90, 105, 110, 130, 145, 160, 150, 140, 125, 110,  95,  85 },
            { 320, 350, 380, 420, 450, 480, 460, 430, 390, 360, 330, 310 }
        };

        System.out.println("\n=== 2-D Array Analysis ===");
        for (int i = 0; i < houses.length; i++) {
            analyseUsage("House " + (i + 1), houses[i]);
        }

        // 3. Integer Overflow Demonstration & Fix
        System.out.println("\n=== Integer Overflow Demo ===");
        int a = 1_500_000_000, b = 1_000_000_000;
        System.out.println("int sum (overflow) : " + (a + b));
        System.out.println("long sum (safe)    : " + ((long) a + b));
    }

    // Reusable analysis method for any house's monthly data
    public static void analyseUsage(String label, int[] usage) {
        long total = 0L; // long accumulator prevents overflow
        int min = usage[0], max = usage[0];

        for (int units : usage) {
            total += units;
            if (units < min) min = units;
            if (units > max) max = units;
        }

        // Explicit cast to avoid integer division truncation
        double avg = (double) total / usage.length;

        // Nested ternary operator to assign grade
        char grade = (avg <= SLAB_LOW) ? 'A' : (avg <= SLAB_MEDIUM ? 'B' : 'C');

        System.out.printf("%s -> Total: %4d | Avg: %6.2f | Min: %3d | Max: %3d | Grade: %c%n",
                label, total, avg, min, max, grade);
    }
}
```

---

## 🖥️ Sample Output

```text
=== 1-D Array Analysis ===
House 1 -> Total: 2490 | Avg: 207.50 | Min: 115 | Max: 340 | Grade: B

=== 2-D Array Analysis ===
House 1 -> Total: 2490 | Avg: 207.50 | Min: 115 | Max: 340 | Grade: B
House 2 -> Total: 1445 | Avg: 120.42 | Min:  85 | Max: 160 | Grade: A
House 3 -> Total: 4680 | Avg: 390.00 | Min: 310 | Max: 480 | Grade: C

=== Integer Overflow Demo ===
int sum (overflow) : -1794967296
long sum (safe)    : 2500000000
```

---

## 🧠 Key Revision Concepts

### 1. Integer Division vs. Explicit Cast
- In Java, `int / int` truncates decimals (e.g., `2490 / 12 = 207`).
- Casting `(double) total / length` promotes the operation to floating-point division (`207.50`).

### 2. Nested Ternary Operator
```java
char grade = (avg <= SLAB_LOW) ? 'A' : (avg <= SLAB_MEDIUM ? 'B' : 'C');
```
- Replaces multiple `if-else` lines with a clean, single-line expression.

### 3. Integer Overflow Protection
- Java `int` is 32-bit signed with max value `2,147,483,647`.
- Exceeding this limit causes silent wrap-around to negative values.
- Using 64-bit `long` (e.g., `long total = 0L` or `(long) a + b`) prevents overflow.

### 4. 2-D Arrays as Arrays of Arrays
- `houses.length` returns the number of rows (houses).
- `houses[i].length` returns the number of columns in row `i` (months).

---

## 🎯 Quick Interview Questions

1. **Why cast to `double` before dividing?**  
   Otherwise, Java executes integer division and silently discards the fractional part.
2. **What happens during integer overflow in Java?**  
   The 32-bit value wraps around to negative values via two's complement without throwing an exception.
3. **Can a 2-D array have rows of different lengths in Java?**  
   Yes. In Java, 2-D arrays are arrays of independent array objects (ragged/jagged arrays).

---

## 🔑 Git & SSH Quick Reference

```bash
# 1. Generate SSH Key (if not done)
ssh-keygen -t ed25519 -C "your_email@example.com"

# 2. Copy Public Key to Clipboard (PowerShell)
Get-Content ~/.ssh/id_ed25519.pub | Set-Clipboard

# 3. Add Key in GitHub -> Settings -> SSH and GPG keys -> New SSH Key

# 4. Push Day 2
git add .
git commit -m "Add Day 2: Monthly Usage Analyser"
git push origin main
```
