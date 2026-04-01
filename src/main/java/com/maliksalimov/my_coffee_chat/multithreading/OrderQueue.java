package com.maliksalimov.my_coffee_chat.multithreading;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class OrderQueue {

    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    public void addOrder(Order order){
        try{
            queue.put(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Order takeOrder() throws InterruptedException {
        try{
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public int size(){
        return queue.size();
    }
}
