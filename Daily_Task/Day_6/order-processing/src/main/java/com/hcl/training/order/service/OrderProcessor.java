package com.hcl.training.order.service;

import com.hcl.training.order.exception.InsufficientStockException;
import com.hcl.training.order.exception.InvalidQuantityException;

public class OrderProcessor {
    private int stock;

    public OrderProcessor(int stock) {
        this.stock = stock;
    }

    public void processOrder(int quantity) throws InvalidQuantityException, InsufficientStockException {
        try {
            if (quantity <= 0) {
                throw new InvalidQuantityException("Quantity should be greater than 0");
            }
            if (quantity > stock) {
                throw new InsufficientStockException("Insufficient stock");
            }
            stock -= quantity;
            System.out.println("Order processed successfully. Remaining stock: " + stock);
        } catch (InvalidQuantityException e) {
            throw new RuntimeException("Order failed due to invalid quantity", e);
        } catch (InsufficientStockException e) {
            throw new RuntimeException("Order failed due to insufficient stock", e);
        } finally {
            System.out.println("Order processed completed.");
        }

    }
}
