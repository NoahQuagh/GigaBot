package bot.discordBot.utils.commands;

import bot.discordBot.Main;
import bot.discordBot.commands.*;
import bot.discordBot.utils.commands.datamanager.CommandLog;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Arrays;

public class MessageManager {

    private static CommandRegistry registry = new CommandRegistry();

    //creation de nouvelle commande ici puis cree ca class pour les actions qu'elle realisera
    static {
        registry.addCommand(new Command(
                "ping",
                new CommandPing(),
                "ping"
        ));
        registry.addCommand(new Command(
                "bot",
                new CommandBot(),
                "bot"
        ));
        registry.addCommand(new Command(
                "paul",
                new CommandPaul(),
                "paul"
        ));
        registry.addCommand(new Command(
                "help",
                new CommandHelp(),
                "help","h?"

        ));
        registry.addCommand(new Command(
                "valo",
                new CommandValo(),
                "valo","v"
        ));
        registry.addCommand(new Command(
                "man",
                new CommandMan(),
                "man","m"
        ));
        registry.addCommand(new Command(
                "log",
                new CommandGetLog(),
                "log","l"
        ));
        registry.addCommand(new Command(
                "premier",
                new CommandPremier(),
                "premier","p"
        ));
        registry.addCommand(new Command(
                "new",
                new CommandNew(),
                "new"
        ));
    }

    private static final String PREFIX = Main.getConfigManager().getToml().getString("bot.prefix");

    public static CommandRegistry getRegistry() {
        return registry;
    }

    public static void create(MessageCreateEvent event) {
        String content = event.getMessageContent();

        if (content.startsWith(PREFIX)) {

            String[] split = content.split(" ");

            String commandName = split[0].substring(PREFIX.length());

            String[] args = (split.length > 1)
                    ? Arrays.copyOfRange(split, 1, split.length)
                    : new String[0];

            registry.getByAlias(commandName).ifPresent((cmd) -> {
                String name=event.getMessageAuthor().getDisplayName();
                CommandLog log = new CommandLog(content,name);
                //les arguments propres à l'exécuteur
                cmd.getExecutor().run(event, cmd, args);
            });
        }
    }
}
