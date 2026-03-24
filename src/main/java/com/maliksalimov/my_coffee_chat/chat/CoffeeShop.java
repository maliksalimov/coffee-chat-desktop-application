package com.maliksalimov.my_coffee_chat.chat;

import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class CoffeeShop {
    private static CoffeeShop instance;

    private CoffeeShop() {}

    public static synchronized CoffeeShop getInstance() {
        if (instance == null) {
            instance = new CoffeeShop();
        }
        return instance;
    }

    private final BlockingQueue<String> orderQueue = new LinkedBlockingQueue<>();

    private final int numberOfBaristas = 10;
    private boolean baristasStarted = false;

    private Consumer<String> onResponse;

    public void setOnResponse(Consumer<String> onResponse) {
        this.onResponse = onResponse;
    }

    public synchronized void startBaristas(){
        if (baristasStarted) return;
        for (int i = 0;i < numberOfBaristas;i ++) {
            int barisataId = i + 1;
            Thread barista = new Thread(this::processOrders);
            barista.setName("Barista " + barisataId);
            barista.setDaemon(true);
            barista.start();
        }
        baristasStarted = true;
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
                System.out.println(Thread.currentThread().getName() + " Processing order: " + message);

                String response;
                if(message.toLowerCase().contains("order")){
                    response = "Your order has been placed: " + message;
                } else{
                    response = "I don't understand your order: " + message;
                }
                DatabaseUtil.saveMessage("Barista", response);
                Consumer<String> callback = onResponse;
                if (callback != null) {
                    callback.accept(response);
                }
                System.out.println(Thread.currentThread().getName() + " Sending response: " + response);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }

        }
    }
}
