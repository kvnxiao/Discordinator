import com.alphahelix00.discordinator.commands.Command;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class MultiCommand {

    @Command(
            mainCommand = true,
            name = "main command",
            alias = {"main", "first"},
            desc = "test for a single command",
            subCommands = {"sub command 1"}
    )
    public void mainCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " MAIN COMMAND");
    }

    @Command(
            name = "sub command 1",
            alias = {"sub", "two"},
            desc = "first sub",
            subCommands = {"sub command 2"}
    )
    public void subCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " SECONDARY COMMAND");
    }

    @Command(
            name = "sub command 2",
            alias = {"sub", "three"},
            desc = "second sub"
    )
    public void tertiaryCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " TERTIARY COMMAND");
    }
}
