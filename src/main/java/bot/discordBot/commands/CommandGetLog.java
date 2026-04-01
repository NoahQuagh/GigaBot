package bot.discordBot.commands;

import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.File;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandGetLog implements CommandExecutor {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if (!event.getMessageAuthor().isBotOwner()) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.ACCEE_REFUSE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌ Accès refusé :")
                    .addField("Cette commande est réservée à l'administrateur.", "```Code: " + Code.ACCEE_REFUSE + "```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
            return;
        }

        // 2. Localiser le fichier
        File logFile = new File("logs.txt"); // Ou "logs/commands.log"

        if (logFile.exists()) {
            // 3. Envoyer le fichier sur Discord
            new MessageBuilder()
                    .addAttachment(logFile) // Javacord gère l'upload automatiquement
                    .send(event.getChannel());
        } else {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.AUCUNE_DONNEE_TROUVER);
        }
    }

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public String getDescription() {
        return "Obtenir les logs du bot  **(admin uniquement)**";
    }

    @Override
    public String getUsage() {
        return "!log";
    }

    public HashMap<Integer, String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        return variation;
    }
}
