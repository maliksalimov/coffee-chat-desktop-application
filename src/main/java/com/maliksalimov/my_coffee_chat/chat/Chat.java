package com.maliksalimov.my_coffee_chat.chat;

import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;

public class Chat {
    private final CoffeeShop coffeeShop;

    public Chat(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    public void sendMessage(String sender, String message){
        DatabaseUtil.saveMessage(sender, message);
        coffeeShop.receiveMessage(message);
    }
}
