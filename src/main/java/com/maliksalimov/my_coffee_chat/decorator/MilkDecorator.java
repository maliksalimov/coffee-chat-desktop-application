package com.maliksalimov.my_coffee_chat.decorator;

import com.maliksalimov.my_coffee_chat.factory.Coffee;

public class MilkDecorator extends CoffeeDecorator{

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 0.5;
    }

    @Override
    public String getName() {
        return super.getName() + ", Milk";
    }
}
