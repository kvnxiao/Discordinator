import com.alphahelix00.discordinator.Ordinator;
import com.alphahelix00.discordinator.commands.Command;
import com.alphahelix00.discordinator.commands.CommandRegistry;
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
    public void testSingleCommand() {
        // parse message "!single"
        // parse message "!one"
    }

    @Command(
            name = "single test",
            alias = {"single", "one"},
            desc = "test for a single command"
    )
    public void singleCommand() {
        System.out.println("singleCommand() test!");
    }
}
