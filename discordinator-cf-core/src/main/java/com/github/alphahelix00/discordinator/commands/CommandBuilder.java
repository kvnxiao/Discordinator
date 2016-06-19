package com.github.alphahelix00.discordinator.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on:   6/18/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandBuilder {

    protected String prefix = CommandDefaults.PREFIX;
    protected String name;
    protected String description;
    protected List<String> aliases;
    protected List<String> subCommandNames;
    protected boolean isMainCommand = true;

    private CommandBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandBuilder(String name, String description, List<String> aliases) {
        this(name, description);
        this.aliases = aliases;
    }

    public CommandBuilder(String name, String description, String... aliases) {
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

    public CommandBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CommandBuilder setSubCommandNames(List<String> subCommandNames) {
        this.subCommandNames = subCommandNames;
        return this;
    }

    public CommandBuilder setSubCommandNames(String... strings) {
        if (strings.length > 0) {
            this.subCommandNames = Arrays.asList(strings);
        }
        return this;
    }

    public CommandBuilder setIsMainCommand(boolean isMainCommand) {
        this.isMainCommand = isMainCommand;
        return this;
    }

    public Command build(Object object, Method method) {
        return new Command() {
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
            public void execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
                method.invoke(object, args);
            }
        };
    }

    public Command build(CommandExecutor executor) {
        return new Command() {
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
            public void execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
                executor.execute(args);
            }
        };
    }
}
