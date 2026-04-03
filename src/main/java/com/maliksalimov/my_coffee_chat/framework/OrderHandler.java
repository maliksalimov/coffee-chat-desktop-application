package com.maliksalimov.my_coffee_chat.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as the handler for order requests.
 *
 * <p>Apply this annotation to a single-{@code String}-parameter method on a
 * {@link BusinessObject} to designate it as the order handler. The
 * {@link InteractionHandler} will invoke it when processing any request.
 *
 * <p>Example:
 * <pre>
 * {@literal @}OrderHandler
 * public void handleOrder(String orderDetails) {
 *     System.out.println("Processing order: " + orderDetails);
 * }
 * </pre>
 */
@RequestMappingMeta
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderHandler {
}