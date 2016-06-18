package com.github.alphahelix00.discordinator.d4j.handler;

import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.utils.CommandHandler;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Author:      Kevin Xiao
 * Created on:  6/17/2016
 */
public class CommandHandlerD4J extends CommandHandler {

    private static final CommandHandlerD4J commandHandler = new CommandHandlerD4J();

    private CommandHandlerD4J() {

    }

    public static CommandHandlerD4J getInstance() {
        return commandHandler;
    }

    public void parseForCommands(String message, MessageReceivedEvent event) {
        // Take in a message string and parse it for main and sub commands
        LinkedList<String> commandArgs = new LinkedList<>(Arrays.asList(message.split("\\s+")));
        try {
            executeCommands(commandArgs, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeCommand(Command command, LinkedList<String> args, Object... extraArgs) throws Exception {
        if (extraArgs.length == 1 && extraArgs[0] instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) extraArgs[0];
            ((CommandD4J) command).execute(args, event);
        } else {
            super.executeCommand(command, args, extraArgs);
        }
    }

    @Override
    public void registerAnnotatedCommands(Object object) throws Exception {
        for (Method method : object.getClass().getMethods()) {
            // If method has annotated command class above it
            if (method.isAnnotationPresent(MainCommand.class)) {
                // Get annotated command from object's method
                MainCommand annotatedCommand = method.getAnnotation(MainCommand.class);
                // Generate a command based on the annotations, and add to registry
                commandRegistry.addCommand(new CommandD4J() {
                    @Override
                    public String getPrefix() {
                        return annotatedCommand.prefix();
                    }

                    @Override
                    public boolean isMainCommand() {
                        return true;
                    }

                    @Override
                    public String getName() {
                        return annotatedCommand.name();
                    }

                    @Override
                    public String getDesc() {
                        return annotatedCommand.desc();
                    }

                    @Override
                    public List<String> getAlias() {
                        return Arrays.asList(annotatedCommand.alias());
                    }

                    @Override
                    public List<String> getSubCommandNames() {
                        return Arrays.asList(annotatedCommand.subCommands());
                    }

                    @Override
                    public void execute(LinkedList<String> args, MessageReceivedEvent event) throws Exception {
                        method.invoke(object, args, event);
                    }
                });
            }
        }
    }
}
