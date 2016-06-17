package com.github.alphahelix00.discordinator.d4j.commands;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/17/2016
 */
@FunctionalInterface
public interface CommandExecutorD4J {

    void execute(LinkedList<String> args, MessageReceivedEvent event) throws Exception;
}
