package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;

import static bot.discordBot.utils.commands.Code.ECHEC;
import static bot.discordBot.utils.commands.Code.SYNTAXE_INCORRECTE;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierAddTeam extends CommandPremier {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args){
        if (args.length != 2) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!premier -addteam ningen```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
        try{
            //verifier si on a deja une team premier

            String idCapitaine = event.getMessageAuthor().getIdAsString();

            ArrayList<Equipe> equipe = new ArrayList<>();
            ArrayList<String> joueur = new ArrayList<>();

            joueur.add(idCapitaine);
            equipe.add(new Equipe(args[1],idCapitaine,joueur));

            DataManager.saveEquipes(equipe);

            writeLogFile("logs.txt","new team created");
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅   Création réussi :")
                    .setDescription("nouvelle team crée : **"+args[1].toUpperCase()+"**")
                    .setColor(Color.GREEN);
            event.getChannel().sendMessage(embed);

        }catch (IndexOutOfBoundsException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de crée la team premier.","@nomCapitaine doit être une mention du joueur souhaité en tant que capitaine")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de crée la team premier.","```Code : "+ECHEC+"```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }

    }
}
