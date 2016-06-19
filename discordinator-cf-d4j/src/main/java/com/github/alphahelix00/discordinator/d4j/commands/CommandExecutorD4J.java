package com.github.alphahelix00.discordinator.d4j.commands;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
@FunctionalInterface
public interface CommandExecutorD4J {

    void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException;
}
