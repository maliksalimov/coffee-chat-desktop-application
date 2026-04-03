package com.maliksalimov.my_coffee_chat.decorator;

import com.maliksalimov.my_coffee_chat.factory.Coffee;
import org.springframework.stereotype.Service;

@Service
public class DecoratorService {

    public Coffee addMilk(Coffee coffee){
        return new MilkDecorator(coffee);
    }

    public Coffee addSugar(Coffee coffee){
        return new SugarDecorator(coffee);
    }
}
