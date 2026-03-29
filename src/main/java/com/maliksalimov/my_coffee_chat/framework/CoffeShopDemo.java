package com.maliksalimov.my_coffee_chat.framework;

public class CoffeShopDemo implements BusinessObject{
    @OrderHandler
    public void handleOrder(String orderDetails){
        System.out.println("Order received: " + orderDetails);
    }

    @ChatHandler
    public void handleChat(String message){
        System.out.println("Chat message received: " + message);
    }

    @Override
    public void processRequest(String request) {
        System.out.println("Generic request: " + request);
    }
}
