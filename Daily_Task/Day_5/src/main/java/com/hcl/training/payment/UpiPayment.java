package com.hcl.training.payment;

/**
 * UpiPayment extending Payment and implementing Refundable.
 */
public class UpiPayment extends Payment implements Refundable {
    private String upiId;

    public UpiPayment(double amount, String upiId) {
        super(amount);
        this.upiId = upiId;
    }

    @Override
    public void pay() {
        System.out.println("Processing UPI Payment of Rs." + amount + " to VPA: " + upiId);
    }

    @Override
    public void refund(double amount) {
        System.out.println("Processing UPI Refund of Rs." + amount + " back to VPA: " + upiId);
    }

    public String getUpiId() {
        return upiId;
    }
}
