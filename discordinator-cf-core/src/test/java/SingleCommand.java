import com.github.alphahelix00.discordinator.commands.Command;
import com.github.alphahelix00.discordinator.commands.MainCommand;
import com.github.alphahelix00.discordinator.handler.AbstractCommandHandler;
import com.github.alphahelix00.discordinator.handler.CommandHandler;

import java.util.*;

/**
 * Created on:   6/16/2016
 * Author:       Kevin Xiao (github.com/alphahelix00)
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
