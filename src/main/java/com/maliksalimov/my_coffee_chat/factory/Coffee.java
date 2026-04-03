package com.maliksalimov.my_coffee_chat.factory;

public abstract class Coffee {
    public abstract String getName();
    public abstract double getPrice();

    @Override
    public String toString() {
        return getName() + " - $" + getPrice();
    }
}
