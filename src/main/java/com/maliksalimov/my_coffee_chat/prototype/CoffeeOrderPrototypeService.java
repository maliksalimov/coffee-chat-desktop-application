package com.maliksalimov.my_coffee_chat.prototype;

public class CoffeeOrderPrototypeService {
    private final CoffeeOrder prototypeOrder;

    public CoffeeOrderPrototypeService(CoffeeOrder prototypeOrder) {
        this.prototypeOrder = prototypeOrder;
    }

    public CoffeeOrder createOrder(String customerName, String coffeeType, double price){
        CoffeeOrder order = prototypeOrder.clone();
        order.setCustomerName(customerName);
        order.setCoffeeType(coffeeType);
        order.setPrice(price);
        return order;
    }
}
