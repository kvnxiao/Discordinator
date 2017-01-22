package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.discordinator.d4j.perms.PermissionDefaults;
import com.github.alphahelix00.ordinator.commands.Command;
import com.github.alphahelix00.ordinator.commands.Defaults;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.LinkedList;

/**
 * Created on:   2017-01-22
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public abstract class CommandD4J<T> extends Command<T> implements ICommandD4J<T> {

    private EnumSet<Permissions> permissions = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);;
    private boolean isAllowDm = PermissionDefaults.ALLOW_DM;
    private boolean isRemoveCallMsg = PermissionDefaults.REMOVE_CALL_MESSAGE;
    private boolean isForcePrivateReply = PermissionDefaults.FORCE_PRIVATE_REPLY;
    private boolean isRequireMention = PermissionDefaults.REQUIRE_MENTION;

    public CommandD4J(String prefix,
                      String uniqueName,
                      String description,
                      String usage,
                      EnumSet<Permissions> permissions,
                      boolean isAllowDm,
                      boolean isRemoveCallMsg,
                      boolean isForcePrivateReply,
                      boolean isRequireMention,
                      final String... aliases) {
        super(prefix, uniqueName, description, usage, aliases);
        this.permissions = permissions;
        this.isAllowDm = isAllowDm;
        this.isRemoveCallMsg = isRemoveCallMsg;
        this.isForcePrivateReply = isForcePrivateReply;
        this.isRequireMention = isRequireMention;
    }

    public CommandD4J(String prefix, String uniqueName, String description, String usage, String... alias) {
        super(prefix, uniqueName, description, usage, alias);
    }

    public CommandD4J(String nameAlias) {
        super(nameAlias);
    }

    public static <T> CommandBuilderD4J<T> builder(final String nameAlias) {
        return new CommandBuilderD4J<>(nameAlias);
    }

    public static <T> CommandBuilderD4J<T> builder(final String prefix, final String uniqueName) {
        return new CommandBuilderD4J<>(prefix, uniqueName);
    }

    public static <T> CommandBuilderD4J<T> builder(final String prefix, final String uniqueName, final String... aliases) {
        return new CommandBuilderD4J<>(prefix, uniqueName, aliases);
    }

    @Override
    public T execute(LinkedList<String> args, Object... optionals) throws InvocationTargetException, IllegalAccessException {
        return null;
    }

    public EnumSet getPermissions() {
        return permissions;
    }

    public boolean isAllowDm() {
        return isAllowDm;
    }

    public boolean isRemoveCallMsg() {
        return isRemoveCallMsg;
    }

    public boolean isForcePrivateReply() {
        return isForcePrivateReply;
    }

    public boolean isRequireMention() {
        return isRequireMention;
    }

    public static class CommandBuilderD4J<T> {

        private String[] aliases;
        private String uniqueName;
        private String description = Defaults.NO_DESCRIPTION;
        private String usage = Defaults.NO_USAGE;
        private String prefix = Defaults.PREFIX;
        private EnumSet<Permissions> permissions = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
        private boolean isAllowDm = PermissionDefaults.ALLOW_DM;
        private boolean isRemoveCallMsg = PermissionDefaults.REMOVE_CALL_MESSAGE;
        private boolean isForcePrivateReply = PermissionDefaults.FORCE_PRIVATE_REPLY;
        private boolean isRequireMention = PermissionDefaults.REQUIRE_MENTION;

        private CommandBuilderD4J(final String nameAlias) {
            this.uniqueName = nameAlias;
            this.aliases = new String[]{nameAlias};
        }

        private CommandBuilderD4J(final String prefix, final String uniqueName) {
            this(uniqueName);
            this.prefix = prefix;
        }

        private CommandBuilderD4J(final String prefix, final String uniqueName, final String... aliases) {
            this.prefix = prefix;
            this.uniqueName = uniqueName;
            this.aliases = aliases;
        }

        public CommandBuilderD4J withPrefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        public CommandBuilderD4J withDescription(final String description) {
            this.description = description;
            return this;
        }

        public CommandBuilderD4J withUsage(final String usage) {
            this.usage = usage;
            return this;
        }

        public CommandBuilderD4J setAllowDm(boolean isAllowDm) {
            this.isAllowDm = isAllowDm;
            return this;
        }

        public CommandBuilderD4J setRemoveCallMsg(boolean isRemoveCallMsg) {
            this.isRemoveCallMsg = isRemoveCallMsg;
            return this;
        }

        public CommandBuilderD4J setForcePrivateReply(boolean isForcePrivateReply) {
            this.isForcePrivateReply = isForcePrivateReply;
            return this;
        }

        public CommandBuilderD4J setRequireMention(boolean isRequireMention) {
            this.isRequireMention = isRequireMention;
            return this;
        }

        public CommandBuilderD4J setPermissions(EnumSet<Permissions> permissions) {
            this.permissions = permissions;
            return this;
        }

        public CommandD4J<T> build(ICommandD4J<T> executor) {
            return new CommandD4J<T>(this.prefix, this.uniqueName, this.description, this.usage, this.permissions, this.isAllowDm, this.isRemoveCallMsg, this.isForcePrivateReply, this.isRequireMention, this.aliases) {

                @Override
                public T execute(LinkedList<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder, Object... optionals) throws InvocationTargetException, IllegalAccessException {
                    return executor.execute(args, event, msgBuilder, optionals);
                }
            };
        }

    }
}
