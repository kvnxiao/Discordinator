package com.github.alphahelix00.discordinator;

import com.github.alphahelix00.discordinator.d4j.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.CommandExecutorD4J;
import com.github.alphahelix00.discordinator.d4j.CommandLoaderD4J;
import com.github.alphahelix00.discordinator.d4j.CommandParserD4J;
import com.github.alphahelix00.ordinator.Ordinator;
import com.github.alphahelix00.ordinator.commands.CommandBank;
import com.github.alphahelix00.ordinator.commands.CommandContext;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class Discordinator extends Ordinator implements IListener<MessageReceivedEvent> {

    private final CommandLoaderD4J commandLoader;

    private Discordinator() {
        this.commandBank = new CommandBank(this.config.isAutoLoadConfigs(), this.config.getCommandFolder());
        this.commandParser = new CommandParserD4J();
        this.commandExecutor = new CommandExecutorD4J();
        this.commandLoader = new CommandLoaderD4J();

        this.loadExternalCommands();
    }

    public static Discordinator create() {
        return new Discordinator();
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String messageContent = message.getContent();

        CommandContext context = CommandContext.of(messageContent);
        boolean hasBotMention = hasBotMention(message, context.alias());
        if (hasBotMention) {
            messageContent = context.args();
        }

        if (context.isValid()) {
            CommandD4J command = (CommandD4J) this.getCommand(context.alias());
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

                        // Execute command
                        this.process(messageContent, event, new MessageBuilder(event.getClient()).withChannel(channel));

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

    public void loadExternalCommands() {
        try {
            List<Path> paths = Files.list(Paths.get(this.config.getConfigFolder() + "/jars"))
                    .filter(name -> name.toString().toLowerCase().endsWith(".jar"))
                    .collect(Collectors.toList());
            paths.forEach(path -> commandLoader.load(path.toFile()));
        } catch (IOException e) {
            LOGGER.error("Could not get list of files in attempt to load external commands");
        }
        loadCommandsByClass();
        loadCommandsByAnnotations();
    }

    private void loadCommandsByAnnotations() {
        List<Class> annotatedClasses = commandLoader.getAnnotatedClasses();
        for (Class<?> annotatedClass : annotatedClasses) {
            this.addAnnotatedCommands(annotatedClass);
        }
    }

    private void loadCommandsByClass() {
        List<Class> commandClasses = commandLoader.getCommandClasses();
        for (Class<?> commandClass : commandClasses) {
            CommandD4J command = null;
            Constructor[] constructors = commandClass.getConstructors();
            for (Constructor constructor : constructors) {
                Class<?>[] paramTypes = constructor.getParameterTypes();

                // Check if external command has a constructor that relies on the CommandBank as a parameter
                if (paramTypes.length == 1 && paramTypes[0].equals(CommandBank.class)) {
                    try {
                        Constructor<?> bankConstructor = commandClass.getConstructor(CommandBank.class);
                        command = (CommandD4J) bankConstructor.newInstance(this.commandBank);
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        LOGGER.debug("External command class {} does not have a CommandBank reliant constructor.", commandClass.getSimpleName());
                    }
                } else {
                    // Try to instantiate external command with empty constructor
                    try {
                        command = (CommandD4J) commandClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.debug("External command class {} could not be instantiated with no-args constructor!", commandClass.getSimpleName());
                    }
                }

                // Load external command into command bank
                if (command != null) {
                    LOGGER.debug("Loading external command '{}' ...", command.getUniqueName());
                    this.addCommand(command);
                }
            }
        }
    }

}
