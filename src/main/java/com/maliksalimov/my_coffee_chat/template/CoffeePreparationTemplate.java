package com.maliksalimov.my_coffee_chat.template;

import java.sql.SQLOutput;

public abstract class CoffeePreparationTemplate {

    public final void prepareCoffee(){
        boilWater();
        brew();
        pourInCup();
        addExtras();
    }

    private void boilWater(){
        System.out.println("Boiling water...");
    }

    private void pourInCup(){
        System.out.println("Pouring into cup...");
    }

    protected abstract void brew();

    protected void addExtras(){
        System.out.println("No extras.");
    }
}
