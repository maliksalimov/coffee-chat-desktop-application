package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CoffeeShopSingletonTest {

    private CoffeeShop coffeeShop;

    @BeforeEach
    void setup() {
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
    void get_instance_should_always_return_same_singleton() {
        CoffeeShop first = CoffeeShop.getInstance();
        CoffeeShop second = CoffeeShop.getInstance();

        assertSame(first, second);
    }

    @Test
    void start_baristas_should_not_throw_when_called_multiple_times() {
        assertDoesNotThrow(() -> {
            coffeeShop.startBaristas();
            coffeeShop.startBaristas();
            coffeeShop.startBaristas();
        });
    }

    @Test
    void receive_message_with_null_should_throw_null_pointer_exception() {
        assertThrows(NullPointerException.class, () -> coffeeShop.receiveMessage(null));
    }
}
