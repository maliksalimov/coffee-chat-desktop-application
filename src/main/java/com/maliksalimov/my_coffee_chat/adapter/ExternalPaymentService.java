package com.maliksalimov.my_coffee_chat.adapter;

public class ExternalPaymentService {
    public String makeTransaction(String customerName, double amount){
        System.out.println("External payment: " + customerName + " paid $" + amount);
        return "SUCCESS";
    }
}
