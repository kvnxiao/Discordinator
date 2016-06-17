package com.github.alphahelix00.discordinator.d4j;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 * Author:      Kevin Xiao
 * Created on:  6/17/2016
 */
public class DiscordinatorModule implements IModule {

    @Override
    public boolean enable(IDiscordClient iDiscordClient) {
        return false;
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        return null;
    }
}
