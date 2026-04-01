package com.maliksalimov.my_coffee_chat.multithreading;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final BaristaService baristaService;
    private final OrderQueue orderQueue;

    public CustomerService(BaristaService baristaService, OrderQueue orderQueue) {
        this.baristaService = baristaService;
        this.orderQueue = orderQueue;
    }

    public void placeOrder(String customerName, String coffeeType){
        Order order = new Order(customerName, coffeeType);
        orderQueue.addOrder(order);
        baristaService.prepareCoffee(order);
        System.out.println(customerName + " ordered " + coffeeType);
    }
}
