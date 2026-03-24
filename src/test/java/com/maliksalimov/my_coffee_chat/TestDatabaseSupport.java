package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class TestDatabaseSupport {
    private TestDatabaseSupport() {
    }

    static void initializeAndClearMessages() {
        DatabaseUtil.initializeDatabase();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM messages");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset messages table", e);
        }
    }

    static void resetCallback() {
        CoffeeShop.getInstance().setOnResponse(null);
    }
}
