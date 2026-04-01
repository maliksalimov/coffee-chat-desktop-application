package com.maliksalimov.my_coffee_chat.prototype;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CoffeeOrder implements Cloneable{

    private String customerName;
    private String coffeeType;
    private double price;

    public CoffeeOrder clone(){
        try {
            return (CoffeeOrder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCoffeeType() {
        return coffeeType;
    }

    public void setCoffeeType(String coffeeType) {
        this.coffeeType = coffeeType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return customerName + " ordered " + coffeeType + " - $" + price;
    }
}
