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
        CommandHandler.registerAnnotatedCommands(new SingleCommand());
        assertTrue(commandRegistry.commandExists("single"));
        assertTrue(commandRegistry.commandExists("one"));
        // Test for different alias calls
        CommandHandler.parseForCommands("single");
        CommandHandler.parseForCommands("one");
        // Test for extra arguments
        CommandHandler.parseForCommands("single one abc def");
    }

    @Test
    public void testMultiCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler.registerAnnotatedCommands(new MultiCommand());
        assertTrue(commandRegistry.commandExists("main"));
        assertTrue(commandRegistry.commandExists("first"));
        assertTrue(commandRegistry.commandExists("sub"));
        assertTrue(commandRegistry.commandExists("two"));
        assertFalse(commandRegistry.commandExists("asdf"));
        // Test for calling subcommands without main command
        CommandHandler.parseForCommands("sub test (should not do anything)");
        // Test for calling main commands and then sub commands
        CommandHandler.parseForCommands("main");
        CommandHandler.parseForCommands("main none");
        CommandHandler.parseForCommands("main none sub");
        CommandHandler.parseForCommands("main sub");
        CommandHandler.parseForCommands("main two");
        // Test for tertiary subcommand call
        CommandHandler.parseForCommands("first sub sub");
    }

    @Test
    public void testRepeatCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler.registerAnnotatedCommands(new RepeatCommand());
        assertTrue(commandRegistry.commandExists("rep"));
        // Test for calling main commands that reference sub commands to themselves
        CommandHandler.parseForCommands("rep once");
        CommandHandler.parseForCommands("rep rep rep once");
    }

}
