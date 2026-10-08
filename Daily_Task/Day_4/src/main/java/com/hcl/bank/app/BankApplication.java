package com.hcl.bank.app;

import com.hcl.bank.model.AccountType;
import com.hcl.bank.model.BankAccount;
import com.hcl.bank.model.InsufficientFundsException;
import com.hcl.bank.model.InvalidAmountException;
import com.hcl.bank.service.BankService;

import java.util.HashSet;
import java.util.Set;

/**
 * Main application entry point for Day 4 Banking System.
 * Demonstrates:
 * <ol>
 *   <li>Static counter & 3-Tier constructor chaining</li>
 *   <li>Equals & HashCode contract with HashSet deduplication</li>
 *   <li>Validated deposit & withdraw operations</li>
 *   <li>Debugging Lab harness for withdraw() conditional breakpoint, watch, and Hot Code Replace</li>
 * </ol>
 */
public class BankApplication {

    public static void main(String[] args) {
        printBanner();

        // 1. Constructor Chaining & Static Counter Demo
        demoConstructorChainingAndStaticCounter();

        // 2. Equals and HashCode Contract Demo
        demoEqualsAndHashCode();

        // 3. Service Layer & Validated Operations Demo
        demoServiceOperations();

        // 4. Debugging Lab Harness for withdraw() planted bug
        runDebuggingLabHarness();
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("           🏛️  HCL CORE BANKING PLATFORM - DAY 4 OOP LAB                      ");
        System.out.println("================================================================================");
    }

    /**
     * Demonstrates how constructors chain to the canonical master constructor
     * and how static counters guarantee unique IDs across instances.
     */
    private static void demoConstructorChainingAndStaticCounter() {
        System.out.println("\n--- [DEMO 1: 3-TIER CONSTRUCTOR CHAINING & STATIC COUNTER] ---");

        // Tier 1: Default constructor -> calls this("Default Customer")
        BankAccount acc1 = new BankAccount();

        // Tier 2: 1-arg constructor -> calls this("Aarav Sharma", 0.0)
        BankAccount acc2 = new BankAccount("Aarav Sharma");

        // Tier 3: 2-arg constructor -> calls this("Diya Patel", 5000.0, AccountType.SAVINGS)
        BankAccount acc3 = new BankAccount("Diya Patel", 5000.0);

        // Canonical constructor: 3-arg (explicit AccountType)
        BankAccount acc4 = new BankAccount("Rohan Verma", 15000.0, AccountType.SALARY);

        System.out.println("Account 1 (Tier 1 Default)   : " + acc1);
        System.out.println("Account 2 (Tier 2 1-Param)   : " + acc2);
        System.out.println("Account 3 (Tier 3 2-Param)   : " + acc3);
        System.out.println("Account 4 (Canonical 3-Param): " + acc4);

        System.out.printf("%n[Static Counter Check]%n");
        System.out.printf("Total BankAccount objects instantiated: %d%n", BankAccount.getTotalAccountsCreated());
        System.out.printf("Last Assigned Account Counter Number   : %d%n", BankAccount.getLastAssignedCounter());
    }

    /**
     * Demonstrates proper equals() and hashCode() contract implementation.
     */
    private static void demoEqualsAndHashCode() {
        System.out.println("\n--- [DEMO 2: EQUALS & HASHCODE CONTRACT] ---");

        BankAccount original = new BankAccount("Kavya Rao", 8000.0, AccountType.SAVINGS);

        // Simulated duplicate referencing same accountNumber
        // (In real apps, reconstructed from persistence layer or external lookup)
        System.out.println("Original Account: " + original);
        System.out.printf("Testing Reflexivity: original.equals(original) -> %b%n", original.equals(original));
        System.out.printf("Testing Non-nullity: original.equals(null)     -> %b%n", original.equals(null));

        // Testing inside a HashSet to verify hashCode uniqueness and deduplication
        Set<BankAccount> accountSet = new HashSet<>();
        accountSet.add(original);
        boolean reAddResult = accountSet.add(original); // Should be false because it already exists

        System.out.printf("HashSet Size after re-adding same entity: %d (Duplicate ignored: %b)%n",
                accountSet.size(), !reAddResult);
        System.out.printf("Original hashCode: %d%n", original.hashCode());
    }

