package com.github.alphahelix00.discordinator.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on:   6/15/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public abstract class Command implements CommandExecutor {

    private Map<String, Command> subCommandMap = new HashMap<>();

    public abstract String getName();
    public abstract String getDesc();
    public abstract List<String> getAlias();
    public abstract List<String> getSubCommandNames();
    public abstract boolean isMainCommand();
    public abstract String getPrefix();

    public boolean hasSubCommand() {
        return getSubCommandNames() != null && getSubCommandNames().size() > 0;
    }

    public boolean isRepeating() {
        return getSubCommandNames().contains(getName());
    }

    @Override
    public String toString() {
        return getName() + ": " + getAlias().toString() + " - " + getDesc();
    }

    public void addSubCommand(Command command) {
        this.subCommandMap.put(command.getName(), command);
    }

    public boolean subCommandExists(String prefix, String name) {
        for (Command command : getSubCommands().values()) {
            if (command.getPrefix().equals(prefix) && command.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Command> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }
}
