package com.hcl.training.payment;

/**
 * Main application demonstrating:
 * 1. Abstract Payment hierarchy (CardPayment, UpiPayment, CashPayment)
 * 2. Overloaded pay() method (standard pay vs pay with discount)
 * 3. Refundable interface implementation
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("         DAY 5: PAYMENT HIERARCHY DEMO            ");
        System.out.println("==================================================\n");

        // 1. Card Payment (Supports Payment & Refundable)
        System.out.println("--- 1. Card Payment ---");
        Payment card = new CardPayment(1500.0, "1234567890123456");
        card.pay();                   // standard pay()
        card.pay(200.0);              // overloaded pay(discount)
        if (card instanceof Refundable refundableCard) {
            refundableCard.refund(500.0);
        }
        System.out.println();

        // 2. UPI Payment (Supports Payment & Refundable)
        System.out.println("--- 2. UPI Payment ---");
        Payment upi = new UpiPayment(850.0, "rohit@okaxis");
        upi.pay();                    // standard pay()
        upi.pay(50.0);                // overloaded pay(discount)
        if (upi instanceof Refundable refundableUpi) {
            refundableUpi.refund(100.0);
        }
        System.out.println();

        // 3. Cash Payment (Not Refundable)
        System.out.println("--- 3. Cash Payment ---");
        Payment cash = new CashPayment(500.0);
        cash.pay();                   // standard pay()
        cash.pay(50.0);               // overloaded pay(discount)
        if (cash instanceof Refundable refundableCash) {
            refundableCash.refund(100.0);
        } else {
            System.out.println("Notice: CashPayment does NOT implement Refundable interface.");
        }
        System.out.println("\n==================================================");
    }
}
