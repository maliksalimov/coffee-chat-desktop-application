package com.maliksalimov.my_coffee_chat.framework;

import java.lang.reflect.Method;

/**
 * Utility class for direct method invocation via Java Reflection.
 *
 * <p>Provides a simplified API for invoking a single-{@code String}-parameter
 * method by name, without the caller needing to handle reflection boilerplate.
 */
public class ReflectionUtil {

    /**
     * Invokes the named method on {@code obj}, passing {@code parameter} as
     * the sole argument.
     *
     * <p>The method must be public and accept exactly one {@code String}
     * parameter. If no such method exists, a message is printed to stdout
     * and the call returns silently.
     *
     * @param obj        the object on which to invoke the method
     * @param methodName the exact name of the method to invoke
     * @param parameter  the {@code String} argument to pass to the method
     */
    public static void invokeMethod(Object obj, String methodName, String parameter) {
        try {
            Method method = obj.getClass().getMethod(methodName, String.class);
            method.invoke(obj, parameter);
        } catch (NoSuchMethodException e) {
            System.out.println("Method not found: " + methodName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}