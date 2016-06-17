package com.github.alphahelix00.discordinator.commands;

import java.util.List;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public abstract class Command implements CommandExecutor {

    public abstract String getName();
    public abstract String getDesc();
    public abstract List<String> getAlias();
    public abstract List<String> getSubCommands();
    public abstract boolean isMainCommand();
    public abstract String getPrefix();

    public boolean hasSubCommand() {
        if (getSubCommands() != null) {
            return getSubCommands().size() > 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return getName() + " - " + getDesc();
    }
}
