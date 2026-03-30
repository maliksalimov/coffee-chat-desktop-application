package com.maliksalimov.my_coffee_chat.observer;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class OrderReadyEvent extends ApplicationEvent {

    private final String customerName;
    private final String orderDetails;

    public OrderReadyEvent(Object source, String customerName, String orderDetails) {
        super(source);
        this.customerName = customerName;
        this.orderDetails = orderDetails;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderDetails() {
        return orderDetails;
    }
}
