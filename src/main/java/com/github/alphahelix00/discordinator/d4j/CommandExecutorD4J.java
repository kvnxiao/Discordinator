package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.ordinator.commands.Command;
import com.github.alphahelix00.ordinator.commands.CommandExecutor;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandExecutorD4J extends CommandExecutor {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T executeCommand(LinkedList<String> args, Command<?> command, Object... optionals) throws InvocationTargetException, IllegalAccessException {
        try {
            CommandD4J discordCommand = (CommandD4J) command;
            MessageReceivedEvent event = (MessageReceivedEvent) optionals[0];
            MessageBuilder msgBuilder = (MessageBuilder) optionals[1];
            return (T) discordCommand.execute(args, event, msgBuilder, Arrays.copyOfRange(optionals, 2, optionals.length));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

}
