# 🏧 Day 3 Notes: ATM Simulator (Control Flow & Maven Profiles)

A comprehensive guide and reference implementation covering Java control flow statements (`do-while`, `switch`, `break`, `continue`, enhanced-`for`), robust input validation, and Apache Maven build automation with `dev`/`prod` profiles.

---

## ⚡ Quick Cheat Sheet

| Concept | Syntax / Implementation | Purpose & ATM Context |
| :--- | :--- | :--- |
| **`do-while` Loop** | `do { ... } while (choice != 5);` | Guarantees the ATM menu runs at least once before testing termination condition |
| **`switch-case`** | `switch (choice) { case 1: ... break; }` | Clean multi-branch dispatching of selected ATM operations |
| **`break` Statement** | `if (pin.equals(CORRECT_PIN)) break;` | Immediately exits the 3-attempt PIN authentication loop on success or limit lock |
| **`continue` Statement**| `if (invalid) { displayError(); continue; }` | Skips remaining statements and immediately jumps to the next loop iteration |
| **Enhanced-`for` Loop** | `for (String entry : miniStatement) { ... }` | Cleanly iterates through transaction history without indexing variables |
| **Input Validation** | `Integer.parseInt(input)` inside `try-catch` | Rejects non-numeric characters, out-of-range choices, and negative amounts |
| **Maven Profiles** | `<profile><id>dev</id>...</profile>` | Environment-specific build configurations (`dev` vs `prod`) |
| **Resource Filtering** | `${property.name}` in `.properties` file | Substitutes POM properties into build artifacts during packaging |

---

## 🏗️ Project Structure

```text
Day_3/
├── pom.xml                                      # Maven configuration with dev/prod profiles
├── README.md                                    # Documentation & execution notes
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── hcl/
        │           └── atm/
        │               └── AtmSimulator.java    # Full ATM simulator implementation
        └── resources/
            └── application.properties           # Filtered profile properties file
```

---

## 🔄 Core Concepts Breakdown

### 1. 3 PIN Attempts with `break`
The ATM enforces security by allowing a maximum of **3 PIN verification attempts**:
- If the entered PIN matches, the program prints a welcome message and executes **`break`** to exit the loop immediately.
- If the entered PIN is incorrect, remaining attempts are decremented.
- Upon 3 failed attempts, a security alert is triggered, and **`break`** terminates authentication, exiting the application.

```java
for (int attempt = 1; attempt <= MAX_PIN_ATTEMPTS; attempt++) {
    System.out.printf("Attempt [%d/%d] - Enter 4-digit PIN: ", attempt, MAX_PIN_ATTEMPTS);
    String inputPin = scanner.nextLine().trim();

    if (DEFAULT_PIN.equals(inputPin)) {
        isAuthenticated = true;
        break; // Exit loop on success
    } else {
        int attemptsRemaining = MAX_PIN_ATTEMPTS - attempt;
        if (attemptsRemaining == 0) {
            System.out.println("Card blocked after 3 failed attempts!");
            break; // Exit loop after 3 failed attempts
        }
    }
}
```

### 2. `do-while` Menu with `switch`
The ATM options menu is wrapped in a `do-while` loop so the user sees the menu at least once and can perform multiple transactions until selecting **Option 5 (Exit)**:

```java
do {
    printMenu();
    // read and validate choice...
    switch (choice) {
        case 1: checkBalance(); break;
        case 2: depositCash(); break;
        case 3: withdrawCash(); break;
        case 4: printMiniStatement(); break;
        case 5: exitAtm(); break;
    }
} while (choice != 5);
```

### 3. Input Validation & `continue` on Invalid Entry
Every user input is validated before execution:
- **Menu Choice:** Non-numeric entries and out-of-range selections (e.g., letters, blank input, or numbers `< 1` or `> 5`) print an error and trigger `continue`, instantly refreshing the menu.
- **Deposit / Withdrawal Amount:** Rejects non-numeric values, amounts `<= 0`, amounts not in multiples of 100, amounts exceeding available balance, or amounts exceeding the profile-defined daily limit. `continue` returns control directly to the main menu without altering state.

```java
if (parsedChoice < 1 || parsedChoice > 5) {
    System.out.println(">>> [INVALID ENTRY] Choice out of bounds (1-5 only).");
    continue; // Skips switch and re-prompts menu
}
```

### 4. Enhanced-`for` Loop for Mini-Statement
All account events (opening balance, deposits, withdrawals) are timestamped and appended to `miniStatement`. Option 4 uses the **enhanced-`for` loop** (`for-each`) to iterate and display each entry:

```java
for (String entry : miniStatement) {
    System.out.println("  • " + entry);
}
```

---

