import com.alphahelix00.discordinator.Ordinator;
import com.alphahelix00.discordinator.commands.Command;
import com.alphahelix00.discordinator.commands.CommandRegistry;
import com.alphahelix00.discordinator.utils.CommandHandler;
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
        CommandRegistry commandRegistry = Ordinator.getCommandRegistry();
        commandRegistry.addPrefix("!");
        commandRegistry.addPrefix("?");
        assertTrue(commandRegistry.containsPrefix("!"));
        assertTrue(commandRegistry.containsPrefix("?"));
        assertTrue(commandRegistry.getPrefixes().size() == 2);
        assertFalse(commandRegistry.containsPrefix("~"));
    }

    @Test
    public void testSingleCommand() throws Exception {
        CommandRegistry commandRegistry = Ordinator.getCommandRegistry();
        CommandHandler.registerAnnotatedCommands(new SingleCommand());
        assertTrue(commandRegistry.commandExists("single"));
        assertTrue(commandRegistry.commandExists("one"));
        CommandHandler.parseForCommands("single");
        CommandHandler.parseForCommands("one");
    }

    @Test
    public void testMultiCommand() throws Exception {
        CommandRegistry commandRegistry = Ordinator.getCommandRegistry();
        CommandHandler.registerAnnotatedCommands(new MultiCommand());
        assertTrue(commandRegistry.commandExists("main"));
        assertTrue(commandRegistry.commandExists("first"));
        assertTrue(commandRegistry.commandExists("sub"));
        assertTrue(commandRegistry.commandExists("two"));
        assertFalse(commandRegistry.commandExists("asdf"));
        CommandHandler.parseForCommands("sub test (should not do anything)");
        CommandHandler.parseForCommands("main");
        CommandHandler.parseForCommands("main none");
        CommandHandler.parseForCommands("main none sub");
        CommandHandler.parseForCommands("main sub");
        CommandHandler.parseForCommands("main two");
        CommandHandler.parseForCommands("first sub sub");
    }

    @Test
    public void testRepeatCommand() throws Exception {
        CommandRegistry commandRegistry = Ordinator.getCommandRegistry();
        CommandHandler.registerAnnotatedCommands(new RepeatCommand());
        assertTrue(commandRegistry.commandExists("rep"));
        CommandHandler.parseForCommands("rep once");
        CommandHandler.parseForCommands("rep once rep rep");
    }

}
