package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.discordinator.Discordinator;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 * Created on:   2017-01-27
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class DiscordinatorModule implements IModule {

    private IDiscordClient client;
    private final Discordinator discordinator;

    private static final String NAME = "Discordinator Command Framework";
    private static final String AUTHOR = "alphahelix00";
    private static final String VERSION = "2.0a";
    private static final String MIN_D4J_VERSION = "2.7.0";

    public DiscordinatorModule() {
        this.discordinator = Discordinator.create();
    }

    @Override
    public boolean enable(final IDiscordClient client) {
        this.client = client;
        client.getDispatcher().registerListener(this.discordinator);
        return true;
    }

    @Override
    public void disable() {
        client.getDispatcher().unregisterListener(this.discordinator);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        return MIN_D4J_VERSION;
    }

}
