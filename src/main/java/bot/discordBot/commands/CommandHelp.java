package bot.discordBot.commands;

import bot.discordBot.commands.Help.CommandHelpAll;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandHelp implements CommandExecutor {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Aide utilisateur";
    }

    @Override
    public String getUsage() {
        return "/help <option>";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"Obtenir la liste des commandes disponible sur le bot_/help all");
        return variation;
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        String option = ctx.getOptionStringDirect("option").orElse("");

        if (option.equalsIgnoreCase("all")) {
            new CommandHelpAll().run(ctx, command, option);
        }else {
            sendDefaultHelp(ctx);
        }
    }

    private void sendDefaultHelp(CommandContext ctx) {
        MessageManager.getRegistry().getByAlias("man").ifPresentOrElse(cmd -> {
            CommandExecutor man = cmd.getExecutor();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("**AIDE** :")
                    .addField("", "- Utiliser ```/man <commande>``` " + man.getDescription() + ".")
                    .addField("", "* Utiliser ```/help all``` pour voir toute les commandes disponibles.")
                    .setColor(Color.GREEN);
            ctx.replyDeferred(embed);
        }, () -> {
        });
    }
}
