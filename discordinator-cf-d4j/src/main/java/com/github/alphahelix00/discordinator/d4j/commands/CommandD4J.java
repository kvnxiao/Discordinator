package com.github.alphahelix00.discordinator.d4j.commands;

import com.github.alphahelix00.ordinator.commands.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public abstract class CommandD4J extends Command implements CommandExecutorD4J{

    private EnumSet<Permissions> permissions;
    private boolean requireMention;
    private boolean allowPrivateMessage;

    public CommandD4J(String prefix, String name, String description, List<String> aliases, boolean isMain, boolean isEnabled, boolean isEssential, Map<String, Command> subCommandMap, Map<String, String> subCommandNames,
                      EnumSet<Permissions> permissions, boolean requireMention, boolean allowPrivateMessage) {
        super(prefix, name, description, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames);
        this.permissions = permissions;
        this.requireMention = requireMention;
        this.allowPrivateMessage = allowPrivateMessage;
    }

    @Override
    public Optional execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
        return Optional.empty();
    }

    public EnumSet<Permissions> getPermissions() {
        return permissions;
    }

    public boolean isRequireMention() {
        return requireMention;
    }

    public boolean isAllowPrivateMessage() {
        return allowPrivateMessage;
    }

}
