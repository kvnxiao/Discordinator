package com.alphahelix00.discordinator.commands;

import java.util.*;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandRegistry {

    private final Set<String> prefixes;
    private final Map<String, Order> commandMap;

    public CommandRegistry() {
        prefixes = new HashSet<>();
        commandMap = new HashMap<>();
        this.addPrefix(CommandDefaults.PREFIX);
    }

    public void addCommand(Order command) throws Exception {
        commandMap.put(command.getName(), command);
    }

    public void addPrefix(String identifier) {
        prefixes.add(identifier);
    }

    /**
     * Returns an unmodifiable list of all available commands in the command registry
     * @return unmodifiable list of all commands
     */
    public Map<String, Order> getCommandList() {
        return Collections.unmodifiableMap(commandMap);
    }

    /**
     * Returns an unmodifiable set of all prefixes in the command registry
     * @return unmodifiable set of all command prefixes
     */
    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    public boolean commandExists(String alias) {
        List<Order> commands = Collections.unmodifiableList(new ArrayList<>(commandMap.values()));
        for (Order command : commands) {
            if (command.getAlias().contains(alias)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the supplied prefix is a member of the prefixes set
     * @param identifier String of prefix to look for
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

    public void execute(Order command, LinkedList<String> args) throws Exception {
        command.execute(args);
    }

    public Order getMainCommandByAlias(String alias) {
        List<Order> commandList = Collections.unmodifiableList(new ArrayList<>(commandMap.values()));
        for (Order command : commandList) {
            if (command.isMainCommand() && command.getAlias().contains(alias)) {
                return command;
            }
        }
        return null;
    }

    public Order getCommandByName(String name) {
        if (commandMap.containsKey(name)) {
            return commandMap.get(name);
        }
        return null;
    }
}
