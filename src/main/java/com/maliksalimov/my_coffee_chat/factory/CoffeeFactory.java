package com.maliksalimov.my_coffee_chat.factory;

import org.springframework.stereotype.Service;

@Service
public class CoffeeFactory {

    public Coffee createCoffee(String coffeeType){
        return switch (coffeeType.toLowerCase()){
            case "espresso" -> new Espresso();
            case "cappuccino" -> new Cappuccino();
            default -> throw new IllegalArgumentException("Invalid coffee type: " + coffeeType);
        };
    }
}
