package com.hcl.training;

import com.hcl.training.order.service.OrderProcessor;

public class Main {
    public static void main(String[] args) {
        OrderProcessor process = new OrderProcessor(10);
        try {
            process.processOrder(5);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        try {
            process.processOrder(-1);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        try {
            process.processOrder(15);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}