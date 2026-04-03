package com.maliksalimov.my_coffee_chat.strategy;

public interface PricingStrategy {
    double applyDiscount(double originalPrice);
    String getStrategyName();
}
