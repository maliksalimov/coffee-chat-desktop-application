package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import com.maliksalimov.my_coffee_chat.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseUtilTest {

    @BeforeEach
    void prepare() {
        TestDatabaseSupport.initializeAndClearMessages();
    }

    @Test
    void initialize_database_should_be_idempotent() {
        DatabaseUtil.initializeDatabase();
        DatabaseUtil.initializeDatabase();

        assertNotNull(DatabaseUtil.getAllMessages());
    }

    @Test
    void save_message_should_persist_message() {
        DatabaseUtil.saveMessage("User", "order filter coffee");

        List<Message> messages = DatabaseUtil.getAllMessages();

        assertEquals(1, messages.size());
        assertEquals("User", messages.get(0).getSender());
        assertEquals("order filter coffee", messages.get(0).getText());
        assertNotNull(messages.get(0).getTimestamp());
    }

    @Test
    void get_all_messages_should_return_records_in_insert_order() {
        DatabaseUtil.saveMessage("User", "first");
        DatabaseUtil.saveMessage("Barista", "second");

        List<Message> messages = DatabaseUtil.getAllMessages();

        assertEquals(2, messages.size());
        assertEquals("first", messages.get(0).getText());
        assertEquals("second", messages.get(1).getText());
        assertTrue(messages.get(0).getId() < messages.get(1).getId());
    }

    @Test
    void get_all_messages_should_return_empty_list_when_no_data() {
        List<Message> messages = DatabaseUtil.getAllMessages();
        assertTrue(messages.isEmpty());
    }
}
