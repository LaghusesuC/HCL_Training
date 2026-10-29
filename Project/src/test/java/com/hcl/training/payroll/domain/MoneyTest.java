package com.hcl.training.payroll.domain;

import com.hcl.training.payroll.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Money enforces scale 2 and HALF_UP rounding by default (NFR-P1)")
    void testScaleAndRounding() {
        Money m1 = Money.of(100.456);
        assertEquals(new BigDecimal("100.46"), m1.amount());

        Money m2 = Money.of(100.454);
        assertEquals(new BigDecimal("100.45"), m2.amount());

        Money m3 = Money.of("25000.00");
        assertEquals("25000.00", m3.toString());
    }

    @Test
    @DisplayName("Money addition and subtraction calculate accurately")
    void testAddAndSubtract() {
        Money basic = Money.of(50000.00);
        Money hra = Money.of(20000.00);
        Money pf = Money.of(6000.00);

        Money gross = basic.add(hra);
        assertEquals(Money.of(70000.00), gross);

        Money net = gross.subtract(pf);
        assertEquals(Money.of(64000.00), net);
    }

    @Test
    @DisplayName("Money percentage calculation with scale 2 rounding")
    void testPercentage() {
        Money basic = Money.of(45000.00);
        // PF @ 12% of Basic
        Money pf = basic.percentage(12.0);
        assertEquals(Money.of(5400.00), pf);

        // HRA @ 40% of Basic
        Money hra = basic.percentage(40.0);
        assertEquals(Money.of(18000.00), hra);
    }

    @Test
    @DisplayName("Money multiplication and division work with exact scale")
    void testMultiplyAndDivide() {
        Money dailyRate = Money.of(2000.00);
        Money threeDays = dailyRate.multiply(3.0);
        assertEquals(Money.of(6000.00), threeDays);

        Money monthly = Money.of(60000.00);
        Money perDay = monthly.divide(30.0);
        assertEquals(Money.of(2000.00), perDay);

        assertThrows(ArithmeticException.class, () -> monthly.divide(0.0));
    }

    @Test
    @DisplayName("Money comparisons and predicates")
    void testComparisons() {
        Money high = Money.of(1000.00);
        Money low = Money.of(500.00);
        Money zero = Money.ZERO;

        assertTrue(high.isGreaterThan(low));
        assertFalse(low.isGreaterThan(high));
        assertTrue(high.isPositive());
        assertTrue(zero.isZero());
        assertEquals(1, high.compareTo(low));
        assertEquals(0, high.compareTo(Money.of(1000.00)));
    }
}
