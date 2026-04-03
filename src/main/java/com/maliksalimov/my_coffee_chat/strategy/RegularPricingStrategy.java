package com.maliksalimov.my_coffee_chat.strategy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class RegularPricingStrategy implements PricingStrategy{
    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice;
    }

    @Override
    public String getStrategyName() {
        return "Regular";
    }
}
