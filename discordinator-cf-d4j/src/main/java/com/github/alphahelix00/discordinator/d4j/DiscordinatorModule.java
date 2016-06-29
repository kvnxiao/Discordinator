package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.discordinator.d4j.handler.CommandHandlerD4J;
import com.github.alphahelix00.discordinator.d4j.handler.CommandListenerD4J;
import com.github.alphahelix00.ordinator.Ordinator;
import com.github.alphahelix00.ordinator.commands.CommandRegistry;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

/**
 * The main module class that is loaded by Discord4J's module loader.
 * <p>
 * <p>Created on:   6/17/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 */
public class DiscordinatorModule implements IModule {

    private IDiscordClient client;
    private CommandRegistry commandRegistry = Ordinator.getCommandRegistry();
    private CommandHandlerD4J commandHandler = (CommandHandlerD4J) commandRegistry.setCommandHandler(new CommandHandlerD4J(commandRegistry));
    private final CommandListenerD4J commandListener = new CommandListenerD4J(commandHandler);

    public DiscordinatorModule() {
    }

    @Override
    public boolean enable(IDiscordClient iDiscordClient) {
        this.client = iDiscordClient;
        handleMessages(client);
        return true;
    }

    @Override
    public void disable() {
        disableMessageHandle(client);
    }

    @Override
    public String getName() {
        return "Discordinator Command Framework";
    }

    @Override
    public String getAuthor() {
        return "alphahelix00";
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion() + " https://github.com/alphahelix00/Discordinator";
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        return "2.5.0-SNAPSHOT";
    }

    private void handleMessages(IDiscordClient client) {
        client.getDispatcher().registerListener(commandListener);
        client.getDispatcher().registerListener(this);
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        String[] botMentions = {client.getOurUser().mention(false), client.getOurUser().mention(true)};
        commandListener.setBotMention(botMentions);
    }

    private void disableMessageHandle(IDiscordClient client) {
        client.getDispatcher().unregisterListener(commandListener);
    }


}
