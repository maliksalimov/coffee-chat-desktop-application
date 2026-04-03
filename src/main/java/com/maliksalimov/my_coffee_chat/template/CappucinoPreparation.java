package com.maliksalimov.my_coffee_chat.template;

import org.springframework.stereotype.Component;

@Component
public class CappucinoPreparation extends CoffeePreparationTemplate{

    protected void brew(){
        System.out.println("Brewing cappuccino...");
    }

    @Override
    protected void addExtras() {
        System.out.println("Adding whipped cream.");
    }
}
