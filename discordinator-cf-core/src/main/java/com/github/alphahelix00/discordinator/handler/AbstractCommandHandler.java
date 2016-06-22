package com.github.alphahelix00.discordinator.handler;

import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.commands.SubCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created on:   6/18/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public abstract class AbstractCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("CommandHandler");
    protected static final CommandRegistry commandRegistry = Discordinator.getCommandRegistry();

    protected AbstractCommandHandler() {

    }

    // Required implementations in sub-concrete classes
    public abstract void executeCommand(Command command, List<String> args, Object... extraArgs) throws IllegalAccessException, InvocationTargetException;

    protected abstract Command createMainCommand(MainCommand annotation, Object obj, Method method, boolean isMainCommand);

    protected abstract Command createSubCommand(SubCommand annotation, Object obj, Method method, boolean isMainCommand);

    /**
     * Returns true if message is a valid command by
     * checking for a message's prefix and subsequent argument
     *
     * @param message   message String to validate
     * @param extraArgs extra arguments to pass in (any object)
     * @return true if message was successfully validated for prefix and parsed
     * and subsequent argument is an existing command in registry
     */
    public boolean validateMessage(String message, Object... extraArgs) {
        // Split message into an arguments list
        List<String> messageArgs = splitMessage(message);
        // Get first argument from message and check for prefix match
        if (!messageArgs.isEmpty()) {
            String prefix = messageArgs.get(0);
            messageArgs.remove(0);

            parseForCommands(prefix, messageArgs, extraArgs);
            return true;
        }
        return false;
    }

    /**
     * Parses the message arguments for commands and attempts to execute if successful
     *
     * @param prefix      prefix identifier
     * @param messageArgs list of arguments parsed from message
     * @param extraArgs   extra arguments passed in (any object)
     */
    private void parseForCommands(String prefix, List<String> messageArgs, Object... extraArgs) {
        // Get first argument from message
        String argFirst = messageArgs.get(0);
        messageArgs.remove(0);
        // Use and prefix and first argument String as command alias to try and retrieve a command from registry
        Command command = commandRegistry.getCommandByAlias(prefix, argFirst);
        if (command != null) {
            executeCommands(command, messageArgs, extraArgs);
        } else {
            LOGGER.warn("Request to execute command \"" + prefix + argFirst + "\" not found in registry!");
        }
    }

    /**
     * Execute the command with given arguments
     *
     * @param command     command to execute
     * @param messageArgs list of arguments parsed from message
     * @param extraArgs   extra arguments passed in (any object)
     */
    public void executeCommands(Command command, List<String> messageArgs, Object... extraArgs) {
        if (command.isEnabled()) {
            try {
                executeCommand(command, messageArgs, extraArgs);
            } catch (IllegalAccessException e) {
                LOGGER.error("IllegalAccessException in attempting to execute command " + command.getName(), e);
            } catch (InvocationTargetException e) {
                LOGGER.error("InvocationTargetException in attempting to execute command " + command.getName(), e);
            } finally {
                // Check if current command has sub command
                if (command.hasSubCommand() && !messageArgs.isEmpty()) {
                    // Get next argument from message
                    String subCommandAlias = messageArgs.get(0);
                    messageArgs.remove(0);
                    // Iterate through list of sub commands and check if alias of those commands contain specific argument
                    Collections.unmodifiableCollection(command.getSubCommands().values()).forEach(subCommand -> {
                        if (subCommand.getAlias().contains(subCommandAlias)) {
                            executeCommands(subCommand, messageArgs, extraArgs);
                        }
                    });
                }
            }
        } else {
            LOGGER.warn(command.toString() + " is disabled and will not be executed!");
        }
    }

    /**
     * Executes the command with given arguments
     *
     * @param command the command to execute
     * @param args    arguments for command
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void executeCommand(Command command, List<String> args) throws IllegalAccessException, InvocationTargetException {
        command.execute(args);
    }

    public List<String> registerAnnotatedCommands(Object obj) {
        List<Method> methodsMain = new ArrayList<>();
        List<Method> methodsSub = new ArrayList<>();

        // Parse class for methods with command annotations
        for (Method method : obj.getClass().getMethods()) {
            if (method.isAnnotationPresent(MainCommand.class)) {
                methodsMain.add(method);
            } else if (method.isAnnotationPresent(SubCommand.class)) {
                methodsSub.add(method);
            }
        }

        // Create and register commands from the main and sub methods
        if (!methodsMain.isEmpty()) {
            registerMainCommands(obj, methodsMain, methodsSub);
        } else {
            LOGGER.warn("No main methods detected in " + obj.getClass().getSimpleName());
        }
        return getMainCommandNames(methodsMain);
    }

    private List<String> getMainCommandNames(List<Method> methods) {
        List<String> commandNames = new ArrayList<>();
        for (Method method : methods) {
            final MainCommand annotation = method.getAnnotation(MainCommand.class);
            commandNames.add(annotation.name());
        }
        return Collections.unmodifiableList(commandNames);
    }

    public Command registerCommand(Command command) {
        if (!commandRegistry.commandExists(command.getPrefix(), command.getName())) {
            LOGGER.info("Registering main command: \"" + command.getName() + "\"");
            commandRegistry.addCommand(command);
        }
        return command;
    }

    private void registerMainCommands(Object obj, List<Method> methodsMain, List<Method> methodsSub) {
        for (Method method : methodsMain) {
            final MainCommand annotation = method.getAnnotation(MainCommand.class);
            if (!commandRegistry.commandExists(annotation.prefix(), annotation.name())) {
                Command command = createMainCommand(annotation, obj, method, true);
                // Check if command is a repeating command or if it has sub commands
                if (command.isRepeating()) {
                    command.addSubCommand(command);
                    LOGGER.info("Registering repeating command: \"" + command.getName() + "\"");
                } else if (command.hasSubCommand()) {
                    LOGGER.info("Registering main command: \"" + command.getName() + "\"");
                    registerSubCommands(obj, methodsSub, command);
                } else {
                    LOGGER.info("Registering main command: \"" + command.getName() + "\"");
                }
                commandRegistry.addCommand(command);
            }
        }
    }

    private void registerSubCommands(Object obj, List<Method> methodsSub, Command parentCommand) {
        // Iterate through all sub command names declared in parent command
        for (String subCommandName : parentCommand.getSubCommandNames()) {
            // Check and match each method annotation's name field with sub command name
            for (Method method : methodsSub) {
                final SubCommand annotation = method.getAnnotation(SubCommand.class);
                if (subCommandName.equals(annotation.name()) && !parentCommand.subCommandExists(annotation.prefix(), annotation.name())) {
                    Command command = createSubCommand(annotation, obj, method, false);
                    LOGGER.info("Registering sub command: \"" + command.getName() + "\" of parent \"" + parentCommand.getName() + "\"");
                    if (command.hasSubCommand()) {
                        registerSubCommands(obj, methodsSub, command);
                    }
                    parentCommand.addSubCommand(command);
                }
            }
        }
    }

    public static Command getCommand(String message) {
        List<String> args = splitMessage(message);
        if (!args.isEmpty()) {
            String prefix = args.get(0);
            args.remove(0);

            // Gets the main command alias first
            return getCommand(prefix, args);
        }
        return null;
    }

    public static Command getCommand(String prefix, List<String> args) {
        // Gets the main command alias first
        String argFirst = args.get(0);
        args.remove(0);

        // Use and prefix and first argument String as command alias to try and retrieve a command from registry
        Command command = commandRegistry.getCommandByAlias(prefix, argFirst);
        if (command != null) {
            return (!args.isEmpty()) ? getSubCommand(command, args) : command;
        }
        return null;
    }

    private static Command getSubCommand(Command parentCommand, List<String> args) {
        // Get the next sub command alias from args
        String subCommandAlias = args.get(0);
        args.remove(0);
        // Iterate through list of sub commands and check if alias of those commands contain specific argument
        for (Command command : Collections.unmodifiableCollection(parentCommand.getSubCommands().values())) {
            if (command.getAlias().contains(subCommandAlias)) {
                if (!args.isEmpty()) {
                    return getSubCommand(command, args);
                } else {
                    return command;
                }
            }
        }
        return null;
    }

    public static List<String> splitMessage(String message) {
        // Split message into an arguments list
        List<String> messageArgs = new LinkedList<>(Arrays.asList(message.split("\\s+")));
        // Get first argument from message and check for prefix match
        String argFirst = messageArgs.get(0);
        messageArgs.remove(0);
        String prefix;

        for (String identifier : commandRegistry.getPrefixes()) {
            if (argFirst.startsWith(identifier)) {
                prefix = identifier;
                argFirst = argFirst.substring(identifier.length());
                messageArgs.add(0, argFirst);
                messageArgs.add(0, prefix);
                return messageArgs;
            }
        }
        return messageArgs;
    }

    public static void enableCommand(Command command) {
        if (!command.essential() && !command.isEnabled()) {
            LOGGER.info("Enabling command: " + command.toString());
            command.setEnabled(true);
        }
    }

    public static void disableCommand(Command command) {
        if (!command.essential() && command.isEnabled()) {
            LOGGER.info("Disabling command: " + command.toString());
            command.setEnabled(false);
        }
    }

}