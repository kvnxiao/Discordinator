package com.github.alphahelix00.discordinator.d4j.commands.cmdimpl;

import com.github.alphahelix00.discordinator.d4j.commands.CommandExecutorD4J;
import com.github.alphahelix00.ordinator.commands.essential.EssentialCommands;
import sx.blah.discord.Discord4J;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

/**
 * Author:      Kevin Xiao
 * Created on:  6/21/2016
 */
public class EssentialCommandsD4J extends EssentialCommands {

    public static class EnableD4J extends EssentialCommands.Enable implements CommandExecutorD4J {
        @Override
        public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
            return super.execute(args);
        }
    }

    public static class DisableD4J extends EssentialCommands.Disable implements CommandExecutorD4J {
        @Override
        public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
            return super.execute(args);
        }
    }

    public static class HelpD4J extends EssentialCommands.Help implements CommandExecutorD4J {
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
