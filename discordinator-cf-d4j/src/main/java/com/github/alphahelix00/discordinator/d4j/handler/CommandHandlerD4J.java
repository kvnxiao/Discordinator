package com.github.alphahelix00.discordinator.d4j.handler;

import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.CommandExecutorD4J;
import com.github.alphahelix00.discordinator.d4j.commands.builder.CommandBuilderD4J;
import com.github.alphahelix00.discordinator.d4j.permissions.Permission;
import com.github.alphahelix00.ordinator.commands.Command;
import com.github.alphahelix00.ordinator.commands.CommandRegistry;
import com.github.alphahelix00.ordinator.commands.MainCommand;
import com.github.alphahelix00.ordinator.commands.SubCommand;
import com.github.alphahelix00.ordinator.commands.handler.AbstractCommandHandler;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * An AbstractCommandHandler implementation for Discord4J
 * <p>
 * <p>Created on:   6/17/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 */
public class CommandHandlerD4J extends AbstractCommandHandler {

    public CommandHandlerD4J(CommandRegistry commandRegistry) {
        super(commandRegistry);
    }

    /**
     * Executes the command for a Discord4J bot with the given list of arguments, including a MessageReceived event
     * and a boolean denoting whether or not the message contains a mention as extra arguments
     *
     * @param command   command to execute
     * @param args      list of string arguments to pass to the command
     * @param extraArgs extra arguments to be parsed, including: MessageReceivedEvent & message hasMention boolean
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Override
    protected void executeCommand(Command command, List<String> args, Object... extraArgs) throws IllegalAccessException, InvocationTargetException {
        MessageReceivedEvent event = null;
        boolean hasMention = false;

        for (Object obj : extraArgs) {
            if (obj instanceof MessageReceivedEvent) {
                event = (MessageReceivedEvent) obj;
            } else if (obj instanceof Boolean) {
                hasMention = (boolean) obj;
            }
        }

        if (event != null) {
            if (command instanceof CommandD4J) {
                parseCommandD4J((CommandD4J) command, args, event, hasMention);
            } else {
                parseCommandRegular(command, args, event, hasMention);
            }
        } else {
            LOGGER.error("MessageReceivedEvent is null! This shouldn't have happened...");
        }
    }

    private void parseCommandD4J(CommandD4J command, List<String> args, MessageReceivedEvent event, boolean hasMention) throws IllegalAccessException, InvocationTargetException {
        boolean validCall = checkPermission(command.getPermissions(), event) && (command.isRequireMention() == hasMention);
        if (validCall) {
            if (!event.getMessage().getChannel().isPrivate()) {
                // Command received on a public channel
                checkRemoveCallMessage(command.isRemoveCallMessage(), event);
                // Checks if command has force private reply on, and then have the bot reply accordingly
                if (!command.isForcePrivateReply()) {
                    // Attempt to execute command on public channels
                    executePublicly(command, args, event);
                } else {
                    // Attempt to execute command on private channels if force private boolean is true
                    executePrivately(command, args, event);
                }
            } else {
                // Command received from private message
                // Attempt to execute command on private channels only if allow private message boolean is true
                if (command.isAllowPrivateMessage()) {
                    executePrivately(command, args, event);
                }
            }
        } else {
            LOGGER.info(event.getMessage().getAuthor().getName() + " cannot execute command: " + command.getPrefix() + command.getName() + " right now (check permissions and/or mentions!)");
        }
    }

    /**
     * For calling commands that do not extend CommandD4J, but implement CommandExecutorD4J. These commands don't have permissions set,
     * and will be treated as essentially public-allowed and private-allowed commands, with no mentions.
     *
     * @param command
     * @param args
     * @param event
     * @param hasMention
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void parseCommandRegular(Command command, List<String> args, MessageReceivedEvent event, boolean hasMention) throws IllegalAccessException, InvocationTargetException {
        if (!hasMention) {
            if (!event.getMessage().getChannel().isPrivate()) {
                executePublicly(command, args, event);
            } else {
                executePrivately(command, args, event);
            }
        }
    }

    private void executePublicly(Command command, List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
        logCommandCall(event, command, false);
        ((CommandExecutorD4J) command).execute(args, event, new MessageBuilder(event.getClient()).withChannel(event.getMessage().getChannel()));
    }

    private void executePrivately(Command command, List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
        logCommandCall(event, command, true);
        RequestBuffer.request(() -> {
            try {
                ((CommandExecutorD4J) command).execute(args, event, new MessageBuilder(event.getClient())
                        .withChannel(event.getClient().getOrCreatePMChannel(event.getMessage().getAuthor())));
            } catch (DiscordException | RateLimitException e) {
                LOGGER.warn("Discord error in attempting to communicate in a private channel!");
            } catch (Exception e) {
                LOGGER.warn("Error in attempting to communicate in a private channel!");
            }
        });
    }

    private void checkRemoveCallMessage(boolean removeCallMessage, MessageReceivedEvent event) {
        if (removeCallMessage) {
            RequestBuffer.request(() -> {
                try {
                    event.getMessage().delete();
                } catch (DiscordException | MissingPermissionsException e) {
                    LOGGER.warn("Exception when attempting to to remove call message!");
                }
            });
        }
    }

    /**
     * Creates a new CommandD4J command from MainCommand annotations
     *
     * @param annotation MainCommand annotation
     * @param obj        object instance with methods
     * @param method     method containing the MainCommand annotation
     * @return new command with properties defined by annotated method
     */
    @Override
    protected Command createMainCommand(MainCommand annotation, Object obj, Method method) {
        CommandBuilderD4J commandBuilder = CommandBuilderD4J.builder(
                annotation.name(), annotation.description())
                .prefix(annotation.prefix())
                .alias(annotation.alias())
                .enabled(true)
                .essential(annotation.essential())
                .subCommandNames(annotation.subCommands())
                .isMain(true);
        return setPermissions(commandBuilder, method).build(obj, method);
    }

