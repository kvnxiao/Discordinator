package com.github.alphahelix00.discordinator.d4j;

import com.github.alphahelix00.discordinator.commands.CommandContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Created on:   2017-01-27
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class CommandLoaderD4J {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLoaderD4J.class);

    private final List<Class> commandClasses = new ArrayList<>();
    private final List<Class> annotatedClasses = new ArrayList<>();

    public void load(File file) {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            try (JarFile jar = new JarFile(file)) {
                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                URL url = file.toURI().toURL();
                for (URL url1 : classLoader.getURLs()) {
                    if (url.equals(url1))
                        return;
                }
                Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addURL.setAccessible(true);
                addURL.invoke(classLoader, url);
                addURL.setAccessible(false);
                List<String> classes = jar.stream()
                        .filter(entry -> !entry.isDirectory() && entry.getName().toLowerCase().endsWith(".class"))
                        .map(path -> path.getName().replace('/', '.').substring(0, path.getName().length() - ".class".length()))
                        .collect(Collectors.toList());
                for (String clazz : classes) {
                    try {
                        Class classInstance = loadClass(clazz);
                        if (CommandD4J.class.isAssignableFrom(classInstance) && !classInstance.equals(CommandExecutorD4J.class)) {
                            commandClasses.add(classInstance);
                        } else if (CommandContainer.class.isAssignableFrom(classInstance) && !classInstance.equals(CommandContainer.class)) {
                            annotatedClasses.add(classInstance);
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Could not load {} as a .jar file!", file.getName());
            } catch (Exception ignored) {}
        }
    }

    public static Class loadClass(String clazz) throws ClassNotFoundException {
        if (clazz.contains("$") && clazz.substring(0, clazz.lastIndexOf("$")).length() > 0) {
            try {
                loadClass(clazz.substring(0, clazz.lastIndexOf("$")));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return Class.forName(clazz);
    }

    public List<Class> getCommandClasses() {
        return Collections.unmodifiableList(commandClasses);
    }

    public List<Class> getAnnotatedClasses() {
        return Collections.unmodifiableList(annotatedClasses);
    }
}
