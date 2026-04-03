package com.maliksalimov.my_coffee_chat.decorator;

import com.maliksalimov.my_coffee_chat.factory.Coffee;

public abstract class CoffeeDecorator extends Coffee {

    protected final Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee decoratedCoffee) {
        this.decoratedCoffee = decoratedCoffee;
    }

    @Override
    public double getPrice() {
        return decoratedCoffee.getPrice();
    }

    @Override
    public String getName() {
        return decoratedCoffee.getName();
    }
}
