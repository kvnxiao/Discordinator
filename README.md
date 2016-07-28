![Discordinator Logo](http://i.imgur.com/sQUmvOw.png)
---
[![Build Status](https://drone.io/github.com/alphahelix00/Discordinator/status.png)](https://drone.io/github.com/alphahelix00/Discordinator/latest)
[![](https://jitpack.io/v/alphahelix00/Discordinator.svg?style=flat-square)](https://jitpack.io/#alphahelix00/Discordinator)

## About

**define:** _**Discordinator**_ = *[Discord](https://discordapp.com/)* + *[Ordinator](https://github.com/alphahelix00/Ordinator)* (one who ordains or decrees)

Discordinator is a fast, modularized command framework created with the purpose of handling the processing and execution of user defined commands for Discord bots.

#### Functionality
The command framework within Discordinator is an extension to *Ordinator*, which is roughly based on the Command Design Pattern by abstracting executable commands from the client.
The two important classes within Discordinator:
- `CommandRegistry class` *(the command bank)*
  - Storage of all commands
- `CommandHandlerD4J class` *(the ordinator, a Discord4J implementation of AbstractCommandHandler from Ordinator)*
  - Validate and parse message Strings for command calls
  - Calls upon the registry to register and execute commands (annotated methods / CommandBuilder / Command class extension)

**Please see [Ordinator](https://github.com/alphahelix00/Ordinator) for more information regarding what's included and how to use.**

## How to use

#### Loading into a Discord4J bot

There are two ways of having Discordinator load into a Discord4J bot:

1. As an external module (easiest)
2. As an internal project dependency for your bot (if you want to have Discordinator as an innate part of your bot)

To load externally as a module, the Discord4J implementation includes an `IModule` implementation called `DiscordinatorModule` which will be automatically loaded by a Discord4J bot if the .jar file is placed in the 'modules' folder of the bot (see [Releases](https://github.com/alphahelix00/Discordinator/releases) section for the .jar files, or compile your own dev branch snapshot)

To load internally as a project dependency, please [(see Usage - Discord4J)](#usage---discord4j)

## Usage - Discord4J

To use as an external module in your [Discord4J bot](https://github.com/alphahelix00/AlphaBot), download the jar-with-dependencies .jar file from the release section or package the latest dev-SNAPSHOT commit yourself and place into the modules folder of your bot.

To import into your own project, download the jar files from [release](https://github.com/alphahelix00/Discordinator/releases) and add as external dependencies within your IDE, or see below for import through jitpack.io.

To use as an internal dependency in your bot, make sure to load the DiscordinatorModule module programmatically:
```java
IDiscordClient#getModuleLoader().loadModule(new DiscordinatorModule());
```

To create your own commands and command modules, [see the next section on creating commands](#creating--registering--parsing-commands)

## Import as dependency

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
        compile 'com.github.alphahelix00.Discordinator:discordinator-cf:@VERSION_NUM@'
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

## Creating / Registering / Parsing Commands

### Creating a Command Module

To create your own command module, create a class that extends `IModule` and register your commands on the `enable(IDiscordClient iDiscordClient)` method implementation: 
```java
    @Override
    public boolean enable(IDiscordClient iDiscordClient) {
        this.client = iDiscordClient;
        CommandHandlerD4J commandHandler = (CommandHandlerD4J) Ordinator.getCommandRegistry().getCommandHandler();
        // ...
        // REGISTER COMMANDS HERE
        // ...
        return true;
    }
```

**For examples of creating commands, see below:**

### Creating Commands for the Registry

All commands within the command registry can be parsed for by their alias.
There are several ways of defining a command:

---

##### Annotations

Anntations exist for defining main commands and sub commands. A class containing methods that have been annotated with `@MainCommand` (and for sub commands, `@SubCommand`) can be registered by the CommandRegistry. Note that method names can be anything, as all that matters is that the annotations exist above them.

Permissions are defined with `@Permission` and are optional; without permissions, the command defaults to requiring a user to be able to *read* and *send* messages in the channel.

Note: By default, the prefix within the annotation defaults to `-`, as defined in *Ordinator* project

Remember to register the class with `CommandHandlerD4J#.registerAnnotatedCommands(new ___MyCommandClassName___())`.

```java
public class Commands {

    @MainCommand(
            prefix = "?",
            name = "Main Command",
            alias = {"main", "first"},
            desc = "main command",
            subCommands = {"sub1"}
    )
    public void mainCommand(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) {
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
    public void subCommand(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) {
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
    public void tertiaryCommand(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) {
        // DO SUB SUB COMMAND STUFF
    }

}
```

######Concrete Example:
```java
public class Commands {

    private static final String NAME = "Ping";

    @MainCommand(
            prefix = "!",
            name = NAME,
            alias = "ping",
            description = "pings the bot to reply with pong!"
    )
    @Permission(
            // This allows
            allowPrivateMessage = true
    )
    public void pingCommand(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) {
        RequestBuffer.request(() -> {
            try {
                // Note that the channel is already provided to this method on parsing of the command call
                // Discordinator will know if the message received was in a public channel or a private message to the bot
                // So it is not required to write msgBuilder.withChannel(...)
                msgBuilder.withContent("pong!").build();
            } catch (DiscordException | MissingPermissionsException e) {
                CommandHandlerD4J.logMissingPerms(event, NAME, e);
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
CommandHandlerD4J#registerCommand(CommandBuilderD4J.builder("Ping", "pings the bot to reply with pong!")
                    .alias("ping")
                    .prefix("!")
                    .isMain(true)
                    .allowPrivateMessage(true)
                    .build((args, event, msgBuilder) -> {
                        RequestBuffer.request(() -> {
                            try {
                                // Note that the channel is already provided to this method on parsing of the command call
                                // Discordinator will know if the message received was in a public channel or a private message to the bot
                                // So it is not required to write msgBuilder.withChannel(...)
                                msgBuilder.withContent("pong!").build();
                            } catch (DiscordException | MissingPermissionsException e) {
                                CommandHandlerD4J.logMissingPerms(event, "Ping", e);
                            }
                        });
                        // CommandExecutorD4J interface returns an Optional type, so it is possible to do extra stuff with the returned object from one command in another command.
                        // If we want void method, return Optional.empty();
                       return Optional.empty(); // Effectively a VOID method
                    });
```

---

##### User-defined commands extending Command class
One can also define commands by extending the (library specific) `Command` class. For example, if using the Discord4J module, create a class that extends `CommandD4J` and define the command's properties and create a constructor:
```java
public class PingCommand extends CommandD4J {

    public PingCommand() {
        super(
                "ping",                                                             // prefix
                "Ping",                                                             // name
                "bot says pong!",                                                   // description
                Collections.singletonList("ping"),                                  // alias
                true,                                                               // is main command?
                true,                                                               // is enabled by default?
                false,                                                              // is an essential command?
                new HashMap<>(),                                                    // set to new HashMap<>() if no subcommands
                new HashMap<>(),                                                    // set to new HashMap<>() if no subcommands
                EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES),   // Permissions
                false,                                                              // requires a mention?
                true,                                                               // allow private messaging the bot to execute command?
                false,                                                              // bot replies in private message only?
                false                                                               // remove call message?
        );
    }

    // This method is called when the command is executed
    @Override
    public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder msgBuilder) throws IllegalAccessException, InvocationTargetException {
        // Here we define what happens when the command is called!
        RequestBuffer.request(() -> {
            try {
                // Note that the channel is already provided to this method on parsing of the command call
                // Discordinator will know if the message received was in a public channel or a private message to the bot
                // So it is not required to write msgBuilder.withChannel(...)
                msgBuilder.withContent("pong!").build();
            } catch (DiscordException | MissingPermissionsException e) {
                CommandHandlerD4J.logMissingPerms(event, "Ping", e);
            }
        });
        // CommandExecutorD4J interface returns an Optional type, so it is possible to do extra stuff with the returned object from one command in another command.
        // If we want void method, return Optional.empty();
        return Optional.empty(); // Effectively a VOID method
    }
}
```

And somewhere else, make sure to register the newly created command class
```java
CommandHandlerD4J#registerCommand(new PingCommand());
```

##Contribution
Discordinator has been completely released with all features that I intended it to have originally. However, this doesn't mean that new features will not be added. For those of you who are interested seeing this project improve, all contributions are appreciated!

If you are interested in a new feature, feel free to send me a message. Also feel free to drop me a PM on Discord <*@alpha;helix*> if there are any questions, comments, or concerns (throw me a mention in the Discord API server).
