package com.maliksalimov.my_coffee_chat.framework;

import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;

public class BusinessTestClient {
    public static void main(String[] args) {
        InteractionHandler interactionHandler = new InteractionHandler();
        CoffeeShop coffeeShop = CoffeeShop.getInstance();

        System.out.println("--- Order Request ---");
        interactionHandler.handleInteraction(coffeeShop, "order", "1 cappuccino");

        System.out.println("--- Chat Request ---");
        interactionHandler.handleInteraction(coffeeShop, "chat", "Hello, Barista!");

        System.out.println("--- Unknown Request ---");
        interactionHandler.handleInteraction(coffeeShop, "feedback", "Great service");

        System.out.println("--- Reflection direct invocation ---");
        ReflectionUtil.invokeMethod(coffeeShop, "handleOrder", "2 lattes");
    }
}
