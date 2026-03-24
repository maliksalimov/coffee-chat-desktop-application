package com.maliksalimov.my_coffee_chat;

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderQueueTest {
    private static final long RESPONSE_TIMEOUT_SECONDS = 5;

    private CoffeeShop coffeeShop;

    @BeforeEach
    void prepare() {
        TestDatabaseSupport.initializeAndClearMessages();
        coffeeShop = CoffeeShop.getInstance();
        TestDatabaseSupport.resetCallback();
        coffeeShop.startBaristas();
    }

    @AfterEach
    void cleanup() {
        TestDatabaseSupport.resetCallback();
    }

    @Test
    void start_baristas_should_be_idempotent() {
        assertDoesNotThrow(() -> coffeeShop.startBaristas());
    }

    @Test
    void null_message_should_throw_exception() {
        assertThrows(NullPointerException.class, () -> coffeeShop.receiveMessage(null));
    }

    @Test
    void message_should_be_received_by_barista() throws InterruptedException {
        String[] answer = {null};
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> {
            answer[0] = response;
            latch.countDown();
        });

        coffeeShop.receiveMessage("order latte");

        boolean success = latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(success);
        assertEquals("Your order has been placed: order latte", answer[0]);
    }

    @Test
    void message_should_be_processed_case_insensitively_by_barista() throws InterruptedException {
        String[] answer = {null};
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> {
            answer[0] = response;
            latch.countDown();
        });

        coffeeShop.receiveMessage("Please ORDER cappuccino");

        boolean success = latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(success);
        assertEquals("Your order has been placed: Please ORDER cappuccino", answer[0]);
    }

    @Test
    void message_should_be_processed_by_barista() throws InterruptedException {
        String[] answer = {null};
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> {
            answer[0] = response;
            latch.countDown();
        });

        coffeeShop.receiveMessage("I want to order espresso");

        boolean success = latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(success);
        assertTrue(answer[0].contains("Your order has been placed"));
    }

    @Test
    void processed_message_should_be_saved_to_database() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> latch.countDown());

        coffeeShop.receiveMessage("order mocha");

        assertTrue(latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        List<Message> messages = DatabaseUtil.getAllMessages();
        boolean baristaMessageSaved = messages.stream()
                .anyMatch(message -> "Barista".equals(message.getSender())
                        && "Your order has been placed: order mocha".equals(message.getText()));

        assertTrue(baristaMessageSaved);
    }

    @Test
    void message_should_be_ignored_by_barista() throws InterruptedException {
        String[] answer = {null};
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> {
            answer[0] = response;
            latch.countDown();
        });

        coffeeShop.receiveMessage("I want to drink coffee");

        boolean success = latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(success);
        assertTrue(answer[0].contains("I don't understand your order"));
    }
}
