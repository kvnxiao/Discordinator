package com.github.alphahelix00.discordinator.commands;

import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.handler.AbstractCommandHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author:      Kevin Xiao
 * Created on:  6/21/2016
 */
public class EssentialCommands {

    public static Command goToCommand(List<String> argsOriginal) {
        List<String> args = new ArrayList<>(argsOriginal);
        String mainCommand = args.get(0);
        args.remove(0);

        Command command = null;
        List<String> mainCommandArgs = AbstractCommandHandler.splitMessage(mainCommand);
        if (!mainCommandArgs.isEmpty()) {
            args.add(0, mainCommandArgs.get(1));
            command = AbstractCommandHandler.getCommand(mainCommandArgs.get(0), args);
        }
        return command;
    }

    public static class Enable extends Command {

        private final String NAME = "Enable Command";
        private final String DESCRIPTION = "enables the specified command";
        private final String[] ALIAS = {"enable"};
        private final String PREFIX = "!";

        @Override
        public boolean essential() {
            return true;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getDesc() {
            return DESCRIPTION;
        }

        @Override
        public List<String> getAlias() {
            return Arrays.asList(ALIAS);
        }

        @Override
        public List<String> getSubCommandNames() {
            return null;
        }

        @Override
        public boolean isMainCommand() {
            return true;
        }

        @Override
        public String getPrefix() {
            return PREFIX;
        }

        @Override
        public void execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
            if (args.size() > 0) {
                Command command = goToCommand(args);
                if (command != null) {
                    AbstractCommandHandler.enableCommand(command);
                }
            }
        }
    }

    public static class Disable extends Command {

        private final String NAME = "Disable Command";
        private final String DESCRIPTION = "disables the specified command";
        private final String[] ALIAS = {"disable"};
        private final String PREFIX = "!";

        @Override
        public boolean essential() {
            return true;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getDesc() {
            return DESCRIPTION;
        }

        @Override
        public List<String> getAlias() {
            return Arrays.asList(ALIAS);
        }

        @Override
        public List<String> getSubCommandNames() {
            return null;
        }

        @Override
        public boolean isMainCommand() {
            return true;
        }

        @Override
        public String getPrefix() {
            return PREFIX;
        }

        @Override
        public void execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
            if (args.size() > 0) {
                Command command = goToCommand(args);
                if (command != null) {
                    AbstractCommandHandler.disableCommand(command);
                }
            }
        }
    }

    public static class Help extends Command {

        private final String NAME = "Help Command";
        private final String DESCRIPTION = "lists all commands, or more info about a specific command";
        private final String[] ALIAS = {"help"};
        private final String PREFIX = "!";

        @Override
        public boolean essential() {
            return true;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getDesc() {
            return DESCRIPTION;
        }

        @Override
        public List<String> getAlias() {
            return Arrays.asList(ALIAS);
        }

        @Override
        public List<String> getSubCommandNames() {
            return null;
        }

        @Override
        public boolean isMainCommand() {
            return true;
        }

        @Override
        public String getPrefix() {
            return PREFIX;
        }

        @Override
        public void execute(List<String> args) throws IllegalAccessException, InvocationTargetException {
            if (args.isEmpty()) {
                String commandList = getCommandListQuote();
                System.out.println(commandList);
            } else {
                Command command = goToCommand(args);
                if (command != null) {
                    System.out.println(getCommandInfoQuote(command));
                }
            }
        }

        protected static String getCommandInfoQuote(Command command) {
            List<String> text = new ArrayList<>();
            text.add(String.format("%1$-12s: %2$s", "Command Name", command.getName()));
            text.add(String.format("%1$-12s: %2$s", "Description", command.getDesc()));
            text.add(String.format("%1$-12s: %2$s", "Sub-Commands", (command.getSubCommandNames() != null) ? command.getSubCommandNames().toString() : "N/A"));
            return String.join("\r\n", text);
        }

        protected static String getCommandListQuote() {
            List<Command> mainCommands = Discordinator.getCommandRegistry().getCommandList();
            List<String> text = new ArrayList<>();
            text.add("[COMMAND LIST]");
            Collections.sort(mainCommands, Command.COMMAND_COMPARATOR);
            mainCommands.forEach(command -> text.add(command.toFormattedString()));
            return String.join("\r\n", text);
        }
    }
}
