package com.maliksalimov.my_coffee_chat.observer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationListener {

    @EventListener
        public void onOrderReady(OrderReadyEvent event){
        System.out.println("Notification: " + event.getCustomerName() +
                ", your order is ready: " + event.getOrderDetails());
    }
}
