package com.github.alphahelix00.discordinator.utils;

import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.CommandAnnotation;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.commands.Command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandHandler {

    private static final CommandRegistry commandRegistry = Discordinator.getCommandRegistry();

    public static void parseForCommands(String message) throws Exception {
        // Take in a message string and parse it for main and sub commands
        LinkedList<String> commandArgs = new LinkedList<>(Arrays.asList(message.split(" ")));
        executeCommands(commandArgs);
    }

    public static void executeCommands(LinkedList<String> args) throws Exception {
        // Iterate through message to see if they are commands
        Command command = getMainCommand(args);
        if (command != null) {
            executeCommand(command, args);
            while (command != null && command.hasSubCommand()) {
                Command subCommand = getSubCommand(command, args);
                if (subCommand != null) {
                    executeCommand(subCommand, args);
                }
                command = subCommand;
            }
        }
    }

    public static void executeCommand(Command command, LinkedList<String> args) throws Exception {
        commandRegistry.execute(command, args);
    }

    public static Command getMainCommand(LinkedList<String> args) {
        // Check first message arg to see if it is a main command
        String mainCommandAlias = args.removeFirst();
        if (commandRegistry.commandExists(mainCommandAlias)) {
            return commandRegistry.getMainCommandByAlias(mainCommandAlias);
        }
        return null;
    }

    public static Command getSubCommand(Command parentCommand, LinkedList<String> args) {
        List<String> subCommandNames = parentCommand.getSubCommands();
        List<Command> subCommands = new ArrayList<>();
        for (String name : Collections.unmodifiableList(subCommandNames)) {
            Command subCommand = commandRegistry.getCommandByName(name);
            if (subCommand != null) {
                subCommands.add(commandRegistry.getCommandByName(name));
            }
        }
        for (Command command : subCommands) {
            if (command.getAlias().contains(args.peek())) {
                args.removeFirst();
                return command;
            }
        }
        return null;
    }

    public static void registerAnnotatedCommands(Object object) throws Exception {
        for (Method method : object.getClass().getMethods()) {
            // If method has annotated command class above it
            if (method.isAnnotationPresent(CommandAnnotation.class)) {
                // Get annotated command from object's method
                CommandAnnotation annotatedCommand = method.getAnnotation(CommandAnnotation.class);
                // Generate a command based on the annotations, and add to registry
                String prefix = annotatedCommand.prefix();
                commandRegistry.addPrefix(prefix);

                // Get parameters of method
                Parameter[] parameters = method.getParameters();
                Object[] arguments = new Object[parameters.length];

                // For use in D4J or other projects that require different method parameters as arguments
//                if (parameters.length > 1) {
//                    for (int i = 1; i < parameters.length; i++) {
//                        switch (parameters[i].getType().getSimpleName()) {
//                            default:
//                                arguments[i] = null;
//                                break;
//                        }
//                    }
//                }
                // Add command to registry
                commandRegistry.addCommand(new Command() {
                    @Override
                    public boolean isMainCommand() {
                        return annotatedCommand.mainCommand();
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
                    public List<String> getSubCommands() {
                        return Arrays.asList(annotatedCommand.subCommands());
                    }

                    @Override
                    public void execute(LinkedList<String> args) throws Exception {
                        arguments[0] = args;
                        method.invoke(object, arguments);
                    }
                });
            }
        }
    }
}
