package com.maliksalimov.my_coffee_chat.template;

import com.maliksalimov.my_coffee_chat.prototype.CoffeeOrder;
import org.springframework.stereotype.Component;

@Component
public class EspressoPreparation extends CoffeePreparationTemplate {

    @Override
    protected void brew(){
        System.out.println("Brewing espresso shot...");
    }
}
