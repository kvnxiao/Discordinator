package com.github.alphahelix00.discordinator.d4j.permissions;

import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation for Discord4J command Permissions
 * <p>
 * <p>Created on:   6/25/2016</p>
 * <p>Author:       Kevin Xiao (github.com/alphahelix00)</p>
 * @see PermissionDefaults
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {

    /**
     * Gets whether or not the command requires a bot mention in order to be called
     *
     * @return required mention boolean value, default to false
     */
    boolean requireMention() default PermissionDefaults.REQUIRE_MENTION;

    /**
     * Gets the array of Permissions required for this command
     *
     * @return array of Permissions, default to SEND_MESSAGES and READ_MESSAGES
     */
    Permissions[] permissions() default {Permissions.SEND_MESSAGES, Permissions.READ_MESSAGES};

    /**
     * Gets whether or not the command will trigger the bot to send a private message in response
     *
     * @return private message boolean value, default to false
     */
    boolean allowPrivateMessage() default PermissionDefaults.ALLOW_DM;

    /**
     * Gets whether or not the command call message will be removed upon execution
     *
     * @return remove call message boolean value, default to false
     */
    boolean removeCallMessage() default PermissionDefaults.REMOVE_CALL_MESSAGE;

}
