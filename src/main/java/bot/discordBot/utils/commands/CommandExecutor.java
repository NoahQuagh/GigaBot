package bot.discordBot.utils.commands;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.HashMap;

public interface CommandExecutor {

    void run(MessageCreateEvent event, Command command,String[] args);
    String getName();
    String getDescription();
    String getUsage();
    HashMap<Integer, String> getVariation();
}
