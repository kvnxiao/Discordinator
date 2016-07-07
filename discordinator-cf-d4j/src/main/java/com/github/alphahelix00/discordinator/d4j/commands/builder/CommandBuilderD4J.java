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
 * Builder class for building Discord4J Commands (CommandD4J objects)
 * <p>
 * <p>Created on:   6/18/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 */
public class CommandBuilderD4J {

    private boolean isMain = CommandDefaults.ENABLED;
    private boolean isEssential = CommandDefaults.ESSENTIAL;
    private boolean isEnabled = CommandDefaults.ENABLED;
    private String prefix = "!";
    private String usage = CommandDefaults.USAGE;
    private final String name, description;
    private List<String> aliases;
    private Map<String, Command> subCommandMap = new HashMap<>();
    private Map<String, String> subCommandNames = new HashMap<>();
    private EnumSet<Permissions> permissions = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
    private boolean requireMention = false;
    private boolean allowPrivateMessage = false;
    private boolean forcePrivateReply = false;
    private boolean removeCallMessage = false;

    /**
     * Gets a new builder instance to create new commands
     *
     * @param name        name of command to create
     * @param description description of command
     * @return new CommandBuilderD4J instance
     */
    public static CommandBuilderD4J builder(final String name, final String description) {
        return new CommandBuilderD4J(name, description);
    }

    private CommandBuilderD4J(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.aliases = Collections.singletonList(name);
    }

    /**
     * Adds a sub command to this command being created
     *
     * @param subCommand sub command to add to this command
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J addSubCommand(Command subCommand) {
        String name = subCommand.getName();
        this.subCommandMap.put(name, subCommand);
        this.subCommandNames.put(name, name);
        return this;
    }

    /**
     * Adds name identifiers of sub commands to the current command being built (used for annotated command building)
     *
     * @param names String array of sub command name identifiers
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J subCommandNames(String... names) {
        for (String name : names) {
            subCommandNames.put(name, name);
        }
        return this;
    }

    /**
     * Sets the aliases for the current command being built
     *
     * @param aliases String... array of command aliases. If empty -> alias = name
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J alias(String... aliases) {
        if (aliases.length > 0) {
            this.aliases = Arrays.asList(aliases);
        } else {
            this.aliases = Collections.singletonList(name.split("\\s+")[0]);
        }
        return this;
    }

    /**
     * Sets whether or not the command is an essential command
     *
     * @param isEssential boolean value denoting essentiality
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J essential(boolean isEssential) {
        this.isEssential = isEssential;
        return this;
    }

    /**
     * Sets whether or not the command is a main command. Set to false if it is sub command to avoid problems with the registry!
     *
     * @param isMain boolean denoting whether the command is a main or sub command
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J isMain(boolean isMain) {
        this.isMain = isMain;
        return this;
    }

    /**
     * Sets whether or not the command is enabled upon creation
     *
     * @param isEnabled boolean denoting whether the command is enabled or not
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J enabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    /**
     * Sets the prefix string for the command
     *
     * @param prefix prefix string identifier
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the permissions required by the user in order to successfully call this command
     *
     * @param permissions an EnumSet of 'Permissions' from the Discord4J library
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J permissions(EnumSet<Permissions> permissions) {
        this.permissions = permissions;
        return this;
    }

    /**
     * Sets whether or not the command requires a mention to be called
     *
     * @param requireMention boolean denoting command requires mention
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J requireMention(boolean requireMention) {
        this.requireMention = requireMention;
        return this;
    }

    /**
     * Sets the usage field information for the command
     *
     * @param usage usage field String
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J usage(String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Sets whether or not the command can be called through a private message to the bot (true), or if it can only be called
     * through a public channel (false)
     *
     * @param allowPrivateMessage boolean denoting whether or not the command can be issued through a private message
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J allowPrivateMessage(boolean allowPrivateMessage) {
        this.allowPrivateMessage = allowPrivateMessage;
        return this;
    }

    /**
     * Sets whether or not the bot's reply will be a private message (true), or will be in the public channel where
     * the command call was received (false)
     *
     * @param forcePrivateReply boolean denoting whether or not a bot reply to the command will be a private message
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J forcePrivateReply(boolean forcePrivateReply) {
        this.forcePrivateReply = forcePrivateReply;
        return this;
    }

    /**
     * Sets whether or not the bot will remove the caller's message on command execution
     *
     * @param removeCallMessage boolean denoting whether or not the bot will remove the command call message
     * @return current CommandBuilderD4J instance
     */
    public CommandBuilderD4J removeCallMessage(boolean removeCallMessage) {
        this.removeCallMessage = removeCallMessage;
        return this;
    }

    /**
     * Builds the command with a supplied CommandExecutorD4J
     *
     * @param executor CommandExecutorD4J object (recommended use of lambdas) with defining method body for execution
     * @return complete CommandD4J that has been built
     * @see CommandExecutorD4J
     */
    public CommandD4J build(CommandExecutorD4J executor) {
        return new CommandD4J(prefix, name, description, usage, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, forcePrivateReply, removeCallMessage) {

            @Override
            public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
                return executor.execute(args, event, msgBuilder);
            }
        };
    }

    /**
     * Builds the command with a supplied object instance and its method to be invoked upon execution. This is mainly for annotated commands.
     *
     * @param obj    object instance containing the method
     * @param method method for the command to invoke
     * @return complete CommandD4J that has been built
     */
    public CommandD4J build(Object obj, Method method) {
        return new CommandD4J(prefix, name, description, usage, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, forcePrivateReply, removeCallMessage) {

            @Override
            public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
                return Optional.ofNullable(method.invoke(obj, args, event, msgBuilder));
            }
        };
    }
}
