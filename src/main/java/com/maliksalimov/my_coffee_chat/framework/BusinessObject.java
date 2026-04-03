package com.maliksalimov.my_coffee_chat.framework;

/**
 * Common interface for all business types in the framework.
 *
 * <p>Any class that wants to participate in the annotation-driven dispatch
 * system must implement this interface. The {@link InteractionHandler} accepts
 * only {@code BusinessObject} instances, ensuring type safety at the
 * dispatch boundary.
 *
 * <p>Example implementations: {@code CoffeeShop}, {@code CoffeeShopDemo}.
 */
public interface BusinessObject {
    /**
     * Handles an arbitrary request string.
     *
     * <p>Implementations may delegate to specific handler methods annotated
     * with {@link OrderHandler}, {@link ChatHandler}, or any other annotation
     * meta-annotated with {@link RequestMappingMeta}.
     *
     * @param request the raw request payload
     */
    void processRequest(String request);
}