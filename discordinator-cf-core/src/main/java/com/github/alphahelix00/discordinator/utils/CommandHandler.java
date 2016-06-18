package com.github.alphahelix00.discordinator.utils;

import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.commands.SubCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandHandler {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);
    protected static final CommandRegistry commandRegistry = Discordinator.getCommandRegistry();

    public void parseForCommands(String message) {
        // Take in a message string and parse it for main and sub commands
        LinkedList<String> commandArgs = new LinkedList<>(Arrays.asList(message.split("\\s+")));
        try {
            executeCommands(commandArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeCommands(LinkedList<String> args, Object... extraArgs) throws Exception {
        // Iterate through message to see if they are commands
        Command command = getMainCommand(args);
        if (command != null) {
            executeCommand(command, args, extraArgs);
            while (command != null && command.hasSubCommand()) {
                Command subCommand = getSubCommand(command, args);
                if (subCommand != null) {
                    executeCommand(command, args, extraArgs);
                }
                command = subCommand;
            }
        }
    }

    /**
     * Executes the command with given arguments
     * @param command   the command to execute
     * @param args      arguments for command
     * @throws Exception
     */
    public void executeCommand(Command command, LinkedList<String> args) throws Exception {
        command.execute(args);
    }

    public void executeCommand(Command command, LinkedList<String> args, Object... extraArgs) throws Exception {
        this.executeCommand(command, args);
    }

    public Command getMainCommand(LinkedList<String> args) {
        // Check first message arg to see if it is a main command
        String firstArg = args.removeFirst();
        String prefix;
        for (String identifier : commandRegistry.getPrefixes()) {
            if (firstArg.startsWith(identifier)) {
                prefix = identifier;
                String mainCommandAlias = firstArg.substring(prefix.length());
                if (commandRegistry.commandExists(mainCommandAlias, prefix)) {
                    return commandRegistry.getMainCommandByAlias(mainCommandAlias, prefix);
                }
            }
        }
        return null;
    }

    public Command getSubCommand(Command parentCommand, LinkedList<String> args) {
        List<String> subCommandNames = parentCommand.getSubCommandNames();
        List<Command> subCommands = new ArrayList<>();
        for (String name : Collections.unmodifiableList(subCommandNames)) {
            Command subCommand = commandRegistry.getCommandByName(name, parentCommand.getPrefix());
            if (subCommand != null) {
                subCommands.add(commandRegistry.getCommandByName(name, parentCommand.getPrefix()));
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

    public void registerAnnotatedCommands(Object object) throws Exception {
        List<Method> mainMethods = new ArrayList<>();
        List<Method> subMethods = new ArrayList<>();
        for (Method method : object.getClass().getMethods()) {
            // Parse class for methods with command annotations
            if (method.isAnnotationPresent(MainCommand.class)) {
                mainMethods.add(method);
            } else if (method.isAnnotationPresent(SubCommand.class)) {
                subMethods.add(method);
            }
        }
        if (mainMethods.size() > 0) {
            createMainCommand(object, mainMethods, subMethods);
        } else {
            LOGGER.warn("No main methods detected in " + object.getClass().getSimpleName());
        }
    }

    public void createMainCommand(Object object, List<Method> mainMethods, List<Method> subMethods) throws Exception {
        for (Method method : mainMethods) {
            MainCommand annotatedCommand = method.getAnnotation(MainCommand.class);
            // Generate a command based on the annotations, and add to registry
            Command mainCommand = new Command() {
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
                public void execute(LinkedList<String> args) throws Exception {
                    method.invoke(object, args);
                }
            };
            // Check if main command is a repeating command
            if (mainCommand.getSubCommandNames().contains(mainCommand.getName())) {
                mainCommand.addSubCommand(mainCommand);
            }
            // Check if main command has sub commands
            if (mainCommand.hasSubCommand()) {
                createSubCommand(object, mainCommand, subMethods);
            }
            commandRegistry.addCommand(mainCommand);
        }
    }

    public void createSubCommand(Object object, Command parentCommand, List<Method> subMethods) {
        // Iterate through all sub command names declared in parent command's
        for (String subCommandName : parentCommand.getSubCommandNames()) {
            for (Method method : subMethods) {
                SubCommand annotatedCommand = method.getAnnotation(SubCommand.class);
                if (subCommandName.equals(annotatedCommand.name())) {
                    Command subCommand = new Command() {
                        @Override
                        public String getPrefix() {
                            return annotatedCommand.prefix();
                        }

                        @Override
                        public boolean isMainCommand() {
                            return false;
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
                        public void execute(LinkedList<String> args) throws Exception {
                            method.invoke(object, args);
                        }
                    };
                    if (subCommand.hasSubCommand()) {
                        createSubCommand(object, subCommand, subMethods);
                    }
                    // Add subCommand to main command
                    parentCommand.addSubCommand(subCommand);
                }
            }
        }
    }
}