    /**
     * Demonstrates service layer integration and validated operations.
     */
    private static void demoServiceOperations() {
        System.out.println("\n--- [DEMO 3: SERVICE LAYER & VALIDATED TRANSACTIONS] ---");
        BankService service = new BankService();

        BankAccount alice = service.openAccount("Alice Walker", 3000.0, AccountType.SAVINGS);
        BankAccount bob = service.openAccount("Bob Dylan", 1500.0, AccountType.CURRENT);

        System.out.println("Before Transfer:");
        System.out.println(" Alice: " + alice);
        System.out.println(" Bob  : " + bob);

        System.out.println("\nPerforming ₹1000 transfer from Alice to Bob...");
        service.transfer(alice.getAccountNumber(), bob.getAccountNumber(), 1000.0);

        System.out.println("After Transfer:");
        System.out.println(" Alice: " + alice);
        System.out.println(" Bob  : " + bob);

        // Validation Test: Negative deposit rejection
        System.out.println("\nAttempting invalid deposit (-₹500)...");
        try {
            alice.deposit(-500.0);
        } catch (InvalidAmountException ex) {
            System.out.println("  [Expected Validation Caught]: " + ex.getMessage());
        }
    }

    /**
     * Demonstrates the planted bug in withdraw() and provides the step-by-step
     * harness for Conditional Breakpoint, Watch Expressions, and Hot Code Replace.
     */
    public static void runDebuggingLabHarness() {
        System.out.println("\n================================================================================");
        System.out.println("   🐛 [DEMO 4: DAY 4 DEBUGGING LAB - CONDITIONAL BREAKPOINT & HOT CODE REPLACE] ");
        System.out.println("================================================================================");

        BankAccount testAccount = new BankAccount("Lab Tester", 1000.0, AccountType.SAVINGS);
        System.out.println("Initial State: " + testAccount);
        System.out.printf("PLANTED_BUG_ACTIVE flag currently is: [%b]%n%n", BankAccount.PLANTED_BUG_ACTIVE);

        double[] withdrawalQueue = { 200.0, 300.0, 1500.0, 100.0 };

        System.out.println("Processing batch of 4 withdrawals on initial balance ₹1000.00:");
        System.out.println("Tx 1: ₹200.00  -> Expected Balance: ₹800.00");
        System.out.println("Tx 2: ₹300.00  -> Expected Balance: ₹500.00");
        System.out.println("Tx 3: ₹1500.00 -> ⚠️ Overdraft Alert! Balance is ₹500, requested ₹1500!");
        System.out.println("Tx 4: ₹100.00  -> Dependent on previous step");
        System.out.println("--------------------------------------------------------------------------------");

        for (int i = 0; i < withdrawalQueue.length; i++) {
            double amount = withdrawalQueue[i];
            System.out.printf("%n[Processing Tx #%d]: Requesting withdrawal of ₹%.2f...%n", (i + 1), amount);

            try {
                // Line inside withdraw() is our breakpoint candidate!
                testAccount.withdraw(amount);
                System.out.printf("  -> Tx SUCCESS! Current balance: ₹%.2f%n", testAccount.getBalance());

                if (testAccount.getBalance() < 0.0) {
                    System.out.println("  🚨 [BUG DETECTED]: Account has a NEGATIVE balance! Overdraft check failed!");
                }
            } catch (InsufficientFundsException ex) {
                System.out.println("  🛡️ [SAFEGUARD TRIGGERED]: " + ex.getMessage());
            } catch (InvalidAmountException ex) {
                System.out.println("  🛡️ [INVALID AMOUNT]: " + ex.getMessage());
            }
        }

        System.out.println("\nFinal Account State: " + testAccount);
        System.out.println("================================================================================");
    }
}
