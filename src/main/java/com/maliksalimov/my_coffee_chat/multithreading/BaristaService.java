package com.maliksalimov.my_coffee_chat.multithreading;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BaristaService {

    public CompletableFuture<String> prepareCoffee(Order order){
        try{
            System.out.println(Thread.currentThread().getName()
                    + " preparing: " + order);
            Thread.sleep(1000);
            String result = "Ready: " + order.getCoffeeType()
                    + " for " + order.getCustomerName();
            System.out.println(result);
            return CompletableFuture.completedFuture(result);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Order interrupted");
        }
    }
}
