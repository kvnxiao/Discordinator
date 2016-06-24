import com.github.alphahelix00.discordinator.commands.MainCommand;

import java.util.List;

/**
 * Created on:   6/16/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
 */
public class SingleCommand {

    @MainCommand(
            name = "Single",
            alias = {"single", "one"},
            desc = "single command test"
    )
    public void singleCommand(List<String> args) {
        System.out.println(this.getClass().getName() + args.toString() + " SingleCommand test!");
    }

}
