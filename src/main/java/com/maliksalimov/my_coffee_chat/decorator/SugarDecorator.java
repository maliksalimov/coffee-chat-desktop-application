package com.maliksalimov.my_coffee_chat.decorator;

import com.maliksalimov.my_coffee_chat.factory.Coffee;

public class SugarDecorator extends CoffeeDecorator{

    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 0.25;
    }

    @Override
    public String getName() {
        return super.getName() + ", Sugar";
    }
}
