package com.maliksalimov.my_coffee_chat.ui;

import com.maliksalimov.my_coffee_chat.chat.Chat;
import com.maliksalimov.my_coffee_chat.chat.CoffeeShop;
import com.maliksalimov.my_coffee_chat.database.DatabaseUtil;
import com.maliksalimov.my_coffee_chat.model.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.List;


public class ChatApplication extends Application{

    private TextArea chatArea;
    private TextField messageField;
    private ListView<ImageView> imageList;
    private Chat chat;
    private ObservableList<ImageView> uploadedImages = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        CoffeeShop coffeeShop = CoffeeShop.getInstance();
        coffeeShop.startBaristas();
        chat = new Chat(coffeeShop);

        // Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("""
        -fx-control-inner-background: #1e1e2e;
        -fx-text-fill: #cdd6f4;
        -fx-font-family: 'Monospace';
        -fx-font-size: 13px;
        -fx-border-color: #45475a;
        -fx-border-radius: 8px;
        -fx-background-radius: 8px;
    """);

        // Message field
        messageField = new TextField();
        messageField.setPromptText("Write your message here...");
        messageField.setPrefWidth(300);
        messageField.setStyle("""
        -fx-background-color: #313244;
        -fx-text-fill: #cdd6f4;
        -fx-prompt-text-fill: #6c7086;
        -fx-font-size: 13px;
        -fx-border-color: #89b4fa;
        -fx-border-radius: 6px;
        -fx-background-radius: 6px;
        -fx-padding: 8px;
    """);
        messageField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                sendMessage();
            }
        });

        // Send button
        Button sendButton = new Button("Send ➤");
        sendButton.setStyle("""
        -fx-background-color: #89b4fa;
        -fx-text-fill: #1e1e2e;
        -fx-font-weight: bold;
        -fx-font-size: 13px;
        -fx-border-radius: 6px;
        -fx-background-radius: 6px;
        -fx-padding: 8px 16px;
        -fx-cursor: hand;
    """);
        sendButton.setOnAction(e -> sendMessage());

        // Upload button
        Button uploadButton = new Button("📎 Upload Image");
        uploadButton.setStyle("""
        -fx-background-color: #313244;
        -fx-text-fill: #cdd6f4;
        -fx-font-size: 13px;
        -fx-border-color: #6c7086;
        -fx-border-radius: 6px;
        -fx-background-radius: 6px;
        -fx-padding: 8px 16px;
        -fx-cursor: hand;
    """);
        uploadButton.setOnAction(e -> uploadImage(stage));

        // Image list
        imageList = new ListView<>(uploadedImages);
        imageList.setPrefHeight(150);
        imageList.setStyle("""
        -fx-background-color: #1e1e2e;
        -fx-border-color: #45475a;
        -fx-border-radius: 8px;
        -fx-background-radius: 8px;
    """);

        // Callback
        coffeeShop.setOnResponse(response ->
                javafx.application.Platform.runLater(() ->
                        chatArea.appendText("Barista: " + response + "\n")
                )
        );

        // Layout
        HBox inputBox = new HBox(10, messageField, sendButton);
        inputBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox root = new VBox(12, chatArea, inputBox, uploadButton, imageList);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #181825;");

        Scene scene = new Scene(root, 520, 680);
        stage.setTitle("☕ Coffee Chat");
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
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file != null){
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            uploadedImages.add(imageView);
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
