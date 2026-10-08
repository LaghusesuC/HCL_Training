package com.hcl.bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * BankAccount model class demonstrating:
 * <ul>
 *   <li>Encapsulation with private fields and controlled getters/setters</li>
 *   <li>Static counter tracking total accounts and generating unique account numbers</li>
 *   <li>3-Tier Constructor Chaining using the 'this(...)' keyword</li>
 *   <li>Validated business operations: deposit() and withdraw()</li>
 *   <li>Strict equals() and hashCode() contract implementation</li>
 * </ul>
 */
public class BankAccount {

    // =========================================================================
    // Static Fields (Class-level State)
    // =========================================================================

    /** Static sequence counter used to auto-generate unique account numbers */
    private static int accountCounter = 1000;

    /** Static counter tracking total accounts instantiated */
    private static int totalAccountsCreated = 0;

    /**
     * Flag toggling the planted bug for the Day 4 Debugging Lab.
     * When set to true, withdraw() contains a logical flaw (missing balance validation / allows overdraft).
     * In the debugging lab, students use a Conditional Breakpoint + Watch and fix it via Hot Code Replace.
     */
    public static boolean PLANTED_BUG_ACTIVE = true;

    // =========================================================================
    // Instance Fields (Object-level Encapsulated State)
    // =========================================================================

    private final String accountNumber;
    private String accountHolderName;
    private double balance;
    private AccountType accountType;
    private final LocalDateTime createdAt;

    // =========================================================================
    // Constructor Chaining (3-Tier Chaining via this(...))
    // =========================================================================

    /**
     * Tier 1: Default / No-Argument Constructor.
     * Chains to Tier 2 with a default placeholder holder name.
     */
    public BankAccount() {
        this("Default Customer");
    }

    /**
     * Tier 2: Single-Argument Constructor.
     * Chains to Tier 3 with an initial zero balance.
     *
     * @param accountHolderName the name of the account holder
     */
    public BankAccount(String accountHolderName) {
        this(accountHolderName, 0.0);
    }

    /**
     * Tier 3: Two-Argument Constructor.
     * Chains to the Canonical Constructor with the default AccountType (SAVINGS).
     *
     * @param accountHolderName the name of the account holder
     * @param initialBalance    the starting balance
     */
    public BankAccount(String accountHolderName, double initialBalance) {
        this(accountHolderName, initialBalance, AccountType.SAVINGS);
    }

    /**
     * Canonical / Master Constructor.
     * Performs validation, increments static counters, generates the unique
     * account number, and initializes all instance variables.
     *
     * @param accountHolderName the name of the account holder
     * @param initialBalance    the starting balance
     * @param accountType       the account type (SAVINGS, CURRENT, SALARY)
     */
    public BankAccount(String accountHolderName, double initialBalance, AccountType accountType) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or blank.");
        }
        if (initialBalance < 0.0) {
            throw new InvalidAmountException("Initial balance cannot be negative: " + initialBalance);
        }

        // Static counter manipulation: thread-safe or sequential unique ID assignment
        accountCounter++;
        totalAccountsCreated++;

        this.accountNumber = String.format("HCL-%04d", accountCounter);
        this.accountHolderName = accountHolderName.trim();
        this.balance = initialBalance;
        this.accountType = (accountType != null) ? accountType : AccountType.SAVINGS;
        this.createdAt = LocalDateTime.now();
    }

    // =========================================================================
    // Business Operations & Input Validation
    // =========================================================================

    /**
     * Deposits money into the account after validating positive amount.
     *
     * @param amount the deposit amount (must be > 0)
     */
    public void deposit(double amount) {
        if (amount <= 0.0) {
            throw new InvalidAmountException(
                String.format("Deposit amount must be strictly positive. Attempted: %.2f", amount)
            );
        }
        this.balance += amount;
    }

    /**
     * Withdraws money from the account after validating positive amount and sufficient balance.
     *
     * =========================================================================
     * 🐛 DAY 4 DEBUGGING LAB - PLANTED BUG TARGET
     * =========================================================================
     * When PLANTED_BUG_ACTIVE is true, the sufficiency check is skipped or flawed,
     * allowing the balance to become negative (illegal overdraft).
     *
     * Lab Goal:
     * 1. Set a CONDITIONAL BREAKPOINT: condition -> amount > this.balance (or this.balance - amount < 0)
     * 2. Add WATCH expressions:
     *      - amount
     *      - this.balance
     *      - amount > this.balance
     *      - this.balance - amount
     * 3. When the debugger halts on the offending transaction, fix the logic
     *    and execute HOT CODE REPLACE (Ctrl+S / Save in IDE) without stopping JVM.
     * =========================================================================
     *
     * @param amount the withdrawal amount (must be > 0 and <= balance)
     */
    public void withdraw(double amount) {
        // Step 1: Validate positive amount
        if (amount <= 0.0) {
            throw new InvalidAmountException(
                String.format("Withdrawal amount must be strictly positive. Attempted: %.2f", amount)
            );
        }

        // ---------------------------------------------------------------------
        // [PLANTED BUG]: Faulty logic when PLANTED_BUG_ACTIVE is enabled
        // ---------------------------------------------------------------------
        if (PLANTED_BUG_ACTIVE) {
            // BUG: Deducts directly without verifying if funds are sufficient!
            // Line where you place the conditional breakpoint:
            this.balance -= amount; // <-- BREAKPOINT HERE (Condition: amount > this.balance)
            return;
        }

        // ---------------------------------------------------------------------
        // [FIXED LOGIC]: Proper validation check preventing overdraft
        // ---------------------------------------------------------------------
        if (amount > this.balance) {
            throw new InsufficientFundsException(
                String.format("Insufficient funds in account [%s]! Available: ₹%.2f, Requested: ₹%.2f",
                    this.accountNumber, this.balance, amount)
            );
        }

        this.balance -= amount;
    }

    // =========================================================================
    // Identity, Equality & Hashing Contract
    // =========================================================================

    /**
     * Evaluates equality based strictly on the unique business identity (accountNumber).
     * Follows the 5 properties of Object.equals contract:
     * 1. Reflexive: x.equals(x) is true
     * 2. Symmetric: x.equals(y) == y.equals(x)
     * 3. Transitive: x.equals(y) && y.equals(z) => x.equals(z)
     * 4. Consistent: multiple invocations yield identical result
     * 5. Non-nullity: x.equals(null) is false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(this.accountNumber, that.accountNumber);
    }

    /**
     * Hash code consistent with equals().
     * Must return the same integer for two objects where equals() returns true.
     */
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    // =========================================================================
    // Getters and Controlled Setters
    // =========================================================================

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        this.accountHolderName = accountHolderName.trim();
    }

    public double getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        if (accountType != null) {
            this.accountType = accountType;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =========================================================================
    // Static Utility Accessors
    // =========================================================================

    public static int getTotalAccountsCreated() {
        return totalAccountsCreated;
    }

    public static int getLastAssignedCounter() {
        return accountCounter;
    }

    /**
     * Resets counter (primarily for test isolation).
     */
    public static void resetCounterForTesting(int startingCounter) {
        accountCounter = startingCounter;
        totalAccountsCreated = 0;
    }

    // =========================================================================
    // String Representation
    // =========================================================================

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("BankAccount[No=%s, Holder='%s', Balance=₹%.2f, Type=%s, Created=%s]",
            accountNumber, accountHolderName, balance, accountType, createdAt.format(dtf));
    }
}
