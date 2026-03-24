package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoffeeShopTest {
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
    void get_instance_should_return_singleton_instance() {
        CoffeeShop first = CoffeeShop.getInstance();
        CoffeeShop second = CoffeeShop.getInstance();

        assertSame(first, second);
    }

    @Test
    void set_on_response_should_replace_previous_callback() throws InterruptedException {
        AtomicInteger firstCallbackCount = new AtomicInteger(0);
        AtomicInteger secondCallbackCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        coffeeShop.setOnResponse(response -> firstCallbackCount.incrementAndGet());
        coffeeShop.setOnResponse(response -> {
            secondCallbackCount.incrementAndGet();
            latch.countDown();
        });

        coffeeShop.receiveMessage("order flat white");

        assertTrue(latch.await(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertEquals(0, firstCallbackCount.get());
        assertEquals(1, secondCallbackCount.get());
    }

    @Test
    void start_baristas_should_be_safe_to_call_multiple_times() {
        assertDoesNotThrow(() -> {
            coffeeShop.startBaristas();
            coffeeShop.startBaristas();
        });
    }
}
