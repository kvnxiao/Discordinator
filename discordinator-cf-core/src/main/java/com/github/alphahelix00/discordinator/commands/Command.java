package com.github.alphahelix00.discordinator.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
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
        if (getSubCommandNames() != null) {
            return getSubCommandNames().size() > 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return getName() + " " + getAlias().toString() + " - " + getDesc();
    }

    public void addSubCommand(Command command) {
        this.subCommandMap.put(command.getName(), command);
    }

    public Map<String, Command> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }
}
