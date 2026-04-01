package com.maliksalimov.my_coffee_chat.strategy;

public class GoldPricingStrategy implements PricingStrategy{
    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * 0.8;
    }
    @Override
    public String getStrategyName() {
        return "Gold (20% off)";
    }
}
