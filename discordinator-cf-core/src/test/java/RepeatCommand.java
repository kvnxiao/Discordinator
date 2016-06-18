import com.github.alphahelix00.discordinator.commands.MainCommand;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class RepeatCommand {

    @MainCommand(
            prefix = "~",
            name = "repeat command",
            alias = {"rep"},
            desc = "test for repeating command",
            subCommands = {"repeat command"}
    )
    public void repeatCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " Repeat test!");
    }
}
