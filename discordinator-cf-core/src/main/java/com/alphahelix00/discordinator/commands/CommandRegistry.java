package com.alphahelix00.discordinator.commands;

import com.alphahelix00.discordinator.Discordinator;
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
    private final Map<String, Command> commandMap;

    public CommandRegistry() {
        prefixes = new HashSet<>();
        commandMap = new HashMap<>();
        this.addPrefix(CommandDefaults.PREFIX);
    }

    public void addCommand(Command command) throws Exception {
        commandMap.put(command.getName(), command);
    }

    public void addPrefix(String identifier) {
        prefixes.add(identifier);
    }

    /**
     * Returns an unmodifiable list of all available commands in the command registry
     * @return unmodifiable list of all commands
     */
    public Map<String, Command> getCommandList() {
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
        List<Command> commands = Collections.unmodifiableList(new ArrayList<>(commandMap.values()));
        for (Command command : commands) {
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

    public void execute(Command command, LinkedList<String> args) throws Exception {
        command.execute(args);
    }

    public Command getMainCommandByAlias(String alias) {
        List<Command> commandList = Collections.unmodifiableList(new ArrayList<>(commandMap.values()));
        for (Command command : commandList) {
            if (command.isMainCommand() && command.getAlias().contains(alias)) {
                return command;
            }
        }
        return null;
    }

    public Command getCommandByName(String name) {
        if (commandMap.containsKey(name)) {
            return commandMap.get(name);
        }
        return null;
    }
}
