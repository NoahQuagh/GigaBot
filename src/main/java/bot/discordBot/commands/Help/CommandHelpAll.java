package bot.discordBot.commands.Help;

import bot.discordBot.commands.CommandHelp;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import bot.discordBot.utils.commands.MessageManager;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.stream.Collectors;

public class CommandHelpAll extends CommandHelp {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("-all")) {

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🛠️ Liste de toutes les commandes disponible")
                    .setDescription("Tips :  ``!man <commande>`` pour plus d'info sur les commandes")
                    .setColor(Color.GREEN);

            for (Command cmd : MessageManager.getRegistry().getCommands()) {
                embed.addField("","- "+cmd.getDescription()+"```!" + cmd.getId()+"```", false);
            }
            event.getChannel().sendMessage(embed);
        }
    }
}
