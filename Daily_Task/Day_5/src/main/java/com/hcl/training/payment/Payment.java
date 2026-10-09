package com.hcl.training.payment;

/**
 * Abstract Payment class with overloaded pay() methods.
 */
public abstract class Payment {
    protected double amount;

    public Payment(double amount) {
        this.amount = amount;
    }

    // Abstract pay method to be implemented by subclasses
    public abstract void pay();

    // Overloaded pay method with discount
    public void pay(double discount) {
        if (discount > 0 && discount <= amount) {
            System.out.println("Applying discount of " + discount + " on " + amount);
            double discountedAmount = amount - discount;
            double previousAmount = this.amount;
            this.amount = discountedAmount;
            pay();
            this.amount = previousAmount; // restore base amount
        } else {
            System.out.println("Invalid discount amount: " + discount);
            pay();
        }
    }

    public double getAmount() {
        return amount;
    }
}
