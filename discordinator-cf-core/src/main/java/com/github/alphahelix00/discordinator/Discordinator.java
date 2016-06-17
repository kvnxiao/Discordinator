package com.github.alphahelix00.discordinator;

import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.permissions.PermissionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class Discordinator {

    public static final Logger LOGGER = LoggerFactory.getLogger(Discordinator.class);

    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final PermissionRegistry permissionRegistry = new PermissionRegistry();

    public static CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static PermissionRegistry getPermissionRegistry() {
        return permissionRegistry;
    }
}
