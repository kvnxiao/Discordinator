package com.github.alphahelix00.discordinator.d4j.commands.builder;

import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandDefaults;
import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.CommandExecutorD4J;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on:   6/18/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandBuilderD4J {

    private String prefix = CommandDefaults.PREFIX;
    private String name;
    private String description;
    private List<String> aliases;
    private List<String> subCommandNames;
    private boolean isMainCommand = true;

    private CommandBuilderD4J(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandBuilderD4J(String name, String description, List<String> aliases) {
        this(name, description);
        this.aliases = aliases;
    }

    public CommandBuilderD4J(String name, String description, String... aliases) {
        this(name, description);
        // Supplied string array of aliases
        if (aliases.length > 0) {
            this.aliases = Arrays.asList(aliases);
        } else {
            // If by chance the aliases argument was empty, set alias to the command name (first portion split by white space)
            this.aliases = new ArrayList<>(1);
            this.aliases.add(name.split("\\s+")[0]);
        }
    }

    public CommandBuilderD4J setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CommandBuilderD4J setSubCommandNames(List<String> subCommandNames) {
        this.subCommandNames = subCommandNames;
        return this;
    }

    public CommandBuilderD4J setSubCommandNames(String... strings) {
        if (strings.length > 0) {
            this.subCommandNames = Arrays.asList(strings);
        }
        return this;
    }

    public CommandBuilderD4J setIsMainCommand(boolean isMainCommand) {
        this.isMainCommand = isMainCommand;
        return this;
    }

    public Command build(Object object, Method method) {
        return new CommandD4J() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDesc() {
                return description;
            }

            @Override
            public List<String> getAlias() {
                return aliases;
            }

            @Override
            public List<String> getSubCommandNames() {
                return subCommandNames;
            }

            @Override
            public boolean isMainCommand() {
                return isMainCommand;
            }

            @Override
            public String getPrefix() {
                return prefix;
            }

            @Override
            public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
                method.invoke(object, args, event);
            }
        };
    }

    public Command build(CommandExecutorD4J executor) {
        return new CommandD4J() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDesc() {
                return description;
            }

            @Override
            public List<String> getAlias() {
                return aliases;
            }

            @Override
            public List<String> getSubCommandNames() {
                return subCommandNames;
            }

            @Override
            public boolean isMainCommand() {
                return isMainCommand;
            }

            @Override
            public String getPrefix() {
                return prefix;
            }

            @Override
            public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
                executor.execute(args, event);
            }
        };
    }
}
