package com.github.alphahelix00.discordinator.d4j.commands;

import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.d4j.handler.CommandHandlerD4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 * Created on:   6/20/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public abstract class AbstractCommandModule implements IModule{

    protected IDiscordClient client;
    protected CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
    protected CommandHandlerD4J commandHandler = CommandHandlerD4J.getInstance();

    @Override
    public boolean enable(IDiscordClient iDiscordClient) {
        this.client = iDiscordClient;
        return true;
    }

}
