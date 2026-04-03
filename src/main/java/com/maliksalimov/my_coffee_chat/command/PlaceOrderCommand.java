package com.maliksalimov.my_coffee_chat.command;

public class PlaceOrderCommand implements OrderCommand{
    private final String customerName;
    private final String coffeeType;

    public PlaceOrderCommand(String customerName, String coffeeType) {
        this.customerName = customerName;
        this.coffeeType = coffeeType;
    }

    @Override
    public void execute() {
        System.out.println("Order placed: " + customerName + " ordered " + coffeeType);
    }
}
