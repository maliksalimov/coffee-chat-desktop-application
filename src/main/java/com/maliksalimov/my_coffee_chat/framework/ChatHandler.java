package com.maliksalimov.my_coffee_chat.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as the handler for chat requests.
 *
 * <p>Apply this annotation to a single-{@code String}-parameter method on a
 * {@link BusinessObject} to designate it as the chat handler. The
 * {@link InteractionHandler} will invoke it when processing any request.
 *
 * <p>Example:
 * <pre>
 * {@literal @}ChatHandler
 * public void handleChat(String message) {
 *     System.out.println("Chat message: " + message);
 * }
 * </pre>
 */
@RequestMappingMeta
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatHandler {
}