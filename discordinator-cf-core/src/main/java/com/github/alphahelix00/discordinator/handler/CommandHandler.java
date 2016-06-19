package com.github.alphahelix00.discordinator.handler;

import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandBuilder;
import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.commands.SubCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created on:   6/15/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandHandler extends AbstractCommandHandler {

    private static final CommandHandler instance = new CommandHandler();

    private CommandHandler() {
        super();
    }

    public static CommandHandler getInstance() {
        return instance;
    }

    @Override
    public void executeCommand(Command command, List<String> args, Object... extraArgs) throws IllegalAccessException, InvocationTargetException {
        this.executeCommand(command, args);
    }

    @Override
    protected Command createMainCommand(MainCommand annotation, Object obj, Method method, boolean isMainCommand) {
        return new CommandBuilder(
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
        return new CommandBuilder(
                annotation.name(),
                annotation.desc(),
                Arrays.asList(annotation.alias()))
                .setPrefix(annotation.prefix())
                .setIsMainCommand(isMainCommand)
                .setSubCommandNames(Arrays.asList(annotation.subCommands()))
                .build(obj, method);
    }

}
