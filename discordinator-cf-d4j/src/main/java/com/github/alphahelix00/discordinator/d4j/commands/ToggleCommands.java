package com.github.alphahelix00.discordinator.d4j.commands;

import com.github.alphahelix00.discordinator.commands.Commands;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Author:      Kevin Xiao
 * Created on:  6/21/2016
 */
public class ToggleCommands extends Commands {

    public static class EnableD4J extends Commands.Enable implements CommandExecutorD4J {
        @Override
        public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
            super.execute(args);
        }
    }

    public static class DisableD4J extends Commands.Disable implements CommandExecutorD4J {
        @Override
        public void execute(List<String> args, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
            super.execute(args);
        }
    }
}
