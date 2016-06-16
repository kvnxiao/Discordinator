package com.alphahelix00.discordinator;

import com.alphahelix00.discordinator.commands.CommandRegistry;
import com.alphahelix00.discordinator.permissions.PermissionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class Ordinator {

    public static final Logger LOGGER = LoggerFactory.getLogger(Ordinator.class);

    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final PermissionRegistry permissionRegistry = new PermissionRegistry();

    public static CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static PermissionRegistry getPermissionRegistry() {
        return permissionRegistry;
    }
}
