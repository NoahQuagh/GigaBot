package bot.discordBot.commands;

import bot.discordBot.commands.Help.CommandHelpAll;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import bot.discordBot.utils.commands.MessageManager;
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
        return "!help <@option>";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"Obtenir la liste des commandes disponible sur le bot_!help -all");
        return variation;
    }

    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {

        if(args.length >0){
            if(args[0].equalsIgnoreCase("-all")){
                new CommandHelpAll().run(event,command,args);
                return;
            }else{
                String name=event.getMessageAuthor().getDisplayName();
                writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("⚠️   Attention :")
                        .setDescription("Syntaxe incorrecte !")
                        .addField("syntaxe correcte:","```!help -all```")
                        .setColor(Color.orange);
                event.getChannel().sendMessage(embed);
                return;
            }
        }else{
            sendDefaultHelp(event);
        }
    }

    protected void sendDefaultHelp(MessageCreateEvent event) {
        MessageManager.getRegistry().getByAlias("man").ifPresentOrElse(cmd -> {
            CommandExecutor man = cmd.getExecutor();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("**AIDE** :")
                    .addField("", "- Utiliser ```!man <commande>``` " + man.getDescription() + ".")
                    .addField("", "* Utiliser ```!help -all``` pour voir toute les commandes disponibles.")
                    .setColor(Color.GREEN);
            event.getChannel().sendMessage(embed);
        }, () -> {
        });
    }
}
