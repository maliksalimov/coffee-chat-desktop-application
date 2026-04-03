package com.maliksalimov.my_coffee_chat.observer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public OrderEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishOrderReadyEvent(String customerName, String orderDetails){
        eventPublisher.publishEvent(new OrderReadyEvent(this, customerName, orderDetails));
    }
}
