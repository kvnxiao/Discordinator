package com.alphahelix00.discordinator.commands;

import java.util.*;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class CommandRegistry {

    private final Set<String> prefixes;
    private final List<Order> commandList;
    private final Map<String[], Order> aliasToCommands;

    public CommandRegistry() {
        prefixes = new HashSet<>();
        commandList = new ArrayList<>();
        aliasToCommands = new HashMap<>();
        this.addPrefix(CommandDefaults.PREFIX);
    }

    public void addCommand(Order command) {
        commandList.add(command);
        aliasToCommands.put(command.getAlias(), command);
    }

    public void addPrefix(String identifier) {
        prefixes.add(identifier);
    }

    /**
     * Returns an unmodifiable list of all available commands in the command registry
     * @return unmodifiable list of all commands
     */
    public List<Order> getCommandList() {
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

}
