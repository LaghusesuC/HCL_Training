package com.hcl.training.payment;

/**
 * CardPayment extending Payment and implementing Refundable.
 */
public class CardPayment extends Payment implements Refundable {
    private String cardNumber;

    public CardPayment(double amount, String cardNumber) {
        super(amount);
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay() {
        String masked = (cardNumber != null && cardNumber.length() >= 4)
                ? "**** " + cardNumber.substring(cardNumber.length() - 4)
                : "****";
        System.out.println("Processing Card Payment of Rs." + amount + " via Card: " + masked);
    }

    @Override
    public void refund(double amount) {
        System.out.println("Processing Card Refund of Rs." + amount + " to Card ending in "
                + cardNumber.substring(cardNumber.length() - 4));
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
