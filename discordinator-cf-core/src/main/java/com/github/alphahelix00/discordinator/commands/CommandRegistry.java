package com.github.alphahelix00.discordinator.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Created on:   6/15/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandRegistry {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    private final Set<String> prefixes;
    private final Map<String, Map<String, Command>> prefixToCommandsMap;
    private final List<Command> mainCommandList;

    public CommandRegistry() {
        prefixes = new HashSet<>();
        prefixToCommandsMap = new HashMap<>();
        mainCommandList = new ArrayList<>();
        this.addPrefix(CommandDefaults.PREFIX);
    }

    /**
     * Adds the specified command to the registry, under the proper prefixed map
     *
     * @param command command to add
     */
    public void addCommand(Command command) {
        // Add prefix before adding command to registry
        String prefix = command.getPrefix();
        this.addPrefix(prefix);
        if (prefixToCommandsMap.containsKey(prefix)) {
            Map<String, Command> existingCommandMap = prefixToCommandsMap.get(prefix);
            // Only one command with the same name may be added
            if (!existingCommandMap.containsKey(command.getName())) {
                existingCommandMap.put(command.getName(), command);
                mainCommandList.add(command);
            }
        } else {
            LOGGER.warn("Attempt to add command: [" + command.toString() + "] unsuccessful!");
        }
    }

    /**
     * Adds the specified prefix to the prefix set
     * Creates a new command map for the specified prefix, if it does not already exist
     *
     * @param identifier prefix identifier
     */
    public void addPrefix(String identifier) {
        // Add prefix to set
        prefixes.add(identifier);
        // If prefix does not exist in command mapping, create a new map for that prefix
        if (!prefixToCommandsMap.containsKey(identifier)) {
            prefixToCommandsMap.put(identifier, new HashMap<>());
        }
    }

    /**
     * Returns a mutable list of all available commands in the command registry
     *
     * @return mutable list of all commands
     */
    public List<Command> getCommandList() {
        return mainCommandList;
    }

    /**
     * Returns an unmodifiable set of all prefixes in the command registry
     *
     * @return unmodifiable set of all command prefixes
     */
    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    /**
     * Checks if the command exists in the registry by providing
     * a specified command name
     *
     * @param prefix prefix attached to command
     * @param name   name of command
     * @return true if both prefix and command name exists in registry
     */
    public boolean commandExists(String prefix, String name) {
        if (containsPrefix(prefix)) {
            if (getCommandMap(prefix).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the command exists in the registry by providing
     * a specified command's alias
     *
     * @param prefix prefix attached to command
     * @param alias  alias of command
     * @return true if prefix exists, and command alias exists under the prefixed map
     */
    public boolean commandExistsAlias(String prefix, String alias) {
        if (containsPrefix(prefix)) {
            List<Command> commands = Collections.unmodifiableList(new ArrayList<>(prefixToCommandsMap.get(prefix).values()));
            for (Command command : commands) {
                if (command.getAlias().contains(alias)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the supplied prefix is a member of the prefixes set
     *
     * @param identifier prefix identifier
     * @return true if prefix is found in set
     */
    public boolean containsPrefix(String identifier) {
        for (String prefix : getPrefixes()) {
            if (identifier.equals(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the main command when supplied the prefix and alias of the command
     *
     * @param prefix prefix attached to command
     * @param alias  alias calling name of command
     * @return returns the command if it exists, else returns null
     */
    public Command getCommandByAlias(String prefix, String alias) {
        if (getPrefixes().contains(prefix)) {
            List<Command> commandList = Collections.unmodifiableList(new ArrayList<>(prefixToCommandsMap.get(prefix).values()));
            for (Command command : commandList) {
                if (command.isMainCommand() && command.getAlias().contains(alias)) {
                    return command;
                }
            }
        }
        return null;
    }

    /**
     * Returns the command when supplied the prefix and unique name of the command
     *
     * @param name   name of the command
     * @param prefix prefix attached to command
     * @return returns the command if it exists, else returns null
     */
    public Command getCommandByName(String name, String prefix) {
        if (getCommandMap(prefix).containsKey(name)) {
            return getCommandMap(prefix).get(name);
        }
        return null;
    }

    /**
     * Gets the command map pertaining to a specific prefix
     *
     * @param prefix prefix identifier
     * @return returns command map for specified prefix, null if prefix does not exist
     */
    public Map<String, Command> getCommandMap(String prefix) {
        if (prefixToCommandsMap.containsKey(prefix)) {
            return Collections.unmodifiableMap(prefixToCommandsMap.get(prefix));
        }
        return null;
    }
}
