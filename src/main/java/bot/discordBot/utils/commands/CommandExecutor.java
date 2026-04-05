package bot.discordBot.utils.commands;

import java.util.HashMap;

public interface CommandExecutor {

    void run(CommandContext ctx, Command command,String[] args);
    String getName();
    String getDescription();
    String getUsage();
    HashMap<Integer, String> getVariation();
}
