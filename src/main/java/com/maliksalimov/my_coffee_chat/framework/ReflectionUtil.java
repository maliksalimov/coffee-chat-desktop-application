package com.maliksalimov.my_coffee_chat.framework;

import java.lang.reflect.Method;

public class ReflectionUtil {
    public static void invokeMethod(Object object, String methodName, String parameter) {

        try{
            Method method = object.getClass().getMethod(methodName, String.class);
            method.invoke(object, parameter);
        } catch (NoSuchMethodException e){
            System.out.println("Method not found: " + methodName);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
