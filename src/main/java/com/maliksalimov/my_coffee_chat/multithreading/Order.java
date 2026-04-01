package com.maliksalimov.my_coffee_chat.multithreading;

public class Order {
    private final String customerName;
    private final String coffeeType;

    public Order(String customerName, String coffeeType) {
        this.customerName = customerName;
        this.coffeeType = coffeeType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCoffeeType() {
        return coffeeType;
    }

    @Override
    public String toString() {
        return customerName + " -> " + coffeeType;
    }
}
