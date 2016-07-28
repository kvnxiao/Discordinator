package com.github.alphahelix00.discordinator.d4j.commands.cmdimpl;

import com.github.alphahelix00.discordinator.d4j.commands.CommandExecutorD4J;
import com.github.alphahelix00.discordinator.d4j.handler.CommandHandlerD4J;
import com.github.alphahelix00.ordinator.commands.essential.EssentialCommands;
import sx.blah.discord.Discord4J;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * The Discord4J implementation of essential commands: enable, disable, and help commands
 * <p>
 * <p>Created on:   6/21/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 */
public class EssentialCommandsD4J extends EssentialCommands {

    private static final String PREFIX = "?";

    /**
     * Enable command which helps enable any disabled commands so that they may be called again
     */
    public static class EnableD4J extends EssentialCommands.Enable implements CommandExecutorD4J {

        public EnableD4J() {
            super(EssentialCommandsD4J.PREFIX, PREFIX + ALIAS.get(0) + " <command alias>");
        }

        @Override
        public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
            if (CommandHandlerD4J.checkPermission(EnumSet.of(Permissions.MANAGE_ROLES, Permissions.MANAGE_CHANNELS), event)) {
                return super.execute(args);
            } else {
                CommandHandlerD4J.replyUserMissingPerms(event, msgBuilder, "Enable Command");
                return Optional.empty();
            }
        }
    }

    /**
     * Disable command which disables any non-essential commands and prevents them from being called
     */
    public static class DisableD4J extends EssentialCommands.Disable implements CommandExecutorD4J {

        public DisableD4J() {
            super(EssentialCommandsD4J.PREFIX, PREFIX + ALIAS.get(0) + " <command alias>");
        }

        @Override
        public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
            if (CommandHandlerD4J.checkPermission(EnumSet.of(Permissions.MANAGE_ROLES, Permissions.MANAGE_CHANNELS), event)) {
                return super.execute(args);
            } else {
                CommandHandlerD4J.replyUserMissingPerms(event, msgBuilder, "Disable Command");
                return Optional.empty();
            }
        }
    }

    /**
     * Help command which will print a formatted list of all main commands, or print more information regarding a specific command
     */
    public static class HelpD4J extends EssentialCommands.Help implements CommandExecutorD4J {

        public HelpD4J() {
            super(EssentialCommandsD4J.PREFIX, PREFIX + ALIAS.get(0) + " || " + PREFIX + ALIAS.get(0) + " <command alias>");
        }

        @Override
        public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
            Optional optString = this.execute(args);
            if (optString.isPresent()) {
                try {
                    String helpString = (String) optString.get();
                    RequestBuffer.request(() -> {
                        try {
                            msgBuilder.withChannel(event.getMessage().getChannel()).withCode("xl", helpString).build();
                        } catch (DiscordException | MissingPermissionsException | RateLimitException e) {
                            Discord4J.LOGGER.warn("No permission to perform this action!");
                        }
                    });
                } catch (ClassCastException e) {
                    Discord4J.LOGGER.error(e.getMessage());
                }
            }
            return Optional.empty();
        }
    }

}
