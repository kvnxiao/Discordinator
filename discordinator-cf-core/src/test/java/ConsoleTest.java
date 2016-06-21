import com.github.alphahelix00.discordinator.Discordinator;
import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.CommandRegistry;
import com.github.alphahelix00.discordinator.handler.CommandHandler;
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
 * Created on:   6/15/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleTest {

    private CommandRegistry commandRegistry = Discordinator.getCommandRegistry();
    private CommandHandler commandHandler = CommandHandler.getInstance();

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
        assertTrue(commandRegistry.commandExistsAlias("!", "single"));
        assertTrue(commandRegistry.commandExistsAlias("!", "one"));
        // Test for different alias calls
        commandHandler.validateMessage("!single");
        commandHandler.validateMessage("!one");
        // Test for extra arguments and extra whitespace
        commandHandler.validateMessage("!single one abc    def");
    }

    @Test
    public void testbMultiCommand() throws Exception {
        assertTrue(commandRegistry.commandExistsAlias("?", "main"));
        assertFalse(commandRegistry.commandExistsAlias("!", "first"));
        assertFalse(commandRegistry.commandExistsAlias("?", "sub"));
        assertFalse(commandRegistry.commandExistsAlias("?", "two"));
        assertFalse(commandRegistry.commandExistsAlias("!", "asdf"));
        // Test for calling subcommands without main command
        commandHandler.validateMessage("?sub test (should not do anything)");
        // Test for calling main commands and then sub commands
        commandHandler.validateMessage("?main");
        commandHandler.validateMessage("?main none");
        commandHandler.validateMessage("?main none sub");
        commandHandler.validateMessage("?main sub");
        commandHandler.validateMessage("!main two");
        // Test for tertiary subcommand call
        commandHandler.validateMessage("?first sub sub");
    }

    @Test
    public void testcRepeatCommand() throws Exception {
        assertTrue(commandRegistry.commandExistsAlias("~", "rep"));
        // Test for calling main commands that reference sub commands to themselves
        commandHandler.validateMessage("~rep once");
        commandHandler.validateMessage("~rep rep rep once");
    }

    @Test
    public void testdPrintMainCommandList() throws Exception {
        List<Command> commands = commandRegistry.getCommandList();
        commands.forEach(command -> {
            System.out.println(command.toString());
            printSubCommands(command, " ↳ ");
        });
    }

    @Test
    public void testeToggleCommand() {
        System.out.println(CommandHandler.getCommand("?main sub"));
        commandHandler.validateMessage("?main sub");
        commandHandler.validateMessage("!disable ?main sub");
        commandHandler.validateMessage("?main sub");
        commandHandler.validateMessage("!enable ?main sub");
        commandHandler.validateMessage("?main sub");
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
