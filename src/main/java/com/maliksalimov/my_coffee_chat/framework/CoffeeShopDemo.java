package com.maliksalimov.my_coffee_chat.framework;

/**
 * Demonstration implementation of {@link BusinessObject}.
 *
 * <p>Shows how to wire handler methods into the annotation-driven framework.
 * {@link InteractionHandler} will discover {@code handleOrder} and
 * {@code handleChat} automatically via reflection — no registration required.
 *
 * <p>This class is intentionally minimal. For the full production
 * implementation see {@code com.maliksalimov.my_coffee_chat.chat.CoffeeShop}.
 */
public class CoffeeShopDemo implements BusinessObject {

    /**
     * Handles an incoming order request.
     *
     * @param orderDetails the order payload, e.g. "1 Cappuccino"
     */
    @OrderHandler
    public void handleOrder(String orderDetails) {
        System.out.println("Order received: " + orderDetails);
    }

    /**
     * Handles an incoming chat message.
     *
     * @param message the chat payload, e.g. "Hello, barista!"
     */
    @ChatHandler
    public void handleChat(String message) {
        System.out.println("Chat message: " + message);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link InteractionHandler} in production usage.
     * Left empty here as dispatch is handled externally by the framework.
     */
    @Override
    public void processRequest(String request) {
    }
}