package com.maliksalimov.my_coffee_chat.strategy;

import org.springframework.stereotype.Component;

@Component
public class SilverPricingStrategy  implements PricingStrategy{
    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * 0.9;
    }

    @Override
    public String getStrategyName() {
        return "Silver (10% off)";
    }
}
