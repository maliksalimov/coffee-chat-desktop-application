package com.maliksalimov.my_coffee_chat.factory;

public class Espresso extends Coffee{
    @Override
    public String getName() {
        return "Espresso";
    }

    @Override
    public double getPrice() {
        return 2.5;
    }
}
