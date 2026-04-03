package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.multithreading.Order;
import com.maliksalimov.my_coffee_chat.multithreading.OrderQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderQueueUnitTest {

    private OrderQueue orderQueue;

    @BeforeEach
    void setup() {
        orderQueue = new OrderQueue();
    }

    @Test
    void add_order_should_increase_size() {
        orderQueue.addOrder(new Order("Alice", "Latte"));

        assertEquals(1, orderQueue.size());
    }

    @Test
    void take_order_should_return_correct_order() throws InterruptedException {
        Order order = new Order("Bob", "Espresso");
        orderQueue.addOrder(order);

        Order taken = orderQueue.takeOrder();

        assertNotNull(taken);
        assertEquals("Bob", taken.getCustomerName());
        assertEquals("Espresso", taken.getCoffeeType());
    }

    @Test
    void concurrent_add_order_calls_should_be_thread_safe() throws InterruptedException {
        int threadCount = 20;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                orderQueue.addOrder(new Order("Customer" + index, "Coffee" + index));
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount, orderQueue.size());
    }
}
