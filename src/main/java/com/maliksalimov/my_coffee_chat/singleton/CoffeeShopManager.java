package com.maliksalimov.my_coffee_chat.singleton;

import org.springframework.stereotype.Component;

@Component
public class CoffeeShopManager {

    private int totalOrderProcessed = 0;

    public void recordOrder(){
        totalOrderProcessed++;
    }

    public int getTotalOrderProcessed() {
        return totalOrderProcessed;
    }
}
