package com.github.alphahelix00.discordinator.d4j.handler;

import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandExecutor;
import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.commands.SubCommand;
import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.CommandExecutorD4J;
import com.github.alphahelix00.discordinator.d4j.commands.builder.CommandBuilderD4J;
import com.github.alphahelix00.discordinator.handler.AbstractCommandHandler;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandHandlerD4J extends AbstractCommandHandler {

    private static final CommandHandlerD4J instance = new CommandHandlerD4J();
    private CommandHandlerD4J() {
        super();
    }
    public static CommandHandlerD4J getInstance() {
        return instance;
    }

    @Override
    public void executeCommand(Command command, List<String> args, Object... extraArgs) throws IllegalAccessException, InvocationTargetException {
        if (extraArgs[0] instanceof MessageReceivedEvent) {
            try {
                MessageReceivedEvent event = (MessageReceivedEvent) extraArgs[0];
                LOGGER.info("Executing command: \"" + command.getPrefix()+ command.getName() + "\", called by \"" + event.getMessage().getAuthor().getName()
                        + "\" in channel \"" + event.getMessage().getChannel().getName() + "\" on server \"" + event.getMessage().getGuild().getName() + "\"");
                ((CommandExecutorD4J) command).execute(args, event);
            } catch (ClassCastException e) {
                LOGGER.error("Unable to cast " + command.getName() + " as a D4J command");
            }
        }
    }

    @Override
    protected Command createMainCommand(MainCommand annotation, Object obj, Method method, boolean isMainCommand) {
        return new CommandBuilderD4J(
                annotation.name(),
                annotation.desc(),
                Arrays.asList(annotation.alias()))
                .setPrefix(annotation.prefix())
                .setIsMainCommand(isMainCommand)
                .setSubCommandNames(Arrays.asList(annotation.subCommands()))
                .build(obj, method);
    }

    @Override
    protected Command createSubCommand(SubCommand annotation, Object obj, Method method, boolean isMainCommand) {
        return new CommandBuilderD4J(
                annotation.name(),
                annotation.desc(),
                Arrays.asList(annotation.alias()))
                .setPrefix(annotation.prefix())
                .setIsMainCommand(isMainCommand)
                .setSubCommandNames(Arrays.asList(annotation.subCommands()))
                .build(obj, method);
    }
}
