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

    @MainCommand(
            name = "disable",
            alias = "disable",
            desc = "disables a command"
    )
    public void disable(List<String> args) {
        if (args.size() > 0) {
            String mainCommand = args.get(0);
            args.remove(0);

            List<String> mainCommandArgs = AbstractCommandHandler.splitMessage(mainCommand);
            if (!mainCommandArgs.isEmpty()) {
                args.add(0, mainCommandArgs.get(1));
                Command command = AbstractCommandHandler.getCommand(mainCommandArgs.get(0), new ArrayList<>(args));
                if (command != null) {
                    AbstractCommandHandler.disableCommand(command);
                } else {
                    String message = "";
                    for (String s : args) {
                        message += s + " ";
                    }
                    System.out.println(mainCommandArgs.get(0) + message + "command not found!");
                }
            }
        }
    }

    @MainCommand(
            name = "enable",
            alias = "enable",
            desc = "enables a command"
    )
    public void enable(List<String> args) {
        if (args.size() > 0) {
            String mainCommand = args.get(0);
            args.remove(0);

            List<String> mainCommandArgs = AbstractCommandHandler.splitMessage(mainCommand);
            if (!mainCommandArgs.isEmpty()) {
                args.add(0, mainCommandArgs.get(1));
                Command command = AbstractCommandHandler.getCommand(mainCommandArgs.get(0), new ArrayList<>(args));
                if (command != null) {
                    AbstractCommandHandler.enableCommand(command);
                } else {
                    String message = "";
                    for (String s : args) {
                        message += s + " ";
                    }
                    System.out.println(mainCommandArgs.get(0) + message + "command not found!");
                }
            }
        }
    }
}
