import com.alphahelix00.discordinator.commands.Command;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class RepeatCommand {

    @Command(
            mainCommand = true,
            name = "repeat command",
            alias = {"rep"},
            desc = "test for repeating command",
            subCommands = {"repeat command"}
    )
    public void repeatCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " Repeat test!");
    }
}
