package com.github.alphahelix00.discordinator.d4j;

import org.slf4j.Logger;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

/**
 * Created on:   2017-01-27
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandUtils {

    public static void sendBuffered(MessageBuilder msgBuilder, Logger logger) {
        RequestBuffer.request(() -> {
            try {
                msgBuilder.send();
            } catch (MissingPermissionsException e) {
                logger.error("Executing command failed due to missing permissions!");
            } catch (DiscordException e) {
                logger.error("Encountered a Discord exception!");
                e.printStackTrace();
            }
        });
    }

    public static void sendBuffered(MessageBuilder msgBuilder) {
        RequestBuffer.request(() -> {
            try {
                msgBuilder.send();
            } catch (MissingPermissionsException | DiscordException ignored) {}
        });
    }

}
