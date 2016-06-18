import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.commands.SubCommand;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class MultiCommand {

    @MainCommand(
            prefix = "?",
            name = "main command",
            alias = {"main", "first"},
            desc = "main command",
            subCommands = {"sub command 1"}
    )
    public void mainCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " MAIN COMMAND");
    }

    @SubCommand(
            name = "sub command 1",
            prefix = "?",
            alias = {"sub", "two"},
            desc = "first sub",
            subCommands = {"sub command 2"}
    )
    public void subCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " SECONDARY COMMAND");
    }

    @SubCommand(
            name = "sub command 2",
            prefix = "?",
            alias = {"sub", "three"},
            desc = "second sub"
    )
    public void tertiaryCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " TERTIARY COMMAND");
    }
}
