package com.github.alphahelix00.discordinator.d4j;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
@FunctionalInterface
public interface ICommandD4J<T> {

    T execute(LinkedList<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder, Object... optionals) throws InvocationTargetException, IllegalAccessException;

}
