package com.github.alphahelix00.discordinator.d4j.commands;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

/**
 * A functional interface implemented by CommandD4J objects, containing a single method body which can be
 * used as the assignment target for a lambda expression or method reference
 * <p>
 * <p>Created on:   6/17/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 *
 * @see CommandD4J
 */
@FunctionalInterface
public interface CommandExecutorD4J {

    /**
     * Required method to be overrided when declaring commands to declare what actions the command will perform
     *
     * @param args       a list of string as arguments for the command
     * @param event      MessageReceivedEvent
     * @param msgBuilder a MessageBuilder pertaining to the message received event
     * @return Optional object that may or may not contain another object
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException;
}
