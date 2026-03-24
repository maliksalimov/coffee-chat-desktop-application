package com.maliksalimov.my_coffee_chat.model;

public class Message {

    private final Long id;
    private final String sender;
    private final String text;
    private final String timestamp;

    public Message(Long id, String sender, String text, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
