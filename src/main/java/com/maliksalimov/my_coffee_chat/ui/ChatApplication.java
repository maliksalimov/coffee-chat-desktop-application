package com.maliksalimov.my_coffee_chat.ui;

import com.maliksalimov.my_coffee_chat.chat.Chat;
import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import com.maliksalimov.my_coffee_chat.model.Message;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;


public class ChatApplication extends Application{

    private TextArea chatArea;
    private TextField messageField;
    private ListView<String> imageList;
    private Chat chat;
    private ObservableList<String> uploadedImages = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        CoffeeShop coffeeShop = new CoffeeShop();
        coffeeShop.startBaristas();
        chat = new Chat(coffeeShop);

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(400);

        messageField = new TextField();
        messageField.setPromptText("Write your message here...");
        messageField.setPrefWidth(300);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        Button uploadButton = new Button("Upload Image");
        uploadButton.setOnAction(e -> uploadImage(stage));

        imageList = new ListView<>(uploadedImages);
        imageList.setPrefHeight(150);

        HBox inputBox = new HBox(10, messageField, sendButton);
        VBox root = new VBox(10, chatArea, inputBox, uploadButton, imageList);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 500, 650);
        stage.setTitle("Coffee Chat");
        stage.setScene(scene);
        stage.show();

        loadChatHistory();
    }

    private void sendMessage(){
        String message = messageField.getText().trim();
        if (!message.isEmpty()){
            chatArea.appendText("You: " + message + "\n");
            chat.sendMessage("User", message);
            messageField.clear();
        }
    }

    private void uploadImage(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(stage);

        if(file != null){
            uploadedImages.add(file.getAbsolutePath());
            DatabaseUtil.saveMessage("User", "Uploaded image: " + file.getName());
            chatArea.appendText("User: Uploaded image: " + file.getName() + "\n");
        }
    }

    private void loadChatHistory(){
        List<Message> messages = DatabaseUtil.getAllMessages();
        for (Message message : messages) {
            chatArea.appendText(message.getSender() + ": " + message.getText() + "\n");
        }
    }
}
