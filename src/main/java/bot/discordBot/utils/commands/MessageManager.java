package bot.discordBot.utils.commands;

import bot.discordBot.Main;
import bot.discordBot.commands.*;
import bot.discordBot.utils.commands.datamanager.CommandLog;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Arrays;

public class MessageManager {

    private static CommandRegistry registry = new CommandRegistry();

    //creation de nouvelle commande ici puis cree ca class pour les actions qu'elle realisera
    static {
        registry.addCommand(new Command(
                "bot",
                new CommandBot(),
                "bot"
        ));
        registry.addCommand(new Command(
                "help",
                new CommandHelp(),
                "help"

        ));
        registry.addCommand(new Command(
                "valorant",
                new CommandValo(),
                "valorant"
        ));
        registry.addCommand(new Command(
                "man",
                new CommandMan(),
                "man"
        ));
        registry.addCommand(new Command(
                "log",
                new CommandGetLog(),
                "log"
        ));
        registry.addCommand(new Command(
                "premier",
                new CommandPremier(),
                "premier"
        ));
        registry.addCommand(new Command(
                "nouveauté",
                new CommandNew(),
                "nouveauté"
        ));
        registry.addCommand(new Command(
                "edtdev",
                new CommandEdtDev(),
                "edtdev"
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
            String[] args = split.length > 1
                    ? Arrays.copyOfRange(split, 1, split.length)
                    : new String[0];

            registry.getByAlias(commandName).ifPresent(cmd -> {
                // Seul changement : on wrappe dans CommandContext
                CommandContext ctx = new CommandContext(event);
                cmd.getExecutor().run(ctx, cmd, args);
            });
        }
    }
    public static void handleSlash(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String commandName = interaction.getCommandName();

        // Récupère le sous-argument (subcommand) s'il existe
        String[] args = interaction.getOptions().stream()
                .findFirst() // le subcommand
                .map(sub -> {
                    // Reconstruit args[] : ["-event", "date", "heure", "team"]
                    String subName = "-" + sub.getName();
                    String[] subArgs = sub.getOptions().stream()
                            .map(o -> o.getStringRepresentationValue().orElse(""))
                            .toArray(String[]::new);
                    // Merge : ["-event", ...subArgs]
                    String[] result = new String[subArgs.length + 1];
                    result[0] = subName;
                    System.arraycopy(subArgs, 0, result, 1, subArgs.length);
                    return result;
                })
                .orElse(new String[0]);

        registry.getByAlias(commandName).ifPresent(cmd -> {
            CommandContext ctx = new CommandContext(event);
            cmd.getExecutor().run(ctx, cmd, args);
        });
    }
}
