package com.hcl.atm;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Day 3 Task: ATM Simulator
 * 
 * Demonstrates:
 * 1. PIN verification with maximum 3 attempts and break statement.
 * 2. Menu navigation using do-while loop and switch-case construct.
 * 3. Robust input validation with continue statement on invalid entry.
 * 4. Enhanced-for (for-each) loop to render a detailed mini-statement.
 * 5. Dynamic configuration loaded from Maven profile-filtered application.properties.
 */
public class AtmSimulator {

    // Default configuration (fallback if properties not found or unexpanded)
    private static final String DEFAULT_PIN = "1234";
    private static String bankName = "HCL National Bank";
    private static String environment = "DEVELOPMENT";
    private static double dailyLimit = 25000.00;
    private static String currency = "INR";
    private static boolean debugMode = true;

    // Account state
    private static double balance = 15000.00;
    private static final List<String> miniStatement = new ArrayList<>();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Load properties injected via Maven dev/prod profile
        loadConfiguration();

        Scanner scanner = new Scanner(System.in);

        // Display Header Banner
        printBanner();

        // ---------------------------------------------------------------------
        // Requirement 1: 3 PIN Attempts with 'break'
        // ---------------------------------------------------------------------
        final int MAX_PIN_ATTEMPTS = 3;
        boolean isAuthenticated = false;

        System.out.println("Please insert your card and authenticate.\n");

        for (int attempt = 1; attempt <= MAX_PIN_ATTEMPTS; attempt++) {
            System.out.printf("Attempt [%d/%d] - Enter 4-digit PIN: ", attempt, MAX_PIN_ATTEMPTS);
            String inputPin = scanner.nextLine().trim();

            if (DEFAULT_PIN.equals(inputPin)) {
                isAuthenticated = true;
                System.out.println("\n>>> PIN Verified Successfully! Access Granted.\n");
                break; // Break out of PIN verification loop on success
            } else {
                int attemptsRemaining = MAX_PIN_ATTEMPTS - attempt;
                if (attemptsRemaining > 0) {
                    System.out.printf(">>> [ERROR] Incorrect PIN. Attempts remaining: %d%n%n", attemptsRemaining);
                } else {
                    System.out.println("\n>>> [SECURITY ALERT] 3 incorrect PIN attempts! Your card has been blocked.");
                    break; // Break out of loop when maximum attempts are exhausted
                }
            }
        }

        if (!isAuthenticated) {
            System.out.println("Transaction terminated. Please contact your nearest bank branch.");
            scanner.close();
            return;
        }

        // Initialize transaction history with opening balance
        recordTransaction(String.format("Opening Balance                         : +%s %,.2f", currency, balance));

        // ---------------------------------------------------------------------
        // Requirement 2 & 3: do-while menu + switch + continue on invalid entry
        // ---------------------------------------------------------------------
        int choice = 0;
        do {
            printMenu();
            System.out.print("Select an option (1-5): ");
            String input = scanner.nextLine().trim();

            // Input Validation: Check for empty input or non-numeric values
            if (input.isEmpty()) {
                System.out.println(">>> [INVALID ENTRY] Choice cannot be empty. Please try again.\n");
                continue; // Continue directly to next iteration
            }

            int parsedChoice;
            try {
                parsedChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">>> [INVALID ENTRY] Non-numeric input detected. Please enter a valid number (1-5).\n");
                continue; // Continue directly to next iteration
            }

            // Input Validation: Check range (1 - 5)
            if (parsedChoice < 1 || parsedChoice > 5) {
                System.out.println(">>> [INVALID ENTRY] Choice out of bounds. Please select between 1 and 5.\n");
                continue; // Continue directly to next iteration
            }

            choice = parsedChoice;

