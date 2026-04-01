package com.maliksalimov.my_coffee_chat.adapter;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentProcessor paymentProcessor;

    public PaymentService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public boolean processPayment(String customerName, double amount){
        return paymentProcessor.processPayment(customerName, amount);
    }
}
