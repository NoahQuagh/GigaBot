package bot.discordBot.commands.Help;

import bot.discordBot.commands.CommandHelp;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.MessageManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CommandHelpAll extends CommandHelp {
    public void run(CommandContext ctx, Command command, String args) {
        if (args.equalsIgnoreCase("all")) {

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🛠️ Liste de toutes les commandes disponible")
                    .setDescription("Tips :  ``/man <commande>`` pour plus d'info sur les commandes")
                    .setColor(Color.GREEN);

            for (Command cmd : MessageManager.getRegistry().getCommands()) {
                embed.addField("","- "+cmd.getDescription()+"```/" + cmd.getId()+"```", false);
            }
            ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
