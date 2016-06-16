import com.alphahelix00.discordinator.commands.Command;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class SingleCommand {

    @Command(
            mainCommand = true,
            name = "single test",
            alias = {"single", "one"},
            desc = "test for a single command"
    )
    public void singleCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString());
    }
}
