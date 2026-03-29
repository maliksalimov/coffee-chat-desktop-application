package com.maliksalimov.my_coffee_chat.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispatches incoming requests to handler methods on a {@link BusinessObject}.
 *
 * <p>On first use, scans the target class for methods annotated with any
 * annotation that carries {@link RequestMappingMeta}. The result is cached
 * in {@code methodCache} so subsequent calls avoid repeated reflection scans.
 *
 * <p>Usage:
 * <pre>
 *   InteractionHandler handler = new InteractionHandler();
 *   handler.handleInteraction(coffeeShop, "order", "1 Cappuccino");
 * </pre>
 */
public class InteractionHandler {

    /**
     * Cache: maps each BusinessObject class to the list of handler methods
     * found on it. Populated once per class on first call.
     */
    private final Map<Class<?>, List<Method>> methodCache = new ConcurrentHashMap<>();

    /**
     * Finds and invokes the first handler method on {@code businessObject}
     * whose annotation is meta-annotated with {@link RequestMappingMeta}.
     *
     * @param businessObject the target object to dispatch the request to
     * @param requestType    a label describing the request (used in the
     *                       "no handler found" message)
     * @param request        the request payload passed to the handler method
     */
    public void handleInteraction(BusinessObject businessObject, String requestType, String request) {
        List<Method> handlers = methodCache.computeIfAbsent(
                businessObject.getClass(), this::scanHandlerMethods);

        if (handlers.isEmpty()) {
            System.out.println("No handler found for request type: " + requestType);
            return;
        }

        try {
            handlers.get(0).invoke(businessObject, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans all public methods of {@code clazz} and returns those whose
     * annotations are themselves annotated with {@link RequestMappingMeta}.
     * Called at most once per class thanks to {@code methodCache}.
     *
     * @param clazz the class to scan
     * @return list of handler methods; never null, may be empty
     */
    private List<Method> scanHandlerMethods(Class<?> clazz) {
        List<Method> handlers = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(RequestMappingMeta.class)) {
                    handlers.add(method);
                    break;
                }
            }
        }
        return handlers;
    }
}