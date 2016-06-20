# Discordinator.java
![Discordinator Logo](http://i.imgur.com/sQUmvOw.png)

An easily adoptable command framework primarily intended for Discord bots.

## About

**define:** _**Discordinator**_ = *[Discord](https://discordapp.com/)* + *Ordinator* (one who ordains or decrees)

Discordinator is a modularized command framework, created with the purpose of handling the processing and execution of user defined commands.
Although originally intended for use in conjunction with other Discord API wrappers (e.g. [Discord4J](https://github.com/austinv11/Discord4J)), the core of the framework can also be easily adapted for use in any other project.

##Functionality
The command framework within Discordinator is roughly based on the Command Design Pattern by abstracting executable commands from the client.
The main two classes within the framework are:
- `CommandRegistry` *(the command bank)*
  - Storage of all commands
- `CommandHandler` *(the ordinator)*
  - Validate and parse Strings for command calls
  - Calls upon the registry to register commands (annotated methods / CommandBuilder / Command class extension)

### Commands
Commands are split into _**main**_ and _**sub**_ commands. Main commands must have unique names and are registered into the command registry. Sub commands are entirely optional, and are tied to their parent commands by their unique names. All commands are called by their _**alias**_ but defined by their _**name**_ For a sub command to properly function, it is crucial that the name of the sub commands is set within the parent command. Take the following as an example:

#### Example
Supposed you have defined a main command `(name = "Greet Command", alias = "greet", subCommands = {"Hi Command", "Bye Command"})` that will print out `Greetings.`.

And suppose you have also defined two sub commands: `(name = "Hi Command", alias = "hi")` that will print out `Hi.`, and `(name = "Bye Command", alias = "bye")` that will print out `Bye.`

When a string containing `greet` is parsed, `Greetings.` will be printed. When a string containing `greet hi` is parsed, `Greetings. Hi.` will be printed, and likewise if `greet bye` is parsed, `Greetings. Bye.` will be printed.

All commands can have a list of arguments passed into it on execution. For example, if `greet bye args1 args2` is called, the main command will execute a method to print `Greetings. Bye`, but will also have access to an argument token list, `{args1, args2}`.
Depending on the supplied argument, conditional statements can be made to 
Note that one can choose to have a main command do nothing and have only its sub commands do something. So in the above example, we can choose to not print out `"Greetings."`, so that only `"Hi."` is displayed when `greet hi` is parsed.

#### Command chaining
The number of sub commands is not limited to just one in a main command. A sub command can also have a parent command that is a sub command to another command. As long as the defining hierarchy is correct, there should be no problem having any number of sub command depth.

##### Repeatable (Self-chaining) Commands
A command can also be defined as self-chaining or repeatable if the command name is included within the subCommands: `(name = "repeat", alias = "repeat", subCommands = "repeat")`
Calling `repeat` will do something once, and calling `repeat repeat` will do the same thing twice, as if it were a sub command of itself.

## Creating / Registering / Parsing Commands

All commands within the command registry can be parsed for by their alias.
There are several ways of defining a command (the below examples are illustrated for the Discord4J module):

---

##### Annotations
A class containing method defined with `@MainCommand` (and for sub commands, `@SubCommand`) can have those methods registered as a command in the CommandRegistry.
Call `#registerAnnotatedCommands` from your `AbstractCommandHandler` implementation (e.g. use `CommandHandlerD4J.registerAnnotatedCommands(_objWithAnnotatedMethods_)` if you are using the Discord4J module)

By default, the prefix is not a required field within the annotation (defaults to `!`), and the method name can be declared as anything.

```java
    @MainCommand(
            prefix = "?",
            name = "Main Command",
            alias = {"main", "first"},
            desc = "main command",
            subCommands = {"sub1"}
    )
    public void mainCommand(LinkedList<String> args) {
        // DO MAIN COMMAND STUFF
    }

    @SubCommand(
            name = "sub1",
            prefix = "?",
            alias = {"sub", "two"},
            desc = "first subcommand of main command",
            subCommands = {"sub2"}
    )
    public void subCommand(LinkedList<String> args) {
        // DO SUB COMMAND STUFF
    }

    @SubCommand(
            name = "sub2",
            prefix = "?",
            alias = {"sub", "three"},
            desc = "second subcommand of main command"
    )
    public void tertiaryCommand(LinkedList<String> args) {
        // DO SUB SUB COMMAND STUFF
    }
```

######Concrete Example:
```java
public class Commands {

    @MainCommand(
            name = "ping",
            alias = "ping",
            desc = "bot says pong!"
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
Using the CommandBuilder, supply the proper information to create a new command and add it to the registry.
Within the .build() method call, the use of Java 8 lambda expressions is recommended  as it takes in a functional interface CommandExecutor class
```java
commandHandler.registerCommand(new CommandBuilderD4J("ping", "bot says pong!", "ping")
        .setPrefix("?").setIsMainCommand(true).build((args, event) -> {
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
                }));
```

---

##### User-defined commands extending Command class
One can define their own commands that extend the (library module specific) `Command` class. For example, if using the Discord4J module, create a class that extends `CommandD4J`:
```java
public class PingCommand extends CommandD4J {

    private final String PREFIX = "!";
    private final String DESCRIPTION = "bot says pong!";
    private final String NAME = "ping";
    private final String[] ALIASES = {"ping"};

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
        return Arrays.asList(ALIASES);
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

    // This method is called when the command is executed
    @Override
    public void execute(List<String> list, MessageReceivedEvent event) throws IllegalAccessException, InvocationTargetException {
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
    }

}
```

And somewhere else, make sure to register the newly created command class
```java
CommandHandlerD4J#registerCommand(new PingCommand());
```

## To-do List
- Permissions
- Parse methods that return objects -> do something with returned objects (library specific?)

##Contribution
Discordinator is still a **[Work In Progress]**

For those of you who are interested seeing this project improve, all contributions are appreciated!
Also feel free to drop me a PM on Discord <*@alpha;helix*> if there are any questions, comments, or concerns (throw me a mention in the Discord API server).
