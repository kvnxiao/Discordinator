package com.github.alphahelix00.discordinator.commands;

import com.github.alphahelix00.discordinator.handler.AbstractCommandHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author:      Kevin Xiao
 * Created on:  6/21/2016
 */
public class Commands {

    public static class Enable extends Command {

        private final String NAME = "Enable Command";
        private final String DESCRIPTION = "Enables the specified command";
        private final String[] ALIAS = {"enable"};
        private final String PREFIX = "!";

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
                String mainCommand = args.get(0);
                args.remove(0);

                List<String> mainCommandArgs = AbstractCommandHandler.splitMessage(mainCommand);
                if (!mainCommandArgs.isEmpty()) {
                    args.add(0, mainCommandArgs.get(1));
                    Command command = AbstractCommandHandler.getCommand(mainCommandArgs.get(0), new ArrayList<>(args));
                    if (command != null) {
                        AbstractCommandHandler.enableCommand(command);
                    } else {
                        String message = "";
                        for (String s : args) {
                            message += s + " ";
                        }
                        System.out.println(mainCommandArgs.get(0) + message + "command not found!");
                    }
                }
            }
        }
    }

    public static class Disable extends Command {

        private final String NAME = "Disable Command";
        private final String DESCRIPTION = "Disables the specified command";
        private final String[] ALIAS = {"disable"};
        private final String PREFIX = "!";

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
                String mainCommand = args.get(0);
                args.remove(0);

                List<String> mainCommandArgs = AbstractCommandHandler.splitMessage(mainCommand);
                if (!mainCommandArgs.isEmpty()) {
                    args.add(0, mainCommandArgs.get(1));
                    Command command = AbstractCommandHandler.getCommand(mainCommandArgs.get(0), new ArrayList<>(args));
                    if (command != null) {
                        AbstractCommandHandler.disableCommand(command);
                    } else {
                        String message = "";
                        for (String s : args) {
                            message += s + " ";
                        }
                        System.out.println(mainCommandArgs.get(0) + message + "command not found!");
                    }
                }
            }
        }
    }
}
