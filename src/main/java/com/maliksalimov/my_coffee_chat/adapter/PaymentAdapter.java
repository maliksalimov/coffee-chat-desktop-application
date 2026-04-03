package com.maliksalimov.my_coffee_chat.adapter;

import org.springframework.stereotype.Component;

@Component
public class PaymentAdapter implements PaymentProcessor{

    private final ExternalPaymentService externalPaymentService = new ExternalPaymentService();

    @Override
    public boolean processPayment(String customerName, double amount) {
        String result = externalPaymentService.makeTransaction(customerName, amount);
        return "SUCCESS".equals(result);
    }
}
