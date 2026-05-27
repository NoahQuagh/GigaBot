package bot.discordBot.commands;

import bot.discordBot.commands.Help.CommandHelpAll;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.*;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandHelp implements CommandExecutor {

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
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**AIDE** :")
                .addField("", "- Utiliser ```/doc``` pour lire la documentation.",false)
                .addField("", "* Utiliser ```/help all``` pour voir toute les commandes disponibles.",false)
                .setColor(Color.GREEN);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
