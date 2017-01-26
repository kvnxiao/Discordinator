package com.github.alphahelix00.discordinator;

import com.github.alphahelix00.discordinator.d4j.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.CommandExecutorD4J;
import com.github.alphahelix00.discordinator.d4j.CommandParserD4J;
import com.github.alphahelix00.ordinator.Ordinator;
import com.github.alphahelix00.ordinator.commands.CommandBank;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

import java.util.EnumSet;
import java.util.LinkedList;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class Discordinator extends Ordinator implements IListener<MessageReceivedEvent> {

    private Discordinator(boolean autoLoadConfig) {
        this.commandBank = new CommandBank(autoLoadConfig);
        this.commandParser = new CommandParserD4J();
        this.commandExecutor = new CommandExecutorD4J();
    }

    public static Discordinator create() {
        return new Discordinator(false);
    }

    public static Discordinator create(boolean autoLoadConfig) {
        return new Discordinator(autoLoadConfig);
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        IMessage message = event.getMessage();

        LinkedList<String> tokens = this.tokenize(message.getContent());
        boolean hasBotMention = hasBotMention(message, tokens.peek());
        if (hasBotMention) {
            tokens.pop();
        }

        if (tokens.size() > 0) {
            CommandD4J command = (CommandD4J) this.getCommand(tokens);
            if (command != null) {
                String user = event.getMessage().getAuthor().getName();
                boolean isDm = event.getMessage().getChannel().isPrivate();

                // Log command execution for private (DM) and non-private channels
                if (isDm) {
                    LOGGER.debug("{} attempting to execute command: {}", user, command.getUniqueName());
                } else {
                    IGuild guild = event.getMessage().getGuild();
                    LOGGER.debug("{} in guild \"{}\" attempting to execute command: {}", user, guild.getName(), command.getUniqueName());
                }

                // Execute command if user has permission to do so
                if (hasPermission(command, event.getMessage(), isDm, hasBotMention)) {
                    try {
                        // Create channel for bot to reply to
                        IChannel channel = (command.isForcePrivateReply()) ? event.getClient().getOrCreatePMChannel(event.getMessage().getAuthor()) : event.getMessage().getChannel();

                        LOGGER.debug("Executing command... " + command.getUniqueName() + " with tokens: " + tokens.toString());
                        // Execute command
                        this.process(tokens, event, new MessageBuilder(event.getClient()).withChannel(channel));

                        // Remove call message if necessary
                        this.processMessage(command.isRemoveCallMsg(), isDm, event);

                    } catch (DiscordException | RateLimitException e) {
                        LOGGER.error("Exception in attempting to select channel for bot to reply to!");
                    }
                } else {
                    LOGGER.error(user + " has no permission to execute this command!");
                }
            }
        }
    }

    private boolean hasBotMention(IMessage message, String firstToken) {
        String botMention = message.getClient().getOurUser().mention(false);
        String botMentionNickname = message.getClient().getOurUser().mention(true);

        return firstToken.equals(botMention) || firstToken.equals(botMentionNickname);
    }

    private void processMessage(boolean isRemoveCallMsg, boolean isDm, MessageReceivedEvent event) {
        if (isRemoveCallMsg && !isDm) {
            RequestBuffer.request(() -> {
                try {
                    event.getMessage().delete();
                } catch (MissingPermissionsException e) {
                    LOGGER.warn("MissingPermissionsException when attempting to to remove call message! Does the bot have permissions to delete messages?");
                } catch (DiscordException e) {
                    LOGGER.warn("OOPS! DiscordException when attempting to to remove call message!");
                }
            });
        }
    }

    private boolean hasPermission(CommandD4J commandD4J, IMessage message, boolean isDm, boolean hasBotMention) {
        if (hasBotMention != commandD4J.isRequireMention()) {
            return false;
        }
        if (isDm) {
            return commandD4J.isAllowDm();
        }
        return checkPermission(commandD4J.getPermissions(), (message.getChannel().getModifiedPermissions(message.getAuthor())));
    }

    public static boolean checkPermission(EnumSet requiredPerms, EnumSet<Permissions> givenPerms) {
        return givenPerms.containsAll(requiredPerms);
    }

}
