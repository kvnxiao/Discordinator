package com.github.alphahelix00.discordinator.d4j.perms;

import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionLevel {

    /**
     * Gets whether or not the command requires a bot mention in order to be called
     *
     * @return required mention boolean value, default to false
     */
    boolean reqMention() default PermissionDefaults.REQUIRE_MENTION;

    /**
     * Gets the array of Permissions required for this command
     *
     * @return array of Permissions, default to SEND_MESSAGES and READ_MESSAGES
     */
    Permissions[] permissions() default {Permissions.SEND_MESSAGES, Permissions.READ_MESSAGES};

    /**
     * Gets whether or not the user can call this command through a private message to the bot
     *
     * @return allowed to call command through private message boolean value, default to false
     */
    boolean allowPrivate() default PermissionDefaults.ALLOW_DM;

    /**
     * Gets whether or not the command will trigger the bot to send a private message in response
     *
     * @return private message reply boolean value, default to false
     */
    boolean forcePrivate() default PermissionDefaults.FORCE_PRIVATE_REPLY;

    /**
     * Gets whether or not the command call message will be removed upon execution
     *
     * @return remove call message boolean value, default to false
     */
    boolean removeCall() default PermissionDefaults.REMOVE_CALL_MESSAGE;

}

