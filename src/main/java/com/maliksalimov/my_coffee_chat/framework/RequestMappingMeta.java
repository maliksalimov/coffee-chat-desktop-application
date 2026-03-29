package com.maliksalimov.my_coffee_chat.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation that marks an annotation as a request handler.
 *
 * <p>Any annotation carrying {@code @RequestMappingMeta} signals to
 * {@link InteractionHandler} that methods annotated with it should be
 * treated as request handlers and invoked during dispatch.
 *
 * <p>To define a new handler type, create an annotation and apply
 * {@code @RequestMappingMeta} to it:
 *
 * <pre>
 * {@literal @}RequestMappingMeta
 * {@literal @}Target(ElementType.METHOD)
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * public {@literal @}interface FeedbackHandler {}
 * </pre>
 *
 * <p>No changes to {@link InteractionHandler} are required.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMappingMeta {
}