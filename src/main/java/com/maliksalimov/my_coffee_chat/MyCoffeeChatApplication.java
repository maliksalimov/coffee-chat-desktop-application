package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.Chat;
import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import com.maliksalimov.my_coffee_chat.ui.ChatApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyCoffeeChatApplication {

    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();

        CoffeeShop coffeeShop = new CoffeeShop();
        coffeeShop.startBaristas();

        Chat chat = new Chat(coffeeShop);

        Application.launch(ChatApplication.class, args);
    }

}
