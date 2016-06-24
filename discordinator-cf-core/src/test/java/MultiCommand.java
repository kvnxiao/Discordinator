import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.commands.SubCommand;

import java.util.List;

/**
 * Created on:   6/16/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class MultiCommand {

    @MainCommand(
            prefix = "?",
            name = "Multi-Command",
            alias = {"main", "first"},
            desc = "main command",
            subCommands = {"sub1"}
    )
    public void mainCommand(List<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " MultiCommand test! primary");
    }

    @SubCommand(
            name = "sub1",
            prefix = "?",
            alias = {"sub", "two"},
            desc = "first subcommand of main command",
            subCommands = {"sub2"}
    )
    public void subCommand(List<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " MultiCommand test! secondary");
    }

    @SubCommand(
            name = "sub2",
            prefix = "?",
            alias = {"sub", "three"},
            desc = "second subcommand of main command"
    )
    public void tertiaryCommand(List<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " MultiCommand test! tertiary");
    }
}