            // Switch statement to handle valid menu options
            switch (choice) {
                case 1:
                    // Check Balance
                    System.out.println("\n----------------------------------------");
                    System.out.println("             ACCOUNT BALANCE            ");
                    System.out.println("----------------------------------------");
                    System.out.printf("Available Balance : %s %,.2f%n", currency, balance);
                    System.out.printf("Daily Limit       : %s %,.2f%n", currency, dailyLimit);
                    System.out.println("----------------------------------------\n");
                    break;

                case 2:
                    // Cash Deposit
                    System.out.print("\nEnter amount to deposit: ");
                    String depositInput = scanner.nextLine().trim();
                    double depositAmount;

                    try {
                        depositAmount = Double.parseDouble(depositInput);
                    } catch (NumberFormatException e) {
                        System.out.println(">>> [INVALID ENTRY] Invalid deposit amount. Numbers only.\n");
                        continue; // Continue back to main menu
                    }

                    if (depositAmount <= 0) {
                        System.out.println(">>> [INVALID ENTRY] Deposit amount must be strictly greater than 0.\n");
                        continue; // Continue back to main menu
                    }

                    balance += depositAmount;
                    recordTransaction(String.format("Cash Deposit                            : +%s %,.2f", currency, depositAmount));
                    System.out.printf(">>> [SUCCESS] Deposited %s %,.2f successfully! Updated Balance: %s %,.2f%n%n",
                            currency, depositAmount, currency, balance);
                    break;

                case 3:
                    // Cash Withdrawal
                    System.out.print("\nEnter amount to withdraw (multiples of 100): ");
                    String withdrawInput = scanner.nextLine().trim();
                    double withdrawAmount;

                    try {
                        withdrawAmount = Double.parseDouble(withdrawInput);
                    } catch (NumberFormatException e) {
                        System.out.println(">>> [INVALID ENTRY] Invalid withdrawal amount. Numbers only.\n");
                        continue; // Continue back to main menu
                    }

                    if (withdrawAmount <= 0) {
                        System.out.println(">>> [INVALID ENTRY] Withdrawal amount must be greater than zero.\n");
                        continue; // Continue back to main menu
                    }

                    if (withdrawAmount % 100 != 0) {
                        System.out.println(">>> [INVALID ENTRY] ATM dispenses only 100/500/2000 notes. Amount must be a multiple of 100.\n");
                        continue; // Continue back to main menu
                    }

                    if (withdrawAmount > dailyLimit) {
                        System.out.printf(">>> [LIMIT EXCEEDED] Amount exceeds profile daily withdrawal limit of %s %,.2f.%n%n",
                                currency, dailyLimit);
                        continue; // Continue back to main menu
                    }

                    if (withdrawAmount > balance) {
                        System.out.printf(">>> [INSUFFICIENT FUNDS] Current balance is only %s %,.2f.%n%n", currency, balance);
                        continue; // Continue back to main menu
                    }

                    balance -= withdrawAmount;
                    recordTransaction(String.format("Cash Withdrawal                         : -%s %,.2f", currency, withdrawAmount));
                    System.out.printf(">>> [SUCCESS] Please collect your cash: %s %,.2f. Remaining Balance: %s %,.2f%n%n",
                            currency, withdrawAmount, currency, balance);
                    break;

                case 4:
                    // ---------------------------------------------------------
                    // Requirement 4: Enhanced-for loop for Mini-Statement
                    // ---------------------------------------------------------
                    printMiniStatement();
                    break;

                case 5:
                    // Exit
                    System.out.println("\n=======================================================");
                    System.out.printf(" Thank you for banking with %s!%n", bankName);
                    System.out.println(" Please remember to remove your card.");
                    System.out.println("=======================================================\n");
                    break;

                default:
                    // Fallback branch
                    System.out.println(">>> [ERROR] Unexpected option encountered.\n");
                    break;
            }

        } while (choice != 5);

        scanner.close();
    }

    /**
     * Requirement 4: Displays the mini-statement using an enhanced-for loop.
     */
    private static void printMiniStatement() {
        System.out.println("\n================================================================================");
        System.out.printf("                           %s - MINI STATEMENT                         %n", bankName.toUpperCase());
        System.out.printf(" Environment: %-15s | Timestamp: %s%n", environment, LocalDateTime.now().format(TIME_FORMATTER));
        System.out.println("================================================================================");

        if (miniStatement.isEmpty()) {
            System.out.println(" No transactions recorded in this session.");
        } else {
            // Enhanced-for (for-each) loop iterating over transaction records
            for (String entry : miniStatement) {
                System.out.println("  • " + entry);
            }
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(" Closing Available Balance                       : %s %,.2f%n", currency, balance);
        System.out.println("================================================================================\n");
    }

    private static void recordTransaction(String detail) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        miniStatement.add(String.format("[%s] %s", timestamp, detail));
    }

    private static void printBanner() {
        System.out.println("=======================================================");
        System.out.printf("           WELCOME TO %s           %n", bankName.toUpperCase());
        System.out.printf("           Active Profile Environment: %s          %n", environment);
        if (debugMode) {
            System.out.printf("  [DEV MODE ACTIVE] Default Test PIN: %s | Daily Limit: %s %,.2f%n",
                    DEFAULT_PIN, currency, dailyLimit);
        }
        System.out.println("=======================================================\n");
    }

    private static void printMenu() {
        System.out.println("================== ATM MENU ==================");
        System.out.println(" 1. Balance Enquiry");
        System.out.println(" 2. Cash Deposit");
        System.out.println(" 3. Cash Withdrawal");
        System.out.println(" 4. Mini-Statement");
        System.out.println(" 5. Exit");
        System.out.println("==============================================");
    }

    /**
     * Loads dynamic configuration properties filtered by Maven profiles.
     */
    private static void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream is = AtmSimulator.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);

                String env = properties.getProperty("app.env");
                if (env != null && !env.contains("${")) {
                    environment = env;
                }

                String bName = properties.getProperty("atm.bank.name");
                if (bName != null && !bName.contains("${")) {
                    bankName = bName;
                }

                String limitStr = properties.getProperty("atm.daily.limit");
                if (limitStr != null && !limitStr.contains("${")) {
                    try {
                        dailyLimit = Double.parseDouble(limitStr);
                    } catch (NumberFormatException ignored) {}
                }

                String curr = properties.getProperty("atm.currency");
                if (curr != null && !curr.contains("${")) {
                    currency = curr;
                }

                String debugStr = properties.getProperty("atm.debug.mode");
                if (debugStr != null && !debugStr.contains("${")) {
                    debugMode = Boolean.parseBoolean(debugStr);
                }
            }
        } catch (Exception e) {
            System.err.println("[WARN] Could not load application.properties; using default dev settings.");
        }
    }
}
