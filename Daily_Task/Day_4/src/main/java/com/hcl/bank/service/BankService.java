package com.hcl.bank.service;

import com.hcl.bank.model.AccountType;
import com.hcl.bank.model.BankAccount;
import com.hcl.bank.model.InsufficientFundsException;
import com.hcl.bank.model.InvalidAmountException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Service layer coordinating banking domain operations, lifecycle management,
 * and multi-account transactions (transfers, audits, reporting).
 */
public class BankService {

    private final Map<String, BankAccount> accountStore = new HashMap<>();

    /**
     * Opens an account using the default constructor chaining tier (name only).
     */
    public BankAccount openAccount(String accountHolderName) {
        BankAccount account = new BankAccount(accountHolderName);
        accountStore.put(account.getAccountNumber(), account);
        return account;
    }

    /**
     * Opens an account using the 2-tier constructor (name and initial balance).
     */
    public BankAccount openAccount(String accountHolderName, double initialBalance) {
        BankAccount account = new BankAccount(accountHolderName, initialBalance);
        accountStore.put(account.getAccountNumber(), account);
        return account;
    }

    /**
     * Opens an account using the canonical constructor (name, balance, and type).
     */
    public BankAccount openAccount(String accountHolderName, double initialBalance, AccountType accountType) {
        BankAccount account = new BankAccount(accountHolderName, initialBalance, accountType);
        accountStore.put(account.getAccountNumber(), account);
        return account;
    }

    /**
     * Registers a pre-existing BankAccount instance.
     */
    public void registerAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Cannot register null account.");
        }
        accountStore.put(account.getAccountNumber(), account);
    }

    /**
     * Retrieves an account by its unique account number.
     */
    public BankAccount getAccount(String accountNumber) {
        BankAccount account = accountStore.get(accountNumber);
        if (account == null) {
            throw new NoSuchElementException("Account not found: " + accountNumber);
        }
        return account;
    }

    /**
     * Deposits money into the specified account.
     */
    public void deposit(String accountNumber, double amount) {
        BankAccount account = getAccount(accountNumber);
        account.deposit(amount);
    }

    /**
     * Withdraws money from the specified account.
     */
    public void withdraw(String accountNumber, double amount) {
        BankAccount account = getAccount(accountNumber);
        account.withdraw(amount);
    }

    /**
     * Transfers money atomically from source account to destination account.
     */
    public void transfer(String sourceAccountNumber, String destinationAccountNumber, double amount) {
        if (amount <= 0.0) {
            throw new InvalidAmountException("Transfer amount must be positive. Provided: " + amount);
        }
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("Source and destination accounts must be distinct.");
        }

        BankAccount source = getAccount(sourceAccountNumber);
        BankAccount destination = getAccount(destinationAccountNumber);

        // Deduct from source first (validates funds)
        source.withdraw(amount);
        // Credit to destination
        destination.deposit(amount);
    }

    /**
     * Returns an unmodifiable collection of all active accounts.
     */
    public Collection<BankAccount> getAllAccounts() {
        return Collections.unmodifiableCollection(accountStore.values());
    }

    /**
     * Calculates the total liquidity / deposits held across all accounts.
     */
    public double getTotalBankLiquidity() {
        return accountStore.values().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    /**
     * Clears all stored accounts (useful for test resets).
     */
    public void clearStore() {
        accountStore.clear();
    }
}
