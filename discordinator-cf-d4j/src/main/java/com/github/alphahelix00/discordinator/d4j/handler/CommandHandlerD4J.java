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
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * <p>Created on:   6/17/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 */
public class CommandHandlerD4J extends AbstractCommandHandler {

    public CommandHandlerD4J(CommandRegistry commandRegistry) {
        super(commandRegistry);
    }

    @Override
    protected void executeCommand(Command command, List<String> args, Object... extraArgs) throws IllegalAccessException, InvocationTargetException {
        MessageReceivedEvent event = null;
        boolean permValid = true;
        boolean requireMention = false;
        boolean hasMention = false;
        boolean removeCallMessage = false;
        for (Object obj : extraArgs) {
            if (obj instanceof MessageReceivedEvent) {
                event = (MessageReceivedEvent) obj;
            } else if (obj instanceof Boolean) {
                hasMention = (boolean) obj;
            }
        }
        if (event != null) {
            final MessageReceivedEvent messageReceivedEvent = event;
            if (command instanceof CommandD4J) {
                permValid = checkPermission(((CommandD4J) command).getPermissions(), messageReceivedEvent);
                requireMention = ((CommandD4J) command).isRequireMention();
                removeCallMessage = ((CommandD4J) command).isRemoveCallMessage();
            }
            if (permValid && hasMention == requireMention) {
                LOGGER.info("Executing command: \"" + command.getPrefix() + command.getName() + "\", called by \"" + messageReceivedEvent.getMessage().getAuthor().getName()
                        + "\" in channel \"" + messageReceivedEvent.getMessage().getChannel().getName() + "\" on server \"" + messageReceivedEvent.getMessage().getGuild().getName() + "\"");
                if (removeCallMessage) {
                    RequestBuffer.request(() -> {
                       try {
                           messageReceivedEvent.getMessage().delete();
                       } catch (DiscordException | MissingPermissionsException e) {
                           LOGGER.warn("Exception when attempting to to remove call message!");
                       }
                    });
                }
                ((CommandExecutorD4J) command).execute(args, messageReceivedEvent, new MessageBuilder(messageReceivedEvent.getClient()));
            } else {
                LOGGER.info(messageReceivedEvent.getMessage().getAuthor().getName() + " cannot execute command: " + command.getPrefix() + command.getName() + " right now (check permissions & mentions!)"
                        + "\" in channel \"" + messageReceivedEvent.getMessage().getChannel().getName() + "\" on server \"" + messageReceivedEvent.getMessage().getGuild().getName() + "\"");
            }
        }
    }

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

    private boolean checkPermission(EnumSet<Permissions> requiredPerms, MessageReceivedEvent event) {
        return (event.getMessage().getChannel().getModifiedPermissions(event.getMessage().getAuthor()).containsAll(requiredPerms));
    }

    private CommandBuilderD4J setPermissions(CommandBuilderD4J commandBuilderD4J, Method method) {
        if (method.isAnnotationPresent(Permission.class)) {
            EnumSet<Permissions> permissionsEnumSet = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
            final Permission permissionAnn = method.getAnnotation(Permission.class);

            permissionsEnumSet.addAll(Arrays.asList(permissionAnn.permissions()));
            return commandBuilderD4J.permissions(permissionsEnumSet)
                    .allowPrivateMessage(permissionAnn.allowPrivateMessage())
                    .removeCallMessage(permissionAnn.removeCallMessage())
                    .requireMention(permissionAnn.requireMention());
        }
        return commandBuilderD4J;
    }

    public static void logExceptionFail(MessageReceivedEvent event, String commandName, Exception e) {
        LOGGER.warn("Attempt to call " + commandName + " by user " + event.getMessage().getAuthor().getName()
                + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed!", e);
    }

    public static void logMissingPerms(MessageReceivedEvent event, String commandName, Exception e) {
        LOGGER.warn("INSUFFICIENT PRIVILEGES: Attempt to call " + commandName + " by user " + event.getMessage().getAuthor().getName()
                + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed!", e);
    }

}
