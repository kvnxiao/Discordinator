import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.utils.CommandHandler;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
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

    private CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
    private CommandHandler commandHandler = new CommandHandler();

    @Before
    public void setUp() throws Exception {
        commandHandler.registerAnnotatedCommands(new SingleCommand());
        commandHandler.registerAnnotatedCommands(new MultiCommand());
        commandHandler.registerAnnotatedCommands(new RepeatCommand());
    }

    @Test
    public void testaCommandRegistry() {
        commandRegistry.addPrefix("-");
        assertTrue(commandRegistry.containsPrefix("!"));
        assertTrue(commandRegistry.containsPrefix("?"));
        assertTrue(commandRegistry.containsPrefix("~"));
        assertTrue(commandRegistry.getPrefixes().size() == 4);
    }

    @Test
    public void testaSingleCommand() throws Exception {
        assertTrue(commandRegistry.commandExists("single", "!"));
        assertTrue(commandRegistry.commandExists("one", "!"));
        // Test for different alias calls
        commandHandler.parseForCommands("!single");
        commandHandler.parseForCommands("!one");
        // Test for extra arguments and extra whitespace
        commandHandler.parseForCommands("!single one abc    def");
    }

    @Test
    public void testbMultiCommand() throws Exception {
        assertTrue(commandRegistry.commandExists("main", "?"));
        assertFalse(commandRegistry.commandExists("first", "!"));
        assertFalse(commandRegistry.commandExists("sub", "?"));
        assertFalse(commandRegistry.commandExists("two", "?"));
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
        assertTrue(commandRegistry.commandExists("rep", "~"));
        // Test for calling main commands that reference sub commands to themselves
        commandHandler.parseForCommands("~rep once");
        commandHandler.parseForCommands("~rep rep rep once");
    }

    @Test
    public void testzPrintMainCommandList() throws Exception {
        List<Command> commands = commandRegistry.getCommandList();
        commands.forEach(command -> {
            System.out.println(command.toString());
            printSubCommands(command, " ↳ ");
        });
    }


    private void printSubCommands(Command parentCommand, String tabAmount) {
        if (parentCommand.hasSubCommand()) {
            Map<String, Command> subCommands = parentCommand.getSubCommands();
            Collections.unmodifiableCollection(subCommands.values()).forEach(command -> {
                if (parentCommand.getName().equals(command.getName())) {
                    System.out.println(tabAmount + " repeatable");
                } else {
                    System.out.println(tabAmount + command.toString());
                    printSubCommands(command, tabAmount + tabAmount);
                }
            });
        }
    }

}
