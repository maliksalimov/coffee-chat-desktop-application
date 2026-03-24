package com.maliksalimov.my_coffee_chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Message {

    private Long id;
    private String sender;
    private String text;
    private String timestamp;
}
