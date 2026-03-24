package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class OrderQueueTest {
    private CoffeeShop coffeeShop;

    @BeforeEach
    void prepare() {
        coffeeShop = CoffeeShop.getInstance();
        coffeeShop.startBaristas();
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

        boolean success = latch.await(5, TimeUnit.SECONDS);
        assertTrue(success, "Barista did not respond in time");
        assertEquals("Your order has been placed: order latte", answer[0]);
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

        boolean success = latch.await(5, TimeUnit.SECONDS);
        assertTrue(success, "Barista did not respond in time");
        assertTrue(answer[0].contains("Your order has been placed"), "Barista processed the order");
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

        boolean success = latch.await(5, TimeUnit.SECONDS);
        assertTrue(success, "Barista did not respond in time");
        assertTrue(answer[0].contains("I don't understand your order"), "Barista did not understand the order");
    }
}
