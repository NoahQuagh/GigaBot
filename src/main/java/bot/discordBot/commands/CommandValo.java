package bot.discordBot.commands;

import bot.discordBot.commands.Valo.CommandValoRank;
import bot.discordBot.commands.Valo.CommandValoStats;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;


public class CommandValo implements CommandExecutor {
    @Override
    public String getName() {
        return "valo";
    }

    @Override
    public String getDescription() {return "Commande relative a valorant";}

    @Override
    public String getUsage() {
        return "!valo <@option> <@argument>";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"Obtenir le rang d'un(e) joueur/joueuse_!valo -rank <pseudo>#<tag>");
        variation.put(1,"Obtenir les stats d'un(e) joueur/joueuse_!valo -stats <pseudo>#<tag>");
        return variation;
    }


    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {

        if(args.length >0){
            if(args[0].equalsIgnoreCase("-rank")){
                new CommandValoRank().run(event,command,args);
                return;
            }else if(args[0].equalsIgnoreCase("-stats")){
                new CommandValoStats().run(event,command,args);
                return;
            }
        }else{
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Option manquante :","```!"+getName()+" <--here```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
    }
}
