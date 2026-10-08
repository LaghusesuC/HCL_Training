package com.hcl.bank.app;

import com.hcl.bank.model.AccountType;
import com.hcl.bank.model.BankAccount;
import com.hcl.bank.model.InsufficientFundsException;
import com.hcl.bank.model.InvalidAmountException;

/**
 * Interactive Debugging Lab Runner.
 * Specifically configured for hands-on practice with:
 * 1. Setting a Conditional Breakpoint in BankAccount.withdraw()
 * 2. Adding Watch Expressions to monitor variable states
 * 3. Performing Hot Code Replace (HCR) to fix the bug live during debug session
 */
public class BankDebugLab {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("             🧪 HCL DAY 4 INTERACTIVE DEBUGGING LAB RUNNER                    ");
        System.out.println("================================================================================");
        System.out.println("Follow these steps to complete the exercise in your IDE (VS Code / Eclipse):");
        System.out.println(" 1. Open 'BankAccount.java'.");
        System.out.println(" 2. Locate the withdraw(double amount) method.");
        System.out.println(" 3. Right-click the left gutter on the line:");
        System.out.println("      'this.balance -= amount;'");
        System.out.println("    Select: 'Add Conditional Breakpoint...'");
        System.out.println("    Enter expression: amount > this.balance");
        System.out.println(" 4. In the Debug Side Panel, add these WATCH expressions:");
        System.out.println("      - amount");
        System.out.println("      - this.balance");
        System.out.println("      - amount > this.balance");
        System.out.println("      - this.balance - amount");
        System.out.println(" 5. Start Debugging this class (F5).");
        System.out.println(" 6. Observe that Tx #1 and Tx #2 will NOT pause execution.");
        System.out.println("    Execution will PAUSE ONLY on Tx #3 (withdrawal of ₹1500.00).");
        System.out.println(" 7. Perform Hot Code Replace (HCR):");
        System.out.println("    - While paused, edit BankAccount.java: change PLANTED_BUG_ACTIVE = false");
        System.out.println("      (or replace the line with proper balance check).");
        System.out.println("    - Press Ctrl+S (Save). Notice the IDE reloads the class.");
        System.out.println("    - Press F5 (Continue). The transaction will be safely intercepted!");
        System.out.println("================================================================================\n");

        BankAccount account = new BankAccount("Vikram Malhotra", 1000.0, AccountType.SAVINGS);
        System.out.println("Account Opened: " + account);
        System.out.println("Starting automated transaction pipeline...\n");

        double[] transactions = { 200.0, 300.0, 1500.0, 250.0 };

        for (int i = 0; i < transactions.length; i++) {
            double reqAmount = transactions[i];
            System.out.printf("[Tx #%d] Attempting withdrawal of ₹%.2f from Balance ₹%.2f...%n",
                    (i + 1), reqAmount, account.getBalance());

            try {
                // Method call containing the planted bug
                account.withdraw(reqAmount);
                System.out.printf("   [Result]: Approved. New Balance: ₹%.2f%n", account.getBalance());

                if (account.getBalance() < 0.0) {
                    System.err.printf("   [CRITICAL BUG]: Account dropped to illegal balance: ₹%.2f%n", account.getBalance());
                }
            } catch (InsufficientFundsException ex) {
                System.out.printf("   [Security Check Passed]: %s%n", ex.getMessage());
            } catch (InvalidAmountException ex) {
                System.out.printf("   [Invalid Input Caught]: %s%n", ex.getMessage());
            }

            // Small delay to allow observation in live logs
            try {
                Thread.sleep(800);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("Pipeline finished. Final Account Summary: " + account);
        System.out.println("================================================================================");
    }
}
