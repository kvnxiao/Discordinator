import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.utils.CommandHandler;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
public class ConsoleTest {

    @Test
    public void testCommandRegistry() {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        commandRegistry.addPrefix("!");
        commandRegistry.addPrefix("?");
        assertTrue(commandRegistry.containsPrefix("!"));
        assertTrue(commandRegistry.containsPrefix("?"));
        assertFalse(commandRegistry.containsPrefix("~"));
        assertTrue(commandRegistry.getPrefixes().size() == 2);
    }

    @Test
    public void testSingleCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.registerAnnotatedCommands(new SingleCommand());
        assertTrue(commandRegistry.commandExists("single", "!"));
        assertTrue(commandRegistry.commandExists("one", "!"));
        // Test for different alias calls
        commandHandler.parseForCommands("!single");
        commandHandler.parseForCommands("!one");
        // Test for extra arguments
        commandHandler.parseForCommands("!single one abc    def");
    }

    @Test
    public void testMultiCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.registerAnnotatedCommands(new MultiCommand());
        assertTrue(commandRegistry.commandExists("main", "?"));
        assertFalse(commandRegistry.commandExists("first", "!"));
        assertTrue(commandRegistry.commandExists("sub", "?"));
        assertTrue(commandRegistry.commandExists("two", "?"));
        assertFalse(commandRegistry.commandExists("asdf", "!"));
        // Test for calling subcommands without main command
        commandHandler.parseForCommands("?sub test (should not do anything)");
        // Test for calling main commands and then sub commands
        commandHandler.parseForCommands("?main");
        commandHandler.parseForCommands("?main none");
        commandHandler.parseForCommands("?main none sub");
        commandHandler.parseForCommands("?main sub");
        commandHandler.parseForCommands("!main two");
        // Test for tertiary subcommand call
        commandHandler.parseForCommands("?first sub sub");
    }

    @Test
    public void testRepeatCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.registerAnnotatedCommands(new RepeatCommand());
        assertTrue(commandRegistry.commandExists("rep", "~"));
        // Test for calling main commands that reference sub commands to themselves
        commandHandler.parseForCommands("~rep once");
        commandHandler.parseForCommands("~rep rep rep once");
    }

}