## ⚙️ Maven Build Profiles: `dev` vs `prod`

Maven profiles allow environment-specific configurations at build time. We configured two profiles in `pom.xml`:

| Profile Property | `dev` Profile (Default) | `prod` Profile |
| :--- | :--- | :--- |
| **Profile ID** | `dev` | `prod` |
| **`app.env`** | `DEVELOPMENT` | `PRODUCTION` |
| **`atm.bank.name`** | `HCL Bank (Dev Sandbox)` | `HCL Premier Bank` |
| **`atm.daily.limit`** | `25,000.00` | `100,000.00` |
| **`atm.debug.mode`** | `true` (Shows test PIN) | `false` (Hides PIN) |

### Resource Filtering in `pom.xml`
Maven resource filtering replaces `${variable}` placeholders inside `src/main/resources/application.properties` with values from the selected profile during the packaging phase:

```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
    </resource>
</resources>
```

---

## 🚀 Build & Run Instructions

Open terminal / command prompt in the `Day_3` directory:

```bash
cd e:\HCL_Training\Daily_Task\Day_3
```

### 1. Build with Default (`dev`) Profile
```bash
mvn clean package
```
*or explicitly:*
```bash
mvn clean package -Pdev
```

**Run the generated Dev JAR:**
```bash
java -jar target/atm-simulator-1.0.0.jar
```

### 2. Build with `prod` Profile
```bash
mvn clean package -Pprod
```

**Run the generated Prod JAR:**
```bash
java -jar target/atm-simulator-1.0.0.jar
```

---

## 🖥️ Sample Run Demonstration

```text
=======================================================
           WELCOME TO HCL BANK (DEV SANDBOX)           
           Active Profile Environment: DEVELOPMENT          
  [DEV MODE ACTIVE] Default Test PIN: 1234 | Daily Limit: INR 25,000.00
=======================================================

Please insert your card and authenticate.

Attempt [1/3] - Enter 4-digit PIN: 9999
>>> [ERROR] Incorrect PIN. Attempts remaining: 2

Attempt [2/3] - Enter 4-digit PIN: 1234

>>> PIN Verified Successfully! Access Granted.

================== ATM MENU ==================
 1. Balance Enquiry
 2. Cash Deposit
 3. Cash Withdrawal
 4. Mini-Statement
 5. Exit
==============================================
Select an option (1-5): abc
>>> [INVALID ENTRY] Non-numeric input detected. Please enter a valid number (1-5).

================== ATM MENU ==================
 1. Balance Enquiry
 2. Cash Deposit
 3. Cash Withdrawal
 4. Mini-Statement
 5. Exit
==============================================
Select an option (1-5): 2

Enter amount to deposit: 5000
>>> [SUCCESS] Deposited INR 5,000.00 successfully! Updated Balance: INR 20,000.00

================== ATM MENU ==================
 1. Balance Enquiry
 2. Cash Deposit
 3. Cash Withdrawal
 4. Mini-Statement
 5. Exit
==============================================
Select an option (1-5): 3

Enter amount to withdraw (multiples of 100): 250
>>> [INVALID ENTRY] ATM dispenses only 100/500/2000 notes. Amount must be a multiple of 100.

================== ATM MENU ==================
 1. Balance Enquiry
 2. Cash Deposit
 3. Cash Withdrawal
 4. Mini-Statement
 5. Exit
==============================================
Select an option (1-5): 3

Enter amount to withdraw (multiples of 100): 2000
>>> [SUCCESS] Please collect your cash: INR 2,000.00. Remaining Balance: INR 18,000.00

================== ATM MENU ==================
 1. Balance Enquiry
 2. Cash Deposit
 3. Cash Withdrawal
 4. Mini-Statement
 5. Exit
==============================================
Select an option (1-5): 4

================================================================================
                           HCL BANK (DEV SANDBOX) - MINI STATEMENT                         
 Environment: DEVELOPMENT     | Timestamp: 2026-10-07 09:20:00
================================================================================
  • [2026-10-07 09:19:15] Opening Balance                         : +INR 15,000.00
  • [2026-10-07 09:19:35] Cash Deposit                            : +INR 5,000.00
  • [2026-10-07 09:19:50] Cash Withdrawal                         : -INR 2,000.00
--------------------------------------------------------------------------------
 Closing Available Balance                       : INR 18,000.00
================================================================================

================== ATM MENU ==================
 1. Balance Enquiry
 2. Cash Deposit
 3. Cash Withdrawal
 4. Mini-Statement
 5. Exit
==============================================
Select an option (1-5): 5

=======================================================
 Thank you for banking with HCL Bank (Dev Sandbox)!
 Please remember to remove your card.
=======================================================
```
