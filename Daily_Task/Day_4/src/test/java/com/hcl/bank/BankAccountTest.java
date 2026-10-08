package com.hcl.bank;

import com.hcl.bank.model.AccountType;
import com.hcl.bank.model.BankAccount;
import com.hcl.bank.model.InsufficientFundsException;
import com.hcl.bank.model.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Day 4 BankAccount Unit Tests")
class BankAccountTest {

    @BeforeEach
    void setUp() {
        // Ensure clean test isolation
        BankAccount.PLANTED_BUG_ACTIVE = false; // Run unit tests with verified fixed logic
    }

    @Test
    @DisplayName("Should increment static counter and format account numbers sequentially")
    void testStaticCounterAndSequentialAccountNumbers() {
        BankAccount.resetCounterForTesting(5000);

        BankAccount acc1 = new BankAccount("User One", 100.0);
        BankAccount acc2 = new BankAccount("User Two", 200.0);

        assertEquals("HCL-5001", acc1.getAccountNumber());
        assertEquals("HCL-5002", acc2.getAccountNumber());
        assertEquals(2, BankAccount.getTotalAccountsCreated());
        assertEquals(5002, BankAccount.getLastAssignedCounter());
    }

    @Test
    @DisplayName("Should properly chain constructors across all 3 tiers")
    void testConstructorChaining() {
        BankAccount defaultAcc = new BankAccount();
        assertEquals("Default Customer", defaultAcc.getAccountHolderName());
        assertEquals(0.0, defaultAcc.getBalance());
        assertEquals(AccountType.SAVINGS, defaultAcc.getAccountType());

        BankAccount oneParamAcc = new BankAccount("Priya Sharma");
        assertEquals("Priya Sharma", oneParamAcc.getAccountHolderName());
        assertEquals(0.0, oneParamAcc.getBalance());
        assertEquals(AccountType.SAVINGS, oneParamAcc.getAccountType());

        BankAccount twoParamAcc = new BankAccount("Karan Johar", 2500.0);
        assertEquals("Karan Johar", twoParamAcc.getAccountHolderName());
        assertEquals(2500.0, twoParamAcc.getBalance());
        assertEquals(AccountType.SAVINGS, twoParamAcc.getAccountType());
    }

    @Test
    @DisplayName("Should throw exception when initial balance is negative or name is blank")
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount(""));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount(null));
        assertThrows(InvalidAmountException.class, () -> new BankAccount("Valid Name", -10.0));
    }

    @Test
    @DisplayName("Should validate deposit operations")
    void testDepositValidation() {
        BankAccount acc = new BankAccount("Test Holder", 500.0);
        acc.deposit(250.0);
        assertEquals(750.0, acc.getBalance());

        assertThrows(InvalidAmountException.class, () -> acc.deposit(0.0));
        assertThrows(InvalidAmountException.class, () -> acc.deposit(-100.0));
    }

    @Test
    @DisplayName("Should validate withdraw operations and reject overdraft when fixed")
    void testWithdrawValidation() {
        BankAccount acc = new BankAccount("Test Holder", 500.0);
        acc.withdraw(200.0);
        assertEquals(300.0, acc.getBalance());

        // Zero or negative withdrawal
        assertThrows(InvalidAmountException.class, () -> acc.withdraw(0.0));
        assertThrows(InvalidAmountException.class, () -> acc.withdraw(-50.0));

        // Overdraft attempt
        assertThrows(InsufficientFundsException.class, () -> acc.withdraw(400.0));
        assertEquals(300.0, acc.getBalance()); // Balance must remain untouched
    }

    @Test
    @DisplayName("Should adhere to equals and hashCode contract")
    void testEqualsAndHashCodeContract() {
        BankAccount acc1 = new BankAccount("User X", 1000.0);
        BankAccount acc2 = new BankAccount("User Y", 2000.0);

        // Reflexivity
        assertEquals(acc1, acc1);
        assertEquals(acc1.hashCode(), acc1.hashCode());

        // Non-nullity
        assertNotEquals(null, acc1);

        // Distinct accounts must not be equal
        assertNotEquals(acc1, acc2);

        // Deduplication in Set
        Set<BankAccount> set = new HashSet<>();
        set.add(acc1);
        set.add(acc1);
        assertEquals(1, set.size());
    }
}
