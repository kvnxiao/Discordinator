package com.github.alphahelix00.discordinator.d4j.handler;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandListener implements IListener<MessageReceivedEvent> {

    private final CommandHandlerD4J commandHandler = CommandHandlerD4J.getInstance();

    @Override
    public void handle(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        commandHandler.validateMessage(message, event);
    }
}
