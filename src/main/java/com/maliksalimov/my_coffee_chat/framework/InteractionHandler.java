package com.maliksalimov.my_coffee_chat.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class InteractionHandler{

    public void handleInteraction(BusinessObject businessObject, String requestType, String request){
        Method[] methods = businessObject.getClass().getDeclaredMethods();

        for(Method method : methods){
            for(Annotation annotation: method.getAnnotations()){
                if(annotation.annotationType().isAnnotationPresent(RequestMappingMeta.class)){
                    try{
                        method.invoke(businessObject, request);
                        return;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("No handler found for request: " + requestType);
    }
}
