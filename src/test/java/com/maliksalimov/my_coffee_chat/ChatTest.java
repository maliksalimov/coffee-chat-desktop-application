package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.Chat;
import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import com.maliksalimov.my_coffee_chat.model.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatTest {
    private static final String USER = "User";
    private static final long RESPONSE_TIMEOUT_SECONDS = 5;

    private Chat chat;
    private CoffeeShop coffeeShop;

    @BeforeEach
    void prepare() {
        TestDatabaseSupport.initializeAndClearMessages();
        coffeeShop = CoffeeShop.getInstance();
        TestDatabaseSupport.resetCallback();
        coffeeShop.startBaristas();
        chat = new Chat(coffeeShop);
    }

    @AfterEach
    void cleanup() {
        TestDatabaseSupport.resetCallback();
    }

    @Test
    void sending_blank_message_should_not_throw_exception() {
        assertDoesNotThrow(() -> chat.sendMessage(USER, ""));
    }

    @Test
    void sending_blank_message_should_not_be_saved_to_database() {
        int beforeCount = DatabaseUtil.getAllMessages().size();

        chat.sendMessage(USER, "   ");

        int afterCount = DatabaseUtil.getAllMessages().size();
        assertEquals(beforeCount, afterCount);
    }

    @Test
    void sending_null_message_should_not_be_saved_to_database() {
        int beforeCount = DatabaseUtil.getAllMessages().size();

        chat.sendMessage(USER, null);

        int afterCount = DatabaseUtil.getAllMessages().size();
        assertEquals(beforeCount, afterCount);
    }

    @Test
    void sending_message_should_not_throw_exception() {
        assertDoesNotThrow(() -> chat.sendMessage(USER, "Hello"));
    }

    @Test
    void sending_message_should_save_user_message() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        coffeeShop.setOnResponse(response -> latch.countDown());

        chat.sendMessage(USER, "order americano");

        assertTrue(latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        List<Message> messages = DatabaseUtil.getAllMessages();
        boolean userMessageSaved = messages.stream()
                .anyMatch(message -> USER.equals(message.getSender()) && "order americano".equals(message.getText()));

        assertTrue(userMessageSaved);
    }

    @Test
    void response_should_call_callback() throws InterruptedException {
        boolean[] callbackCalled = {false};
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> {
            callbackCalled[0] = true;
            latch.countDown();
        });

        chat.sendMessage(USER, "order coffee");

        boolean success = latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(success);
        assertTrue(callbackCalled[0]);
    }
}
