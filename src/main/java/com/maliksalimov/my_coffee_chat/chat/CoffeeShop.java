package com.maliksalimov.my_coffee_chat.chat;

import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CoffeeShop {

    private final BlockingQueue<String> orderQueue = new LinkedBlockingQueue<>();

    private final int numberOfBaristas = 10;

    public void startBaristas(){
        for (int i = 0;i < numberOfBaristas;i ++) {
            int barisataId = i + 1;
            Thread barista = new Thread(this::processOrders);
            barista.setName("Barista " + barisataId);
            barista.setDaemon(true);
            barista.start();
        }
    }

    public void receiveMessage(String message){
        try{
            orderQueue.put(message);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void processOrders(){
        while(true){
            try{
                String message = orderQueue.take();
                System.out.println(Thread.currentThread().getName() + "Processing order: " + message);

                if(message.toLowerCase().contains("order")){
                    String response = "Your order has been placed" + message;
                    DatabaseUtil.saveMessage("Barista", response);
                    System.out.println(Thread.currentThread().getName() + "Sending response: " + response);
                } else{
                    String response = "I don't understand your order" + message;
                    DatabaseUtil.saveMessage("Barista", response);
                    System.out.println(Thread.currentThread().getName() + "Sending response: " + response);
                }
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
