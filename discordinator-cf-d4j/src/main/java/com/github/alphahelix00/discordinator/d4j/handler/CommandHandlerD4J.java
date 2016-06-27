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
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
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
        for (Object obj : extraArgs) {
            if (obj instanceof MessageReceivedEvent) {
                event = (MessageReceivedEvent) obj;
            } else if (obj instanceof Boolean) {
                hasMention = (boolean) obj;
            }
        }
        if (event != null) {
            if (command instanceof CommandD4J) {
                permValid = checkPermission(((CommandD4J) command).getPermissions(), event);
                requireMention = ((CommandD4J) command).isRequireMention();
            }
            if (permValid && hasMention == requireMention) {
                LOGGER.info("Executing command: \"" + command.getPrefix() + command.getName() + "\", called by \"" + event.getMessage().getAuthor().getName()
                        + "\" in channel \"" + event.getMessage().getChannel().getName() + "\" on server \"" + event.getMessage().getGuild().getName() + "\"");
                ((CommandExecutorD4J) command).execute(args, event, new MessageBuilder(event.getClient()));
            } else {
                LOGGER.info(event.getMessage().getAuthor().getName() + " cannot execute command: " + command.getPrefix() + command.getName() + " right now (check permissions & mentions!)"
                        + "\" in channel \"" + event.getMessage().getChannel().getName() + "\" on server \"" + event.getMessage().getGuild().getName() + "\"");
            }
        }
    }


    @Override
    protected Command createMainCommand(MainCommand annotation, Object obj, Method method, boolean isMainCommand) {
        return CommandBuilderD4J.builder(
                annotation.name(), annotation.description())
                .prefix(annotation.prefix())
                .alias(annotation.alias())
                .enabled(true)
                .essential(annotation.essential())
                .subCommandNames(annotation.subCommands())
                .isMain(true)
                .permissions(getPermissions(method))
                .requireMention(requireMention(method))
                .allowPrivateMessage(allowPrivateMessage(method))
                .build(obj, method);
    }

    @Override
    protected Command createSubCommand(SubCommand annotation, Object obj, Method method, boolean isMainCommand) {
        return CommandBuilderD4J.builder(
                annotation.name(), annotation.description())
                .prefix(annotation.prefix())
                .alias(annotation.alias())
                .enabled(true)
                .essential(annotation.essential())
                .subCommandNames(annotation.subCommands())
                .isMain(false)
                .permissions(getPermissions(method))
                .requireMention(requireMention(method))
                .allowPrivateMessage(allowPrivateMessage(method))
                .build(obj, method);
    }

    private boolean checkPermission(EnumSet<Permissions> requiredPerms, MessageReceivedEvent event) {
        return (event.getMessage().getChannel().getModifiedPermissions(event.getMessage().getAuthor()).containsAll(requiredPerms));
    }

    private EnumSet<Permissions> getPermissions(Method method) {
        EnumSet<Permissions> permissionsEnumSet = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
        if (method.isAnnotationPresent(Permission.class)) {
            final Permission permissionAnn = method.getAnnotation(Permission.class);
            permissionsEnumSet.addAll(Arrays.asList(permissionAnn.permissions()));
        }
        return permissionsEnumSet;
    }

    private boolean requireMention(Method method) {
        if (method.isAnnotationPresent(Permission.class)) {
            final Permission permissionAnn = method.getAnnotation(Permission.class);
            return permissionAnn.requireMention();
        }
        return false;
    }

    private boolean allowPrivateMessage(Method method) {
        if (method.isAnnotationPresent(Permission.class)) {
            final Permission permissionAnn = method.getAnnotation(Permission.class);
            return permissionAnn.allowPrivateMessage();
        }
        return false;
    }

}
