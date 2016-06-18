import com.github.alphahelix00.discordinator.commands.MainCommand;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class SingleCommand {

    @MainCommand(
            name = "SingleCommand MAIN",
            alias = {"single", "one"},
            desc = "single command test"
    )
    public void singleCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " SingleCommand test!");
    }
}
