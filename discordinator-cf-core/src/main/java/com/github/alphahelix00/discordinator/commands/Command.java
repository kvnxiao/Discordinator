package com.github.alphahelix00.discordinator.commands;

import com.google.common.base.Joiner;

import java.util.*;

/**
 * Created on:   6/15/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public abstract class Command implements CommandExecutor {

    public static final CommandComparator COMMAND_COMPARATOR = new CommandComparator();

    private Map<String, Command> subCommandMap = new HashMap<>();
    private boolean isEnabled = true;
    private final Joiner joiner = Joiner.on(", " + getPrefix());

    public abstract String getName();

    public abstract String getDesc();

    public abstract List<String> getAlias();

    public abstract List<String> getSubCommandNames();

    public abstract boolean isMainCommand();

    public abstract String getPrefix();

    public abstract boolean essential();

    public boolean isRepeating() {
        return getSubCommandNames().contains(getName());
    }

    public boolean hasSubCommand() {
        return getSubCommandNames() != null && getSubCommandNames().size() > 0;
    }

    public Map<String, Command> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
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

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String toString() {
        return getName() + " " + getAlias() + " - " + getDesc();
    }

    public String toFormattedString() {
        List<String> aliasList = getAlias();
        Collections.sort(aliasList);
        String aliases = getPrefix() + joiner.join(aliasList);
        String description = getDesc();
        return String.format("%1$-16s - %2$s", aliases, description);
    }

    public static class CommandComparator implements Comparator<Command> {
        @Override
        public int compare(Command o1, Command o2) {
            List<String> o1alias = o1.getAlias(), o2alias = o2.getAlias();
            Collections.sort(o1alias);
            Collections.sort(o2alias);
            if (o1.getPrefix().equals(o2.getPrefix())) {
                return o1alias.get(0).compareTo(o2alias.get(0));
            } else {
                return o1.getPrefix().compareTo(o2.getPrefix());
            }
        }
    }

}
