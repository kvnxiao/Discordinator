package com.github.alphahelix00.discordinator.d4j.handler;

import com.github.alphahelix00.discordinator.d4j.commands.EssentialCommandsD4J;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandListenerD4J implements IListener<MessageReceivedEvent> {

    private final CommandHandlerD4J commandHandler = CommandHandlerD4J.getInstance();

    public CommandListenerD4J() {
        commandHandler.registerCommand(new EssentialCommandsD4J.DisableD4J());
        commandHandler.registerCommand(new EssentialCommandsD4J.EnableD4J());
        commandHandler.registerCommand(new EssentialCommandsD4J.HelpD4J());
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        commandHandler.validateMessage(message, event);
    }
}
