package com.github.alphahelix00.discordinator.d4j.commands;

import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.EssentialCommands;
import sx.blah.discord.Discord4J;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Author:      Kevin Xiao
 * Created on:  6/21/2016
 */
public class EssentialCommandsD4J extends EssentialCommands {

    public static class EnableD4J extends EssentialCommands.Enable implements CommandExecutorD4J {
        @Override
        public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
            super.execute(args);
        }
    }

    public static class DisableD4J extends EssentialCommands.Disable implements CommandExecutorD4J {
        @Override
        public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
            super.execute(args);
        }
    }

    public static class HelpD4J extends EssentialCommands.Help implements CommandExecutorD4J {
        @Override
        public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
            if (args.isEmpty()) {
                RequestBuffer.request(() -> {
                    try {
                        String commandList = getCommandListQuote();
                        new MessageBuilder(event.getClient()).withChannel(event.getMessage().getChannel()).withCode("xl", commandList).build();
                    } catch (DiscordException | MissingPermissionsException | RateLimitException e) {
                        Discord4J.LOGGER.warn("No permissions to perform this action!");
                    }
                });
            } else {
                Command command = goToCommand(args);
                if (command != null) {
                    try {
                        new MessageBuilder(event.getClient()).withChannel(event.getMessage().getChannel()).withCode("xl", getCommandInfoQuote(command)).build();
                    } catch (DiscordException | MissingPermissionsException |RateLimitException e) {
                        Discord4J.LOGGER.warn("No permissions to perform this action!");
                    }
                }
            }
        }
    }
}
