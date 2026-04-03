package com.maliksalimov.my_coffee_chat.command;

public class CancelOrderCommand implements OrderCommand{

    private final String customerName;

    public CancelOrderCommand(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void execute() {
        System.out.println("Order cancelled: " + customerName);
    }
}
