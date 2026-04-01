package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static bot.discordBot.utils.commands.Code.SYNTAXE_INCORRECTE;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierAddPlayerTeam extends CommandPremier {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args){
        if (args.length != 2) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!premier -addplayerteam ningen @nomJoueur```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
        try{
            String idJoueur = event.getMessage().getMentionedUsers().get(0).getIdAsString();
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ERREUR_API);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de crée la team premier.","@nomJoueur doit être une mention du joueur souhaité")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }
    }
}
