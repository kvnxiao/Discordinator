package com.github.alphahelix00.discordinator.d4j.commands;

import com.github.alphahelix00.ordinator.commands.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The command class, containing all the defining information of a Discord4J command
 * <p>
 * <p>Created on:   6/17/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 *
 * @see CommandExecutorD4J
 */
public abstract class CommandD4J extends Command implements CommandExecutorD4J {

    private EnumSet<Permissions> permissions;
    private boolean requireMention;
    private boolean allowPrivateMessage;
    private boolean removeCallMessage;

    /**
     * All field-argument constructor
     *
     * @param prefix              prefix for command
     * @param name                name of command
     * @param description         description of command
     * @param aliases             List of string for aliases
     * @param isMain              boolean -> is command a main or sub command?
     * @param isEnabled           boolean -> is command enabled?
     * @param isEssential         boolean -> is command essential?
     * @param subCommandMap       set to an empty new HashMap if the command will have no sub commands
     * @param subCommandNames     set to an empty new HashMap if the command will have no sub commands
     * @param permissions         EnumSet of Permissions denoting required user permissions in order to execute command
     * @param requireMention      boolean -> requires a bot mention to be called?
     * @param allowPrivateMessage boolean -> bot reply is sent as a private message (true) or as a channel message (false)?
     * @param removeCallMessage   boolean -> remove call message upon command execution?
     */
    public CommandD4J(String prefix, String name, String description, List<String> aliases, boolean isMain, boolean isEnabled, boolean isEssential, Map<String, Command> subCommandMap, Map<String, String> subCommandNames,
                      EnumSet<Permissions> permissions, boolean requireMention, boolean allowPrivateMessage, boolean removeCallMessage) {
        super(prefix, name, description, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames);
        this.permissions = permissions;
        this.requireMention = requireMention;
        this.allowPrivateMessage = allowPrivateMessage;
        this.removeCallMessage = removeCallMessage;
    }

    /**
     * Unused implementation
     *
     * @param args
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Override
    public Optional execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
        return Optional.empty();
    }

    /**
     * Gets permissions required for this command
     *
     * @return EnumSet of Permissions
     */
    public EnumSet<Permissions> getPermissions() {
        return permissions;
    }

    /**
     * Returns whether or not this command must include a bot mention to be called
     *
     * @return boolean denoting whether or not a bot mention is required
     */
    public boolean isRequireMention() {
        return requireMention;
    }

    /**
     * Returns whether or not this command will have the bot reply in a private message
     *
     * @return boolean denoting whether or not the message will be sent privately or publicly
     */
    public boolean isAllowPrivateMessage() {
        return allowPrivateMessage;
    }

    /**
     * Returns whether or not the command call will be removed after parsing
     *
     * @return boolean denoting whether or not the caller's message will be removed after command execution
     */
    public boolean isRemoveCallMessage() {
        return removeCallMessage;
    }

}
