package com.maliksalimov.my_coffee_chat.facade;

import com.maliksalimov.my_coffee_chat.adapter.PaymentService;
import com.maliksalimov.my_coffee_chat.command.OrderCommandProcessor;
import com.maliksalimov.my_coffee_chat.command.PlaceOrderCommand;
import com.maliksalimov.my_coffee_chat.decorator.DecoratorService;
import com.maliksalimov.my_coffee_chat.factory.Coffee;
import com.maliksalimov.my_coffee_chat.factory.CoffeeFactory;
import com.maliksalimov.my_coffee_chat.observer.OrderEventPublisher;
import com.maliksalimov.my_coffee_chat.singleton.CoffeeShopManager;
import com.maliksalimov.my_coffee_chat.strategy.PricingStrategy;
import org.springframework.stereotype.Service;

@Service
public class CoffeeShopFacade {

    private final CoffeeFactory coffeeFactory;
    private final DecoratorService decoratorService;
    private final OrderEventPublisher eventPublisher;
    private final PaymentService paymentService;
    private final CoffeeShopManager coffeeShopManager;
    private final PricingStrategy pricingStrategy;
    private final OrderCommandProcessor orderCommandProcessor;

    public CoffeeShopFacade(CoffeeFactory coffeeFactory, DecoratorService decoratorService,
                            OrderEventPublisher orderEventPublisher, PaymentService paymentService,
                            CoffeeShopManager coffeeShopManager, PricingStrategy pricingStrategy, OrderCommandProcessor orderCommandProcessor) {
        this.coffeeFactory = coffeeFactory;
        this.decoratorService = decoratorService;
        this.eventPublisher = orderEventPublisher;
        this.paymentService = paymentService;
        this.coffeeShopManager = coffeeShopManager;
        this.pricingStrategy = pricingStrategy;
        this.orderCommandProcessor = orderCommandProcessor;
    }

    public String placeOrder(String customerName, String coffeeType,
                             boolean withMilk, boolean withSugar){
        Coffee coffee = coffeeFactory.createCoffee(coffeeType);

        if(withMilk){
            coffee = decoratorService.addMilk(coffee);
        }

        if(withSugar){
            coffee = decoratorService.addSugar(coffee);
        }

        double finalPrice = pricingStrategy.applyDiscount(coffee.getPrice());

        orderCommandProcessor.process(new PlaceOrderCommand(customerName, coffee.getName()));
        paymentService.processPayment(customerName, finalPrice);

        eventPublisher.publishOrderReadyEvent(customerName, coffee.getName());
        coffeeShopManager.recordOrder();

        return coffee.getName() + " -$" + finalPrice;
    }
}
