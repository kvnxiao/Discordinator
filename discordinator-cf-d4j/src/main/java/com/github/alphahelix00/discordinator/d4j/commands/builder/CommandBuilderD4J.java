package com.github.alphahelix00.discordinator.d4j.commands.builder;

import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.CommandExecutorD4J;
import com.github.alphahelix00.ordinator.commands.Command;
import com.github.alphahelix00.ordinator.commands.CommandDefaults;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>Created on:   6/18/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 */
public class CommandBuilderD4J {

    private boolean isMain = CommandDefaults.ENABLED;
    private boolean isEssential = CommandDefaults.ESSENTIAL;
    private boolean isEnabled = CommandDefaults.ENABLED;
    private String prefix = "!";
    private final String name, description;
    private List<String> aliases;
    private Map<String, Command> subCommandMap = new HashMap<>();
    private Map<String, String> subCommandNames = new HashMap<>();
    private EnumSet<Permissions> permissions = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
    private boolean requireMention = false;
    private boolean allowPrivateMessage = false;
    private boolean removeCallMessage = false;

    public static CommandBuilderD4J builder(final String name, final String description) {
        return new CommandBuilderD4J(name, description);
    }

    private CommandBuilderD4J(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.aliases = Collections.singletonList(name);
    }

    public CommandBuilderD4J addSubCommand(Command subCommand) {
        String name = subCommand.getName();
        this.subCommandMap.put(name, subCommand);
        this.subCommandNames.put(name, name);
        return this;
    }

    public CommandBuilderD4J subCommandNames(String... names) {
        for (String name : names) {
            subCommandNames.put(name, name);
        }
        return this;
    }

    public CommandBuilderD4J alias(String... aliases) {
        if (aliases.length > 0) {
            this.aliases = Arrays.asList(aliases);
        } else {
            this.aliases = Collections.singletonList(name.split("\\s+")[0]);
        }
        return this;
    }

    public CommandBuilderD4J essential(boolean isEssential) {
        this.isEssential = isEssential;
        return this;
    }

    public CommandBuilderD4J isMain(boolean isMain) {
        this.isMain = isMain;
        return this;
    }

    public CommandBuilderD4J enabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    public CommandBuilderD4J prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CommandBuilderD4J permissions(EnumSet<Permissions> permissions) {
        this.permissions = permissions;
        return this;
    }

    public CommandBuilderD4J requireMention(boolean requireMention) {
        this.requireMention = requireMention;
        return this;
    }

    public CommandBuilderD4J allowPrivateMessage(boolean allowPrivateMessage) {
        this.allowPrivateMessage = allowPrivateMessage;
        return this;
    }

    public CommandBuilderD4J removeCallMessage(boolean removeCallMessage) {
        this.removeCallMessage = removeCallMessage;
        return this;
    }

    public CommandD4J build(CommandExecutorD4J executor) {
        return new CommandD4J(prefix, name, description, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, removeCallMessage) {

            @Override
            public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
                return executor.execute(args, event, msgBuilder);
            }
        };
    }

    public CommandD4J build(Object obj, Method method) {
        return new CommandD4J(prefix, name, description, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, removeCallMessage) {

            @Override
            public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
                return Optional.ofNullable(method.invoke(obj, args, event, msgBuilder));
            }
        };
    }
}
