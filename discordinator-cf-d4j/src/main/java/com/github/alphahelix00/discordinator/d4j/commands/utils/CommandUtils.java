package com.github.alphahelix00.discordinator.d4j.commands.utils;

import com.github.alphahelix00.discordinator.d4j.handler.CommandHandlerD4J;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

/**
 * Created on:   7/7/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandUtils {

    /**
     * A static wrapper method that sends a message with request buffer included, to help reduce lines of code
     *
     * @param message     message to send
     * @param event       MessageReceivedEvent
     * @param msgBuilder  MessageBuilder
     * @param commandName command name
     */
    public static void messageRequestBuffer(String message, MessageReceivedEvent event, MessageBuilder msgBuilder, String commandName) {
        RequestBuffer.request(() -> {
            try {
                msgBuilder.withContent(message).build();
            } catch (MissingPermissionsException e) {
                CommandHandlerD4J.logMissingPerms(event, commandName, e);
            } catch (DiscordException e) {
                CommandHandlerD4J.logExceptionFail(event, commandName, e);
            }
        });
    }

    /**
     * A static wrapper method that sends a message with request buffer included, to help reduce lines of code
     *
     * @param message     message to send
     * @param style       style of message
     * @param event       MessageReceivedEvent
     * @param msgBuilder  MessageBuilder
     * @param commandName command name
     */
    public static void messageRequestBuffer(String message, MessageBuilder.Styles style, MessageReceivedEvent event, MessageBuilder msgBuilder, String commandName) {
        RequestBuffer.request(() -> {
            try {
                msgBuilder.withContent(message, style).build();
            } catch (MissingPermissionsException e) {
                CommandHandlerD4J.logMissingPerms(event, commandName, e);
            } catch (DiscordException e) {
                CommandHandlerD4J.logExceptionFail(event, commandName, e);
            }
        });
    }

}
