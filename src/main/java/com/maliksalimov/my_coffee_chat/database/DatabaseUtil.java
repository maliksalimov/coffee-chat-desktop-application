package com.maliksalimov.my_coffee_chat.database;

import com.maliksalimov.my_coffee_chat.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    private static final String DATABASE_URL = "jdbc:sqlite:my_coffee.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initializeDatabase(){
        String sql = """
                CREATE TABLE IF NOT EXISTS messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sender TEXT NOT NULL,
                text TEXT NOT NULL,
                timestamp TEXT DEFAULT(datetime('now')))
                """;

        try(Connection connection = getConnection();
        Statement statement = connection.createStatement()){
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void saveMessage(String sender, String text){
        String sql = "INSERT INTO messages (sender, text) VALUES (?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, text);
            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    public static List<Message> getAllMessages(){
        String sql = "SELECT id,sender,text,timestamp FROM messages ORDER BY id";
        List<Message> messages = new ArrayList<>();

        try(Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)){

            while(resultSet.next()){
                messages.add(new Message(resultSet.getLong("id"),
                        resultSet.getString("sender"),
                        resultSet.getString("text"),
                        resultSet.getString("timestamp")));
            }

        } catch (SQLException e) {
            System.out.println("Error getting messages: " + e.getMessage());
        }

        return messages;
    }
}
