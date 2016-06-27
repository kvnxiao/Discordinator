![Discordinator Logo](http://i.imgur.com/sQUmvOw.png)
---
[![Build Status](https://drone.io/github.com/alphahelix00/Discordinator/status.png)](https://drone.io/github.com/alphahelix00/Discordinator/latest)
[![](https://jitpack.io/v/alphahelix00/Discordinator.svg?style=flat-square)](https://jitpack.io/#alphahelix00/Discordinator)

A modular command framework for Discord bots.

## About

**define:** _**Discordinator**_ = *[Discord](https://discordapp.com/)* + *[Ordinator](https://github.com/alphahelix00/Ordinator)* (one who ordains or decrees)

Discordinator is a modularized command framework, created with the purpose of handling the processing and execution of user defined commands for Discord bots.
**Please see [Ordinator](https://github.com/alphahelix00/Ordinator) for more information regarding what's included and how to use.**

## Creating / Registering / Parsing Commands

All commands within the command registry can be parsed for by their alias.
There are several ways of defining a command (the below examples are illustrated for the Discord4J module):

---

##### Annotations
A class containing methods defined with `@MainCommand` (and for sub commands, `@SubCommand`) can have those methods registered as a command in the CommandRegistry.
Permissions are defined with `@Permission` and are optional: without it, the command defaults to requiring a user to be able to read and send messages in the channel.
Call `#registerAnnotatedCommands` on the class object from the `AbstractCommandHandler` implementation (e.g. use `CommandHandlerD4J.registerAnnotatedCommands(_objWithAnnotatedMethods_)` if you are using the Discord4J module)

By default, the prefix is not a required field within the annotation (defaults to `-`, as defined in *Ordinator* project), and the method name can be declared as anything.

```java
public class Commands {

    @MainCommand(
            prefix = "?",
            name = "Main Command",
            alias = {"main", "first"},
            desc = "main command",
            subCommands = {"sub1"}
    )
    public void mainCommand(List<String> args, MessageReceivedEvent event) {
        // DO MAIN COMMAND STUFF
    }

    @SubCommand(
            name = "sub1",
            prefix = "?",
            alias = {"sub", "two"},
            desc = "first subcommand of main command",
            subCommands = {"sub2"}
    )
    @Permission(
        requireMention = true
    )
    public void subCommand(List<String> args, MessageReceivedEvent event) {
        // DO SUB COMMAND STUFF
    }

    @SubCommand(
            name = "sub2",
            prefix = "?",
            alias = {"sub", "three"},
            desc = "second subcommand of main command"
    )
    @Permission(
        permissions = {Permissions.ADMINISTRATOR}
    )
    public void tertiaryCommand(List<String> args, MessageReceivedEvent event) {
        // DO SUB SUB COMMAND STUFF
    }

}
```

######Concrete Example:
```java
public class Commands {

    @MainCommand(
            name = "Ping Command",
            alias = "ping",
            desc = "bot says pong!"
    )
    @Permission(
        permissions = {Permissions.SEND_MESSAGES, Permissions.READ_MESSAGES}
        requireMention = false
    )
    public void ping(List<String> args, MessageReceivedEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        MessageBuilder builder = new MessageBuilder(event.getClient());
        RequestBuffer.request(() -> {
            try {
                builder.withChannel(event.getMessage().getChannel()).withContent("pong!").build();
            } catch (DiscordException | MissingPermissionsException e) {
                LOGGER.warn("Attempt to call ping command by user " + event.getMessage().getAuthor().getName()
                        + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed.", e);
            }
        });
    }
    
}
```

And somewhere else, make sure to register the class containing the annotated methods
```java
CommandHandlerD4J#registerAnnotatedCommands(new Commands());
```

---

##### CommandBuilder
Using `CommandBuilderD4J`, supply the command properties to be created and added to the registry.
Within the .build() method call, the use of Java 8 lambda expressions is recommended as it takes in a functional interface `CommandExecutorD4J` object.

```java
CommandHandlerD4J#registerCommand(CommandBuilderD4J.builder("Ping Command", "bot says pong!")
                   .alias("ping")
                   .prefix("?")
                   .isMain(true)
                   .build((args, event, msgBuilder) -> {
                       // Here we define what happens when the command is called!
                       RequestBuffer.request(() -> {
                           try {
                               msgBuilder.withChannel(event.getMessage().getChannel()).withContent("pong!").build();
                           } catch (DiscordException | MissingPermissionsException e) {
                               LOGGER.warn("Attempt to call ping command by user " + event.getMessage().getAuthor().getName()
                                       + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed.", e);
                           }
                       });
                       return Optional.empty();     // Essentially a void method
                   });
```

---

##### User-defined commands extending Command class
One can also define commands by extending the (library specific) `Command` class. For example, if using the Discord4J module, create a class that extends `CommandD4J` and define the command's properties and create a constructor:
```java
public class Ping extends CommandD4J {

    public Ping() {
        super(
                "ping",                                                             // prefix
                "Ping Command",                                                     // name
                "bot says pong!",                                                   // description
                Collections.singletonList("ping"),                                  // alias
                true,                                                               // is main command?
                true,                                                               // is enabled by default?
                false,                                                              // is an essential command?
                new HashMap<>(),                                                    // set to new HashMap<>() if no subcommands
                new HashMap<>(),                                                    // set to new HashMap<>() if no subcommands
                EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES),   // Permissions
                false,                                                              // requires a mention?
                false                                                               // bot sends reply as a private message / DM ?
        );
    }

    // This method is called when the command is executed
    @Override
    public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
        // Here we define what happens when the command is called!
        MessageBuilder builder = new MessageBuilder(event.getClient());
        RequestBuffer.request(() -> {
            try {
                builder.withChannel(event.getMessage().getChannel()).withContent("pong!").build();
            } catch (DiscordException | MissingPermissionsException e) {
                LOGGER.warn("Attempt to call ping command by user " + event.getMessage().getAuthor().getName()
                        + " in channel " + event.getMessage().getChannel().getName() + " on server " + event.getMessage().getGuild().getName() + " failed.", e);
            }
        });
        return Optional.empty();
    }
}
```

And somewhere else, make sure to register the newly created command class
```java
CommandHandlerD4J#registerCommand(new PingCommand());
```

## Usage - Discord4J
To import into your own project, download the jar files from [release](https://github.com/alphahelix00/Discordinator/releases) and add as external dependencies within your IDE, or see below for import through jitpack.io.
 
To use as a module in your [Discord4J bot](https://github.com/alphahelix00/AlphaBot), download the jar-with-dependencies .jar file from the release section or package the latest dev-SNAPSHOT commit yourself and place into the modules folder of your bot.

### Gradle

###### build.gradle

```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

```
dependencies {
        compile 'com.github.alphahelix00.Discordinator:discordinator-cf:0.9.0'
}
```

### Maven

###### pom.xml
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.alphahelix00</groupId>
    <artifactId>Discordinator</artifactId>
    <version>@VERSION_NUM@</version>
</dependency>
```

Replace `@VERSION_NUM@` with the version you want to use. To use the latest development snapshot, use `dev-SNAPSHOT`.

## To-do List
- Permissions
- Parse methods that return objects -> do something with returned objects (library specific?)

##Contribution
Discordinator is still a **[Work In Progress]**

For those of you who are interested seeing this project improve, all contributions are appreciated!
Also feel free to drop me a PM on Discord <*@alpha;helix*> if there are any questions, comments, or concerns (throw me a mention in the Discord API server).