package com.github.alphahelix00.discordinator.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandRegistry {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    private final Set<String> prefixes;
    private final Map<String, Map<String, Command>> prefixToCommandsMap;
    private final List<Map<String, Command>> commandList;

    public CommandRegistry() {
        prefixes = new HashSet<>();
        prefixToCommandsMap = new HashMap<>();
        commandList = new ArrayList<>();
        this.addPrefix(CommandDefaults.PREFIX);
    }

    /**
     * Adds the specified command to the registry, under the proper prefixed map
     * @param command       command to add
     * @throws Exception
     */
    public void addCommand(Command command) throws Exception {
        // Add prefix before adding command to registry
        String prefix = command.getPrefix();
        this.addPrefix(prefix);
        if (prefixToCommandsMap.containsKey(prefix)) {
            Map<String, Command> existingCommandMap = prefixToCommandsMap.get(prefix);
            existingCommandMap.put(command.getName(), command);
            commandList.add(existingCommandMap);
        } else {
            LOGGER.warn("Attempt to add command: [" + command.toString() + "] unsuccessful!");
        }
    }

    /**
     * Adds the specified prefix to the prefix set, if it does not already exist
     * Creates a new command map for the specified prefix, if it does not already exist
     * @param identifier    prefix identifer
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
     * Returns an unmodifiable list of all available commands in the command registry
     * @return unmodifiable list of all commands
     */
    public List<Map<String, Command>> getCommandMapList() {
        return Collections.unmodifiableList(commandList);
    }

    /**
     * Returns an unmodifiable set of all prefixes in the command registry
     * @return unmodifiable set of all command prefixes
     */
    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    /**
     * Checks if the command exists in the registry
     * @param alias     alias of command
     * @param prefix    prefix attached to command
     * @return          true if prefix exists, and command alias exists under the prefixed map
     */
    public boolean commandExists(String alias, String prefix) {
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
     * @param identifier    prefix identifier
     * @return              true if prefix is found in set
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
     * @param alias     alias calling name of command
     * @param prefix    prefix attached to command
     * @return          returns the command if it exists, else returns null
     */
    public Command getMainCommandByAlias(String alias, String prefix) {
        List<Command> commandList = Collections.unmodifiableList(new ArrayList<>(prefixToCommandsMap.get(prefix).values()));
        for (Command command : commandList) {
            if (command.isMainCommand() && command.getAlias().contains(alias)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Returns the command when supplied the command's unique name and prefix
     * @param name      name of the command
     * @param prefix    prefix attached to command
     * @return          returns the command if it exists, else returns null
     */
    public Command getCommandByName(String name, String prefix) {
        if (getCommandMap(prefix).containsKey(name)) {
            return getCommandMap(prefix).get(name);
        }
        return null;
    }

    /**
     * Gets the command map pertaining to a specific prefix
     * @param prefix    prefix identifier
     * @return          returns null if prefix does not exist
     */
    public Map<String, Command> getCommandMap(String prefix) {
        if (prefixToCommandsMap.containsKey(prefix)) {
            return Collections.unmodifiableMap(prefixToCommandsMap.get(prefix));
        }
        return null;
    }
}
