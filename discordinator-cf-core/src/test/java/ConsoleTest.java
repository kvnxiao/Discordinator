import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.utils.CommandHandler;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created on: 6/15/2016
 * Author:     Kevin Xiao
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleTest {

    @Test
    public void testaCommandRegistry() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
        commandRegistry.addPrefix("!");
        commandRegistry.addPrefix("?");
        commandHandler.registerAnnotatedCommands(new SingleCommand());
        commandHandler.registerAnnotatedCommands(new MultiCommand());
        commandHandler.registerAnnotatedCommands(new RepeatCommand());
        assertTrue(commandRegistry.containsPrefix("!"));
        assertTrue(commandRegistry.containsPrefix("?"));
        assertTrue(commandRegistry.containsPrefix("~"));
        assertTrue(commandRegistry.getPrefixes().size() == 3);
    }

    @Test
    public void testaSingleCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
        assertTrue(commandRegistry.commandExists("single", "!"));
        assertTrue(commandRegistry.commandExists("one", "!"));
        // Test for different alias calls
        commandHandler.parseForCommands("!single");
        commandHandler.parseForCommands("!one");
        // Test for extra arguments
        commandHandler.parseForCommands("!single one abc    def");
    }

    @Test
    public void testbMultiCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
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
    public void testcRepeatCommand() throws Exception {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        CommandHandler commandHandler = new CommandHandler();
        assertTrue(commandRegistry.commandExists("rep", "~"));
        // Test for calling main commands that reference sub commands to themselves
        commandHandler.parseForCommands("~rep once");
        commandHandler.parseForCommands("~rep rep rep once");
    }

    @Test
    public void testzCommandList() {
        CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
        List<Map<String, Command>> commandList = commandRegistry.getCommandMapList();
        for (Map<String, Command> map : commandList) {
            for (Command command : map.values()) {
                System.out.println(command.toString());
            }
        }

    }

}
