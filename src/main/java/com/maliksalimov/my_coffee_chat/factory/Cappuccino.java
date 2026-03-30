package com.maliksalimov.my_coffee_chat.factory;

public class Cappuccino extends Coffee{
    @Override
    public String getName() {
        return "Cappuccino";
    }
    @Override
    public double getPrice() {
        return 3.5;
    }
}
