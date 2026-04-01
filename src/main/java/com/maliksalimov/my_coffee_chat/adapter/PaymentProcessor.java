package com.maliksalimov.my_coffee_chat.adapter;

public interface PaymentProcessor {
    boolean processPayment(String customerName,double amount);
}
