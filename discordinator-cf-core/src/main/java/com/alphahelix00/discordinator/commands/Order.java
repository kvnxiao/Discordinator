package com.alphahelix00.discordinator.commands;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public abstract class Order implements Executor {

    public abstract String getName();
    public abstract String getDesc();
    public abstract String[] getAlias();
    public abstract String[] getSubCommands();

    public boolean hasSubCommand() {
        return (getSubCommands().length > 0);
    }

    @Override
    public String toString() {
        return getName() + " - " + getDesc();
    }
}
