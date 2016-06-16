package com.alphahelix00.discordinator.utils;

import com.alphahelix00.discordinator.Ordinator;
import com.alphahelix00.discordinator.commands.Command;
import com.alphahelix00.discordinator.commands.CommandRegistry;
import com.alphahelix00.discordinator.commands.Order;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandHandler {

    private static final CommandRegistry commandRegistry = Ordinator.getCommandRegistry();

    public static void parseForCommands(String message) {
        LinkedList<String> commandArgs = new LinkedList<>(Arrays.asList(message.split(" ")));
        // Take in a message string and parse it for main and sub commands
        // Create a new linked list of purely main and sub commands, and a string for args
        // execute command
    }

    public static void executeCommand(LinkedList<String> args) {
        // given the alias from the linked list of strings,
        // retrieve command from alias map

        // then execute command with proper args
        // commandRegistry.execute();
    }

    public static void registerCommands(Object object) {
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
                for (int i = 0; i < parameters.length; i++) {
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
                // Add command to registry
                commandRegistry.addCommand(new Order() {
                    @Override
                    public String getName() {
                        return annotatedCommand.name();
                    }

                    @Override
                    public String getDesc() {
                        return annotatedCommand.desc();
                    }

                    @Override
                    public String[] getAlias() {
                        return annotatedCommand.alias();
                    }

                    @Override
                    public String[] getSubCommands() {
                        return annotatedCommand.subCommands();
                    }

                    @Override
                    public void execute(LinkedList<String> args) throws Exception {
                        method.invoke(object, arguments);
                    }
                });
            }
        }
    }
}
