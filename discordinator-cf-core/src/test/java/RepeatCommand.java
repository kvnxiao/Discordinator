import com.github.alphahelix00.discordinator.commands.MainCommand;

import java.util.LinkedList;

/**
 * Created on:   6/16/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class RepeatCommand {

    @MainCommand(
            prefix = "~",
            name = "Repeat",
            alias = {"rep"},
            desc = "test for repeating command",
            subCommands = {"Repeat"}
    )
    public void repeatCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " Repeat test!");
    }
}
