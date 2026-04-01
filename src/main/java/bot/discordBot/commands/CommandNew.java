package bot.discordBot.commands;

import bot.discordBot.commands.Valo.CommandValoRank;
import bot.discordBot.commands.Valo.CommandValoStats;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Bug;
import bot.discordBot.utils.commands.datamanager.DataStructure.Nouveaute;
import bot.discordBot.utils.commands.datamanager.DataStructure.StrucNew;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static bot.discordBot.Main.version;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandNew implements CommandExecutor {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if(args.length == 0){
            ArrayList<StrucNew> toutesLesNews = DataManager.loadNew();

            if(toutesLesNews.isEmpty()){
                event.getChannel().sendMessage("Aucune nouveauté enregistrée.");
                return;
            }

            // ON RÉCUPÈRE LA DERNIÈRE VERSION (L'index 0)
            StrucNew currentVersion = toutesLesNews.get(0);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("**Nouveautés :** v " + currentVersion.getVersion()) // Supposant que tu as getVersion()
                    .setColor(Color.green);

            // 1. Les Nouveautés
            if(currentVersion.getNouveau() != null && !currentVersion.getNouveau().isEmpty()){
                embed.addField("\u200B", "\u200B"); // Espace vide propre
                for(Nouveaute n : currentVersion.getNouveau()){
                    embed.addField("**" + n.getNomNouveaute() + "**", "- " + n.getDescription());
                }
            }

            // 2. Les Bugs
            if(currentVersion.getBug() != null && !currentVersion.getBug().isEmpty()){
                embed.addField("\u200B", "\u200B");
                embed.addField("🪲 **Bug et Fixes :**", "\u200B");
                for(Bug b : currentVersion.getBug()){
                    embed.addField(b.getNomBug() + " : ",
                            " - ```" + b.getDescription() + "```\n" +
                                    " __- Résolution :__ " + b.getResolution());
                }
            }

            event.getChannel().sendMessage(embed);
        }else{
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Option en trop :","```!"+getName()+" <--here```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
    }

    @Override
    public String getName() {
        return "new";
    }

    @Override
    public String getDescription() {
        return "Affiche toutes les nouveautées de la nouvelle version du bot";
    }

    @Override
    public String getUsage() {
        return "!new";
    }

    @Override
    public HashMap<Integer, String> getVariation() {
        return null;
    }
}
