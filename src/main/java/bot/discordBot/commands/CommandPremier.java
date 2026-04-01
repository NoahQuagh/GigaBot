package bot.discordBot.commands;

import bot.discordBot.commands.Premier.*;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremier implements CommandExecutor {

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if(args.length >0){
            if(args[0].equalsIgnoreCase("-rank")){
                new CommandPremierRank().run(event,command,args);
                return;
            }else if(args[0].equalsIgnoreCase("-stats")){
                //new CommandPremierStats().run(event,command,args);
                return;
            }else if(args[0].equalsIgnoreCase("-event")) {
                new CommandPremierEvent().run(event, command, args);
                return;
            }else if(args[0].equalsIgnoreCase("-addplayerteam")) {
                new CommandPremierAddPlayerTeam().run(event, command, args);
                return;
            }else if(args[0].equalsIgnoreCase("-addteam")) {
                new CommandPremierAddTeam().run(event, command, args);
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

    @Override
    public String getName() {
        return "premier";
    }

    @Override
    public String getDescription() {
        return "Commande relative au mode premier de Valorant";
    }

    @Override
    public String getUsage() {
        return "!premier <@option> <@argument>";
    }

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"Obtenir le rang de la team Premier_(indisponible)");
        variation.put(1,"Obtenir les stats de la team Premier_(inexistant)");
        variation.put(2,"Liste des noms des teams Premier_(inexistant)");
        variation.put(3,"Ajouté une team a la liste des teams Premier_(inexistant)");
        variation.put(4,"Retiré une team a la liste des teams Premier_(inexistant)");
        variation.put(5,"Crée un événement de game Premier_!premier -event dd:mm:yyyy hh:mm nomTeam");
        return variation;
    }
}
