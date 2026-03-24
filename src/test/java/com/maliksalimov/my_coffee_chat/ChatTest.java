package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.Chat;
import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatTest {
    private Chat chat;

    @BeforeEach
    void prepare(){
        DatabaseUtil.initializeDatabase();
        CoffeeShop.getInstance().startBaristas();
        chat = new Chat(CoffeeShop.getInstance());
    }

    @Test
    void sending_blank_message_should_not_throw_exception(){
        assertDoesNotThrow(() -> chat.sendMessage("User", ""));
    }

    @Test
    void sending_message_should_not_throw_exception(){
        assertDoesNotThrow(() -> chat.sendMessage("User", "Hello"));
    }

    @Test
    void response_should_call_callback() throws InterruptedException{
        boolean[] callbackCalled = {false};
        CountDownLatch latch = new CountDownLatch(1);

        CoffeeShop.getInstance().setOnResponse(response -> {
            callbackCalled[0] = true;
            latch.countDown();
        });

        chat.sendMessage("User", "order coffee");

        boolean success = latch.await(5, TimeUnit.SECONDS);
        assertTrue(success, "Callback was not called in time");
        assertTrue(callbackCalled[0], "Callback should be called");
    }
}
