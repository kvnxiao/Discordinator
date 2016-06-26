package com.github.alphahelix00.discordinator.d4j.handler;

import com.github.alphahelix00.discordinator.d4j.commands.cmdimpl.EssentialCommandsD4J;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandListenerD4J implements IListener<MessageReceivedEvent> {

    private String[] botMention = null;

    public void setBotMention(String[] botMention) {
        this.botMention = botMention;
    }

    private final CommandHandlerD4J commandHandler;

    public CommandListenerD4J(final CommandHandlerD4J commandHandler) {
        this.commandHandler = commandHandler;
        this.commandHandler.registerCommand(new EssentialCommandsD4J.DisableD4J());
        this.commandHandler.registerCommand(new EssentialCommandsD4J.EnableD4J());
        this.commandHandler.registerCommand(new EssentialCommandsD4J.HelpD4J());
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        boolean hasMention = false;
        if (message.length() > botMention[0].length() && message.startsWith(botMention[0])) {
            message = message.substring(botMention[0].length() + 1);
            hasMention = true;
        } else if (message.length() > botMention[1].length() && message.startsWith(botMention[1])) {
            message = message.substring(botMention[1].length() + 1);
            hasMention = true;
        }
        commandHandler.validateParse(message, event, hasMention);
    }

}
