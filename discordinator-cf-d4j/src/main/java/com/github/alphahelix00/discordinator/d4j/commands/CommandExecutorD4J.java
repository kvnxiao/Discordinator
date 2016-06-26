package com.github.alphahelix00.discordinator.d4j.commands;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

/**
 * Created on:   6/17/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
@FunctionalInterface
public interface CommandExecutorD4J {

    Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException;
}
