package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.discordinator.d4j.handler.CommandListener;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class DiscordinatorModule implements IModule {

    private IDiscordClient client;
    private final CommandListener commandListener = new CommandListener();

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
        return "Discordinator.java Module";
    }

    @Override
    public String getAuthor() {
        return "alphahelix00";
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        return "2.5.0-SNAPSHOT";
    }

    private void handleMessages(IDiscordClient client) {
        client.getDispatcher().registerListener(commandListener);
    }

    private void disableMessageHandle(IDiscordClient client) {
        client.getDispatcher().unregisterListener(commandListener);
    }


}
