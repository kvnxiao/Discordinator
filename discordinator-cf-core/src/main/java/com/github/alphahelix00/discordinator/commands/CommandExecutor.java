package com.github.alphahelix00.discordinator.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created on:   6/15/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
@FunctionalInterface
public interface CommandExecutor {

    void execute(List<String> args) throws IllegalAccessException, InvocationTargetException;
}
