package com.maliksalimov.my_coffee_chat.command;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderCommandProcessor {
    private final List<OrderCommand> orderCommands = new ArrayList<>();

    public void process(OrderCommand command){
        command.execute();
        orderCommands.add(command);
    }

    public List<OrderCommand> getCommandHistory() {
        return orderCommands;
    }
}
