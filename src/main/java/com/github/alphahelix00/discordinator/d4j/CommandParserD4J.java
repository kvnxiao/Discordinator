package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.discordinator.d4j.perms.PermissionDefaults;
import com.github.alphahelix00.discordinator.d4j.perms.PermissionLevel;
import com.github.alphahelix00.ordinator.commands.Command;
import com.github.alphahelix00.ordinator.commands.CommandAnn;
import com.github.alphahelix00.ordinator.commands.CommandParser;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandParserD4J extends CommandParser {

    @Override
    protected Command createCommand(Object instance, Method method, CommandAnn annotation) {
        final EnumSet<Permissions> permissionSet = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);

        if (method.isAnnotationPresent(PermissionLevel.class)) {
            PermissionLevel permission = method.getAnnotation(PermissionLevel.class);
            permissionSet.addAll(Arrays.asList(permission.permissions()));

            return new CommandD4J(annotation.prefix(), annotation.uniqueName(), annotation.description(), annotation.usage(), permissionSet, permission.allowPrivate(), permission.removeCall(), permission.forcePrivate(), permission.reqMention(), annotation.aliases()) {
                @Override
                public Object execute(LinkedList args, MessageReceivedEvent event, MessageBuilder msgBuilder, Object... optionals) throws InvocationTargetException, IllegalAccessException {
                    return method.invoke(instance, args, event, msgBuilder, optionals);
                }
            };
        } else {
            return new CommandD4J(annotation.prefix(), annotation.uniqueName(), annotation.description(), annotation.usage(), permissionSet, PermissionDefaults.ALLOW_DM, PermissionDefaults.REMOVE_CALL_MESSAGE, PermissionDefaults.FORCE_PRIVATE_REPLY, PermissionDefaults.REQUIRE_MENTION, annotation.aliases()) {
                @Override
                public Object execute(LinkedList args, MessageReceivedEvent event, MessageBuilder msgBuilder, Object... optionals) throws InvocationTargetException, IllegalAccessException {
                    return method.invoke(instance, args, event, msgBuilder, optionals);
                }
            };
        }
    }
}
