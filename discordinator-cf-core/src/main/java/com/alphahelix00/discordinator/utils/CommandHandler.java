package com.alphahelix00.discordinator.utils;

import com.alphahelix00.discordinator.Ordinator;
import com.alphahelix00.discordinator.commands.Command;
import com.alphahelix00.discordinator.commands.CommandRegistry;
import com.alphahelix00.discordinator.commands.Order;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandHandler {

    private static final CommandRegistry commandRegistry = Ordinator.getCommandRegistry();

    public static void parseForCommands(String message) throws Exception {
        // Take in a message string and parse it for main and sub commands
        LinkedList<String> commandArgs = new LinkedList<>(Arrays.asList(message.split(" ")));
        LinkedList<Order> commands = createCommandList(commandArgs);
        executeCommand(commands, commandArgs);
    }

    public static LinkedList<Order> createCommandList(LinkedList<String> args) {
        // Iterate through message to see if they are commands
        LinkedList<Order> commands = new LinkedList<>();
        Order command = getMainCommand(args);
        if (command != null) {
            commands.add(command);
        }
        if (command != null) {
            while (command != null && command.hasSubCommand()) {
                Order subCommand = getSubCommand(command, args);
                if (subCommand != null) {
                    commands.add(subCommand);
                }
                command = subCommand;
            }
        }
        return commands;
    }

    public static Order getMainCommand(LinkedList<String> args) {
        // Check first message arg to see if it is a main command
        String mainCommandAlias = args.removeFirst();
        if (commandRegistry.commandExists(mainCommandAlias)) {
            return commandRegistry.getMainCommandByAlias(mainCommandAlias);
        }
        return null;
    }

    public static Order getSubCommand(Order parentCommand, LinkedList<String> args) {
        List<String> subCommandNames = parentCommand.getSubCommands();
        List<Order> subCommands = new ArrayList<>();
        for (String name : Collections.unmodifiableList(subCommandNames)) {
            Order subCommand = commandRegistry.getCommandByName(name);
            if (subCommand != null) {
                subCommands.add(commandRegistry.getCommandByName(name));
            }
        }
        for (Order command : subCommands) {
            if (command.getAlias().contains(args.peek())) {
                args.removeFirst();
                return command;
            }
        }
        return null;
    }

    public static void executeCommand(LinkedList<Order> commands, LinkedList<String> args) throws Exception {
        for (Order command : commands) {
            commandRegistry.execute(command, args);
        }
    }

    public static void registerAnnotatedCommands(Object object) throws Exception {
        for (Method method : object.getClass().getMethods()) {
            // If method has annotated command class above it
            if (method.isAnnotationPresent(Command.class)) {
                // Get annotated command from object's method
                Command annotatedCommand = method.getAnnotation(Command.class);
                // Generate a command based on the annotations, and add to registry
                String prefix = annotatedCommand.prefix();
                commandRegistry.addPrefix(prefix);

                // Get parameters of method
                Parameter[] parameters = method.getParameters();
                Object[] arguments = new Object[parameters.length];
                if (parameters.length > 1) {
                    for (int i = 1; i < parameters.length; i++) {
                        switch (parameters[i].getType().getSimpleName()) {
                            // TODO: remove these special cases, for testing purposes only
                            case "boolean":
                                arguments[i] = true;
                                break;
                            case "int":
                                arguments[i] = 0;
                                break;
                            default:
                                arguments[i] = null;
                                break;
                        }
                    }
                }
                // Add command to registry
                commandRegistry.addCommand(new Order() {
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
