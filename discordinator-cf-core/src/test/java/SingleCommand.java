import com.github.alphahelix00.discordinator.commands.CommandAnnotation;

import java.util.LinkedList;

/**
 * Author:      Kevin Xiao
 * Created on:  6/16/2016
 */
public class SingleCommand {

    @CommandAnnotation(
            mainCommand = true,
            name = "single test",
            alias = {"single", "one"},
            desc = "test for a single command"
    )
    public void singleCommand(LinkedList<String> args) {
        System.out.println(this.getClass().getName() + args.toString());
    }
}
