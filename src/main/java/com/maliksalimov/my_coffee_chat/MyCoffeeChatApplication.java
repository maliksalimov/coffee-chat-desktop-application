package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import com.maliksalimov.my_coffee_chat.ui.ChatApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyCoffeeChatApplication {

    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();
        Application.launch(ChatApplication.class, args);
    }

}