    /**
     * @param annotation SubCommand annotation
     * @param obj        object instance with methods
     * @param method     method containing the SubCommand annotation
     * @return new command with properties defined by annotated method
     */
    @Override
    protected Command createSubCommand(SubCommand annotation, Object obj, Method method) {
        CommandBuilderD4J commandBuilder = CommandBuilderD4J.builder(
                annotation.name(), annotation.description())
                .prefix(annotation.prefix())
                .alias(annotation.alias())
                .enabled(true)
                .essential(annotation.essential())
                .subCommandNames(annotation.subCommands())
                .isMain(false);
        return setPermissions(commandBuilder, method).build(obj, method);
    }

    /**
     * Checks whether or not the user has permissions to call this command
     *
     * @param requiredPerms EnumSet of permissions that the command requires
     * @param event         MessageReceivedEvent (contains author's message -> get author's maximal permission set)
     * @return whether or not the user has permissions to call this command
     */
    private boolean checkPermission(EnumSet<Permissions> requiredPerms, MessageReceivedEvent event) {
        return checkPermission(requiredPerms, (event.getMessage().getChannel().getModifiedPermissions(event.getMessage().getAuthor())));
    }

    /**
     * Checks whether or not a set of required permissions is included in the given permissions
     *
     * @param requiredPerms permissions that are required
     * @param givenPerms    given permissions
     * @return true if given permissions contains all of the required permissions, else false
     */
    public static boolean checkPermission(EnumSet<Permissions> requiredPerms, EnumSet<Permissions> givenPerms) {
        return givenPerms.containsAll(requiredPerms);
    }

    /**
     * Sets the permissions for a command currently being built
     *
     * @param commandBuilderD4J builder instance from which the command is being built by
     * @param method            method containing the MainCommand/SubCommand and Permission annotation(s)
     * @return current CommandBuilderD4J instance with permissions set
     */
    private CommandBuilderD4J setPermissions(CommandBuilderD4J commandBuilderD4J, Method method) {
        if (method.isAnnotationPresent(Permission.class)) {
            EnumSet<Permissions> permissionsEnumSet = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
            final Permission permissionAnn = method.getAnnotation(Permission.class);
            permissionsEnumSet.addAll(Arrays.asList(permissionAnn.permissions()));
            return commandBuilderD4J.permissions(permissionsEnumSet)
                    .allowPrivateMessage(permissionAnn.allowPrivateMessage())
                    .forcePrivateReply(permissionAnn.forcePrivateReply())
                    .removeCallMessage(permissionAnn.removeCallMessage())
                    .requireMention(permissionAnn.requireMention());
        }
        return commandBuilderD4J;
    }

    /**
     * Logs to console for general exceptions
     *
     * @param event       MessageReceivedEvent
     * @param commandName command name
     * @param e           exception
     */
    public static void logExceptionFail(MessageReceivedEvent event, String commandName, Exception e) {
        LOGGER.warn("Attempt to call " + commandName + " by user " + event.getMessage().getAuthor().getName()
                + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed!", e);
    }

    /**
     * Logs to console for MissingPermissionsException
     *
     * @param event       MessageReceivedEvent
     * @param commandName command name
     * @param e           exception
     */
    public static void logMissingPerms(MessageReceivedEvent event, String commandName, Exception e) {
        LOGGER.warn("BOT HAS INSUFFICIENT PRIVILEGES: Attempt to call " + commandName + " by user " + event.getMessage().getAuthor().getName()
                + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed!", e);
    }

    public static void logCommandCall(MessageReceivedEvent event, Command command, boolean isPrivateChannel) {
        LOGGER.info("Executing command: \"" + command.getPrefix() + command.getName() + "\", called by \"" + event.getMessage().getAuthor().getName() +
                (isPrivateChannel ? "in private chat" : "\" in channel \"" + event.getMessage().getChannel().getName() + "\" on server \"" + event.getMessage().getGuild().getName() + "\""));
    }

}
